package com.ccreanga.bitbucketapi.example.cache;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MapDbCache<K,V> implements Cache<K,V> {

    private HTreeMap<K,V> treeMap;
    private DB db;

    public MapDbCache(String cachePath,String cacheName,int cacheExpiration) {
        db =  DBMaker.newFileDB(new File(cachePath)).make();
        DB.HTreeMapMaker mapMaker = db.createHashMap(cacheName);
        mapMaker.expireAfterWrite(cacheExpiration, TimeUnit.SECONDS);
        treeMap = mapMaker.makeOrGet();
    }

    @Override
    public void put(K key, V value) {
        treeMap.put(key,value);
    }

    @Override
    public void putAndCommit(K key, V value) {
        put(key,value);
        db.commit();
    }

    @Override
    public V get(K key) {
        return treeMap.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return treeMap.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return treeMap.remove(key);
    }

    @Override
    public V removeAndCommit(K key, V value) {
        V removed = treeMap.remove(key);
        db.commit();
        return removed;
    }

    @Override
    public boolean isEmpty() {
        return treeMap.isEmpty();
    }

    @Override
    public int size() {
        return treeMap.size();
    }

    @Override
    public void clear() {
        treeMap.clear();
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public void rollback() {
        db.rollback();
    }

    @Override
    public void close() {
        db.close();
    }
}
