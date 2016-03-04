package com.ccreanga.bitbucketapi.example;


import org.springframework.stereotype.Component;

@Component
public interface Serializer {

    byte[] serialize(Object object);
    Object deserialize(byte[] data);

}
