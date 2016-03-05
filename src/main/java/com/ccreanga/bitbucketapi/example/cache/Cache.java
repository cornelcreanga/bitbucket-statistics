package com.ccreanga.bitbucketapi.example.cache;


public interface Cache<K, V> {

    void put(K key, V value);

    void putAndCommit(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    V remove(K key);

    V removeAndCommit(K key, V value);

    boolean isEmpty();

    int size();

    void clear();

    void commit();

    void rollback();

    void close();

}
