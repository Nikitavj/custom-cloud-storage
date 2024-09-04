package org.nikita.spingproject.filestorage.commons;

public interface BaseDao<T> {
    void add(T object);
    void remove(String path);
    T get(String path);
}
