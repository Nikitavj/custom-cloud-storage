package org.nikita.spingproject.filestorage.s3manager;

import java.util.List;

public interface S3Manager<T> {
    void add(T object);
    void remove(String path);
    T get(String path);
    List<T> getAll();
}
