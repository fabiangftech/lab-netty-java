#!/bin/bash

# MongoDB Docker Container Script
# This script stops and removes existing MongoDB container, then creates a new one from scratch

CONTAINER_NAME="lab-mongodb"
IMAGE_NAME="mongo:latest"
PORT="27017"
DATA_VOLUME="mongodb-data"

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

echo "Creating new MongoDB container from scratch..."

# Create and start new MongoDB container
docker run -d \
    --name ${CONTAINER_NAME} \
    -p ${PORT}:27017 \
    -v ${DATA_VOLUME}:/data/db \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
    ${IMAGE_NAME}

if [ $? -eq 0 ]; then
    echo "MongoDB container created successfully!"
    echo "Container name: ${CONTAINER_NAME}"
    echo "Port: ${PORT}"
    echo "Connection string: mongodb://admin:admin123@localhost:${PORT}"
    echo ""
    echo "To view logs: docker logs -f ${CONTAINER_NAME}"
    echo "To stop: docker stop ${CONTAINER_NAME}"
else
    echo "Failed to create MongoDB container"
    exit 1
fi

