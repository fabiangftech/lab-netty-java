package cl.guaman.labhttp2server.factory;

public interface Factory<T, U> {

    U create(T input);

    default U create() {
        return create(null);
    }
}
