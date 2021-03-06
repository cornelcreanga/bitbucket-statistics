package com.ccreanga.bitbucketapi.example.serializers.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public class ImmutableMapSerializer extends Serializer<ImmutableMap<Object, ? extends Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = true;
    private static final boolean IMMUTABLE = true;

    public ImmutableMapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ImmutableMap<Object, ? extends Object> immutableMap) {
        kryo.writeObject(output, Maps.newHashMap(immutableMap));
    }

    @Override
    public ImmutableMap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableMap<Object, ? extends Object>> type) {
        Map map = kryo.readObject(input, HashMap.class);
        return ImmutableMap.copyOf(map);
    }

    public static void registerSerializers(final Kryo kryo) {

        final ImmutableMapSerializer serializer = new ImmutableMapSerializer();

        kryo.register(ImmutableMap.class, serializer);
        kryo.register(ImmutableMap.of().getClass(), serializer);

        Object o1 = new Object();
        Object o2 = new Object();

        kryo.register(ImmutableMap.of(o1, o1).getClass(), serializer);
        kryo.register(ImmutableMap.of(o1, o1, o2, o2).getClass(), serializer);

        Map<DummyEnum,Object> enumMap = new EnumMap<DummyEnum, Object>(DummyEnum.class);
        for (DummyEnum e : DummyEnum.values()) {
            enumMap.put(e, o1);
        }

        kryo.register(ImmutableMap.copyOf(enumMap).getClass(), serializer);
    }

    private enum DummyEnum {
        VALUE1,
        VALUE2
    }
}
