package com.ccreanga.bitbucketapi.example.serializers.jdefault;


import com.ccreanga.bitbucketapi.example.serializers.Serializer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Component(value = "defaultSerializer")
public class JavaDefaultSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        try {

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteStream);
            out.writeObject(object);
            out.close();
            return byteStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(byte[] data) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data,0,data.length));
            return in.readObject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
