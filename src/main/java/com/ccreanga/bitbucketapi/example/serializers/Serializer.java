package com.ccreanga.bitbucketapi.example.serializers;

public interface Serializer {

    byte[] serialize(Object object);
    Object deserialize(byte[] data);

}
