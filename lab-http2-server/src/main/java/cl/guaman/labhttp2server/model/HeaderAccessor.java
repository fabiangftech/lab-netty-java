package cl.guaman.labhttp2server.model;

public interface HeaderAccessor {
    String get(String key);
    boolean contains(String key);
}
