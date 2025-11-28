#!/bin/bash

# MongoDB Docker Container Script
# This script stops and removes existing MongoDB container, then creates a new one from scratch
# MongoDB is configured as a replica set to support Change Streams

CONTAINER_NAME="lab-mongodb"
IMAGE_NAME="mongo:latest"
PORT="27017"
DATA_VOLUME="mongodb-data"
REPLICA_SET_NAME="rs0"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEYFILE_PATH="${SCRIPT_DIR}/mongodb-keyfile"
KEYFILE_CONTAINER_PATH="/data/configdb/replica-set.key"

echo "Stopping and removing existing MongoDB container if it exists..."

# Stop container if running
if [ "$(docker ps -aq -f name=${CONTAINER_NAME})" ]; then
    echo "Stopping container ${CONTAINER_NAME}..."
    docker stop ${CONTAINER_NAME}
    
    echo "Removing container ${CONTAINER_NAME}..."
    docker rm ${CONTAINER_NAME}
fi

# Remove volume if exists (optional - uncomment if you want to remove data volume too)
# if [ "$(docker volume ls -q -f name=${DATA_VOLUME})" ]; then
#     echo "Removing volume ${DATA_VOLUME}..."
#     docker volume rm ${DATA_VOLUME}
# fi

echo "Generating MongoDB replica set keyFile..."

# Generate keyFile if it doesn't exist
if [ ! -f "${KEYFILE_PATH}" ]; then
    openssl rand -base64 756 > "${KEYFILE_PATH}"
    chmod 600 "${KEYFILE_PATH}"
    echo "KeyFile generated at ${KEYFILE_PATH}"
else
    echo "KeyFile already exists at ${KEYFILE_PATH}"
fi

echo "Creating new MongoDB container from scratch with replica set configuration..."

# Create and start new MongoDB container with replica set enabled
docker run -d \
    --name ${CONTAINER_NAME} \
    -p ${PORT}:27017 \
    -v ${DATA_VOLUME}:/data/db \
    -v ${KEYFILE_PATH}:${KEYFILE_CONTAINER_PATH} \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
    ${IMAGE_NAME} --replSet ${REPLICA_SET_NAME} --keyFile ${KEYFILE_CONTAINER_PATH}

if [ $? -eq 0 ]; then
    echo "MongoDB container created successfully!"
    echo "Waiting for MongoDB to be ready..."
    
    # Wait for MongoDB to be ready (max 30 seconds)
    MAX_WAIT=30
    WAIT_TIME=0
    while [ ${WAIT_TIME} -lt ${MAX_WAIT} ]; do
        if docker exec ${CONTAINER_NAME} mongosh --quiet --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
            echo "MongoDB is ready!"
            break
        fi
        sleep 2
        WAIT_TIME=$((WAIT_TIME + 2))
        echo -n "."
    done
    echo ""
    
    # Initialize replica set
    echo "Initializing replica set '${REPLICA_SET_NAME}'..."
    # Use localhost since mongosh runs inside the container
    docker exec ${CONTAINER_NAME} mongosh -u admin -p admin123 --authenticationDatabase admin --quiet --eval \
        "rs.initiate({_id: '${REPLICA_SET_NAME}', members: [{_id: 0, host: 'localhost:27017'}]})"
    
    if [ $? -eq 0 ]; then
        echo "Replica set initialized successfully!"
        echo "Waiting for replica set to be ready..."
        sleep 5
    else
        echo "Warning: Failed to initialize replica set. You may need to initialize it manually."
    fi
    
    echo ""
    echo "Container name: ${CONTAINER_NAME}"
    echo "Port: ${PORT}"
    echo "Replica Set: ${REPLICA_SET_NAME}"
    echo "Connection string: mongodb://admin:admin123@localhost:${PORT}"
    echo ""
    echo "To view logs: docker logs -f ${CONTAINER_NAME}"
    echo "To stop: docker stop ${CONTAINER_NAME}"
    echo "To check replica set status: docker exec ${CONTAINER_NAME} mongosh -u admin -p admin123 --authenticationDatabase admin --eval 'rs.status()'"
else
    echo "Failed to create MongoDB container"
    exit 1
fi

