package org.nikita.spingproject.filestorage.s3manager;

public interface S3Manager<T> {
    void add(T object);
    void remove(String path);
    T get(String path);
}
