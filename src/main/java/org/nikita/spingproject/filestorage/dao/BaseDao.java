package org.nikita.spingproject.filestorage.dao;

import java.util.List;

public interface BaseDao<T> {
    void add(T object);
    void remove(String path);
    T get(String path);
    List<T> getAll();
}
