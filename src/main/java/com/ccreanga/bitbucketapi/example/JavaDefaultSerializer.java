package com.ccreanga.bitbucketapi.example;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.springframework.stereotype.Component;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Component(value = "defaultSerializer")
public class JavaDefaultSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        try {
            ByteOutputStream byteStream = new ByteOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteStream);
            out.writeObject(object);
            out.close();
            return byteStream.getBytes();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(byte[] data) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteInputStream(data,data.length));
            return in.readObject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
