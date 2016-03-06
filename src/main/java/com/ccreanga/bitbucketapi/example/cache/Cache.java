package com.ccreanga.bitbucketapi.example.cache;


public interface Cache {

    void put(String key, byte[] value,int expiration);

    byte[] get(String key);

    boolean containsKey(String key);

    void remove(String key);

    boolean isEmpty();

    long size();

    void clear();

    void commit();

    void rollback();

    void close();

}
