package com.ccreanga.bitbucketapi.example;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "kryoSerializer")
public class KryoSerializer implements Serializer {

    @Autowired
    KryoPool kryoPool;

    public byte[] serialize(Object object){
        Kryo kryo = kryoPool.borrow();
        Output output = new Output(32*1024);
        kryo.writeClassAndObject(output,object);
        output.close();
        kryoPool.release(kryo);
        return output.toBytes();
    }

    public Object deserialize(byte[] data){
        Kryo kryo = kryoPool.borrow();
        Object result =  kryo.readClassAndObject(new Input(data));
        kryoPool.release(kryo);
        return result;
    }

}
