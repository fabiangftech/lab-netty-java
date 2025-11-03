package cl.guaman.labhttp2server.model;

import io.netty.handler.codec.http.HttpMethod;

import java.util.*;

public class RadixRouter<T> {

    public static final class RouteMatch<T> {
        public final T handler;
        public final Map<String, String> params;
        public final String matchedPath;

        RouteMatch(T handler, Map<String, String> params, String matchedPath) {
            this.handler = handler;
            this.params = Collections.unmodifiableMap(params);
            this.matchedPath = matchedPath;
        }
    }

    private static final class Node<T> {
        // Static literal children: "users" -> node
        final Map<String, Node<T>> staticChildren = new HashMap<>();

        // Param child like ":id" or "{id}"
        Node<T> paramChild;
        String paramName;

        // Catch-all child like "*filepath"
        Node<T> wildcardChild;
        String wildcardName;

        T handler;
        String templatePath;
    }

    private final Map<HttpMethod, Node<T>> roots = new EnumMap<>(HttpMethod.class);

    public RadixRouter() {
        roots.put(HttpMethod.GET, new Node<>());
        roots.put(HttpMethod.POST, new Node<>());
        roots.put(HttpMethod.PUT, new Node<>());
        roots.put(HttpMethod.PATCH, new Node<>());
        roots.put(HttpMethod.DELETE, new Node<>());
        roots.put(HttpMethod.HEAD, new Node<>());
    }

    public void add(HttpMethod method, String pathTemplate, T handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(pathTemplate, "pathTemplate");
        Objects.requireNonNull(handler, "handler");

        String norm = normalize(pathTemplate);
        List<Segment> segs = parseTemplate(norm);

        Node<T> cur = roots.get(method);
        for (int i = 0; i < segs.size(); i++) {
            Segment s = segs.get(i);
            switch (s.type) {
                case STATIC -> {
                    cur = cur.staticChildren.computeIfAbsent(s.literal, k -> new Node<>());
                }
                case PARAM -> {
                    if (cur.paramChild == null) {
                        cur.paramChild = new Node<>();
                        cur.paramChild.paramName = s.name;
                    } else if (!cur.paramChild.paramName.equals(s.name)) {
                        // Two param names at same position are fine for matching, but we keep the first name stored.
                        // No structural conflict.
                    }
                    cur = cur.paramChild;
                }
                case WILDCARD -> {
                    if (i != segs.size() - 1) {
                        throw new IllegalArgumentException("Wildcard * must be the last segment: " + pathTemplate);
                    }
                    if (cur.wildcardChild == null) {
                        cur.wildcardChild = new Node<>();
                        cur.wildcardChild.wildcardName = s.name;
                    }
                    cur = cur.wildcardChild;
                }
            }
        }

        if (cur.handler != null) {
            throw new IllegalStateException("Route already exists for " + method + " " + pathTemplate);
        }
        cur.handler = handler;
        cur.templatePath = pathTemplate;
    }

    public RouteMatch<T> match(HttpMethod method, String path) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(path, "path");

        String norm = normalize(path);
        List<String> parts = split(norm);
        Node<T> root = roots.get(method);

        Map<String, String> params = new LinkedHashMap<>();
        Node<T> cur = root;

        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);

            // 1) Try static match first (fast path)
            Node<T> next = cur.staticChildren.get(part);
            if (next != null) {
                cur = next;
                continue;
            }

            // 2) Try param match (single segment)
            if (cur.paramChild != null) {
                params.put(cur.paramChild.paramName, part);
                cur = cur.paramChild;
                continue;
            }

            // 3) Try wildcard (catch-all)
            if (cur.wildcardChild != null) {
                String rest = join(parts, i); // include current and remaining
                params.put(cur.wildcardChild.wildcardName, rest);
                cur = cur.wildcardChild;
                // Wildcard consumes all, end loop
                i = parts.size(); // will exit
                break;
            }

            // No match
            return null;
        }

        if (cur.handler != null) {
            return new RouteMatch<>(cur.handler, params, cur.templatePath);
        }

        // Edge case: path ended but node has only wildcard child that can match empty?
        if (cur.wildcardChild != null && cur.wildcardChild.handler != null) {
            // empty match for wildcard
            params.putIfAbsent(cur.wildcardChild.wildcardName, "");
            return new RouteMatch<>(cur.wildcardChild.handler, params, cur.wildcardChild.templatePath);
        }

        return null;
    }

    private static String normalize(String path) {
        if (path.isEmpty()) return "/";
        String p = path;
        if (p.charAt(0) != '/') p = "/" + p;
        // remove trailing slash except the root "/"
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    private static List<String> split(String normalizedPath) {
        if ("/".equals(normalizedPath)) return List.of();
        String[] arr = normalizedPath.substring(1).split("/");
        List<String> parts = new ArrayList<>(arr.length);
        for (String a : arr) {
            if (!a.isEmpty()) parts.add(a);
        }
        return parts;
    }

    private static String join(List<String> parts, int fromInclusive) {
        StringBuilder sb = new StringBuilder();
        for (int i = fromInclusive; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if (i + 1 < parts.size()) sb.append('/');
        }
        return sb.toString();
    }

    private enum SegType {STATIC, PARAM, WILDCARD}

    private static final class Segment {
        final SegType type;
        final String literal;
        final String name;

        Segment(SegType t, String literal, String name) {
            this.type = t;
            this.literal = literal;
            this.name = name;
        }
    }

    private static List<Segment> parseTemplate(String normalizedTemplate) {
        List<String> parts = split(normalizedTemplate);
        List<Segment> out = new ArrayList<>(parts.size());
        for (String p : parts) {
            if (p.startsWith(":")) {
                String name = p.substring(1);
                validateName(name);
                out.add(new Segment(SegType.PARAM, null, name));
            } else if (p.startsWith("{") && p.endsWith("}") && p.length() > 2) {
                String name = p.substring(1, p.length() - 1);
                validateName(name);
                out.add(new Segment(SegType.PARAM, null, name));
            } else if (p.startsWith("*")) {
                String name = p.substring(1);
                if (name.isEmpty()) throw new IllegalArgumentException("Wildcard must have a name, e.g. *filepath");
                validateName(name);
                out.add(new Segment(SegType.WILDCARD, null, name));
            } else {
                if (p.indexOf('{') >= 0 || p.indexOf('}') >= 0)
                    throw new IllegalArgumentException("Invalid segment: " + p);
                out.add(new Segment(SegType.STATIC, p, null));
            }
        }
        return out;
    }

    private static void validateName(String name) {
        if (!name.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid param name: " + name);
        }
    }
}

