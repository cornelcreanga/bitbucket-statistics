package com.ccreanga.bitbucketapi.example.cache;

import com.google.common.primitives.Longs;
import com.sleepycat.je.*;

import java.io.File;

public class BerkeleyDbCache implements Cache {

    private Environment environment;
    private Database database;

    public BerkeleyDbCache(String cachePath, String cacheName) {
        File folder = new File(cachePath);
        if ((!folder.exists()) || (!folder.isDirectory()))
            throw new RuntimeException(cachePath+" should be an existing folder");
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        try {
            environment = new Environment(new File(cachePath), envConfig);
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            database = environment.openDatabase(null, cacheName, dbConfig);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void put(String key, byte[] value, int expiration) {

        //first 8 bytes are the expiration timestamp
        byte[] data = new byte[value.length+8];
        System.arraycopy(Longs.toByteArray(System.currentTimeMillis()+expiration*1000),0,data,0,8);
        System.arraycopy(value,0,data,8,value.length);

        DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry(data);

        try {
            database.put(null, keyEntry, valueEntry);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] parseKeyAndRemoveExpired(String key,DatabaseEntry value){
        byte[] r = value.getData();
        long timestamp = Longs.fromBytes(r[0],r[1],r[2],r[3],r[4],r[5],r[6],r[7]);
        if (System.currentTimeMillis() > (timestamp) ) {
            remove(key);
            return null;
        }
        byte[] toReturn = new byte[r.length-8];
        System.arraycopy(r,8,toReturn,0,r.length-8);
        return toReturn;
    }

    @Override
    public byte[] get(String key) {
        DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
        DatabaseEntry readValue = new DatabaseEntry();
        try {
            if (database.get(null, keyEntry, readValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                return parseKeyAndRemoveExpired(key,readValue);
            } else {
                return null;
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean containsKey(String key) {
        DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
        DatabaseEntry readValue = new DatabaseEntry();
        try {
            if (database.get(null, keyEntry, readValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                return parseKeyAndRemoveExpired(key,readValue)!=null;
            } else {
                return false;
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(String key) {
        try {
            DatabaseEntry theKey = new DatabaseEntry(key.getBytes());
            database.delete(null, theKey);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        try {
        return database.count()==0;
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public long size() {
        try {
            return database.count();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {
        try {
            database.close();
            environment.close();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

}
