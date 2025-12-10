package cl.guaman.labhttp2server.model;

import cl.guaman.labhttp2server.handler.ControllerHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.AsciiString;

import java.util.HashMap;
import java.util.Map;

public class InternalRouter {

    private static final Character FORWARD_SLASH = '/';
    private static final String FORWARD_SLASH_STR = "/";
    private static final char WILDCARD = '*';
    private static final String NAME_WILDCARD = "wildcard";
    private static final char COLON = ':';

    public record Match(HttpMethod method, ControllerHandler handler, Map<String, String> pathParams) {

    }

    private static final class Node {
        private final Map<AsciiString, Node> staticChildren = new HashMap<>();
        private Node paramChild;
        private String paramName;
        private Node wildcardChild;
        private String wildcardName;

        private final Map<HttpMethod, ControllerHandler> handlersByMethod = new HashMap<>();
        private final Map<HttpMethod, Integer> prioritiesByMethod = new HashMap<>();
    }

    private final Node root = new Node();

    public void add(HttpMethod method, String path, ControllerHandler handler) {
        add(method, path, handler, 0);
    }

    public void add(HttpMethod method, String path, ControllerHandler handler, int priority) {
        if (method == null) {
            throw new IllegalArgumentException("HttpMethod cannot be null");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        String normalized = normalizePath(path);
        String[] segments = normalized.split(FORWARD_SLASH_STR);

        Node node = root;

        for (String seg : segments) {
            if (seg.isEmpty()) continue;

            char c = seg.charAt(0);
            if (c == COLON) {
                String name = seg.substring(1);
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Param name cannot be empty in path: " + path);
                }
                if (node.paramChild == null) {
                    node.paramChild = new Node();
                    node.paramChild.paramName = name;
                } else if (!name.equals(node.paramChild.paramName)) {
                    throw new IllegalStateException(
                            "Conflicting param at same level: existing=" + node.paramChild.paramName + ", new=" + name
                    );
                }
                node = node.paramChild;
            } else if (c == WILDCARD) {

                String name = seg.length() > 1 ? seg.substring(1) : NAME_WILDCARD;
                if (node.wildcardChild == null) {
                    node.wildcardChild = new Node();
                    node.wildcardChild.wildcardName = name;
                } else if (!name.equals(node.wildcardChild.wildcardName)) {
                    throw new IllegalStateException(
                            "Conflicting wildcard at same level: existing=" + node.wildcardChild.wildcardName
                            + ", new=" + name
                    );
                }
                node = node.wildcardChild;
                if (!isLastNonEmptySegment(segments, seg)) {
                    throw new IllegalArgumentException("Wildcard segment must be the last one: " + path);
                }
            } else {
                AsciiString key = AsciiString.cached(seg);
                node = node.staticChildren.computeIfAbsent(key, k -> new Node());
            }
        }

        ControllerHandler existing = node.handlersByMethod.get(method);
        Integer existingPrio = node.prioritiesByMethod.get(method);
        if (existing != null && existingPrio != null && existingPrio >= priority) {
            throw new IllegalStateException(
                    "Route already registered for method " + method + " with same or higher priority: " + path
            );
        }

        node.handlersByMethod.put(method, handler);
        node.prioritiesByMethod.put(method, priority);
    }

    private static boolean isLastNonEmptySegment(String[] segments, String current) {
        int lastIdx = -1;
        for (int i = 0; i < segments.length; i++) {
            if (!segments[i].isEmpty()) lastIdx = i;
        }
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equals(current)) return i == lastIdx;
        }
        return true;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return FORWARD_SLASH_STR;
        if (path.charAt(0) != FORWARD_SLASH) {
            path = FORWARD_SLASH_STR + path;
        }
        return path;
    }

    public Match match(HttpMethod method, String path) {
        if (method == null) {
            throw new IllegalArgumentException("HttpMethod cannot be null");
        }

        String normalized = normalizePath(path);
        int len = normalized.length();
        int offset = 0;

        if (offset < len && normalized.charAt(offset) == FORWARD_SLASH) {
            offset++;
        }

        Node node = root;
        Map<String, String> params = new HashMap<>();

        while (true) {
            if (offset >= len) {
                ControllerHandler handler = node.handlersByMethod.get(method);
                if (handler != null) {
                    return new Match(method, handler, params);
                }
                return null;
            }

            int nextSlash = offset;
            while (nextSlash < len && normalized.charAt(nextSlash) != FORWARD_SLASH) {
                nextSlash++;
            }

            String segment = normalized.substring(offset, nextSlash);

            AsciiString key = AsciiString.cached(segment);
            Node child = node.staticChildren.get(key);
            if (child != null) {
                node = child;
            } else if (node.paramChild != null) {
                params.put(node.paramChild.paramName, segment);
                node = node.paramChild;
            } else if (node.wildcardChild != null) {
                String rest = normalized.substring(offset);
                params.put(node.wildcardChild.wildcardName, rest.startsWith(FORWARD_SLASH_STR) ? rest.substring(1) : rest);
                ControllerHandler handler = node.wildcardChild.handlersByMethod.get(method);
                if (handler != null) {
                    return new Match(method, handler, params);
                }
                return null;
            } else {
                return null;
            }

            offset = nextSlash;
            if (offset < len && normalized.charAt(offset) == FORWARD_SLASH) {
                offset++;
            }
        }
    }
}
