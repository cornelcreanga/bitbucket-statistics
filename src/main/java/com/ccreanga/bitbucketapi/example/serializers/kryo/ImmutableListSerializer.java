package com.ccreanga.bitbucketapi.example.serializers.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class ImmutableListSerializer extends Serializer<ImmutableList<Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;
    private static final boolean IMMUTABLE = true;

    public ImmutableListSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ImmutableList<Object> object) {
        output.writeInt(object.size(), true);
        for (Object elm : object) {
            kryo.writeClassAndObject(output, elm);
        }
    }

    @Override
    public ImmutableList<Object> read(Kryo kryo, Input input, Class<ImmutableList<Object>> type) {
        final int size = input.readInt(true);
        final Object[] list = new Object[size];
        for (int i = 0; i < size; ++i) {
            list[i] = kryo.readClassAndObject(input);
        }
        return ImmutableList.copyOf(list);
    }

    public static void registerSerializers(final Kryo kryo) {


        final ImmutableListSerializer serializer = new ImmutableListSerializer();

        kryo.register(ImmutableList.class, serializer);
        kryo.register(ImmutableList.of().getClass(), serializer);
        kryo.register(ImmutableList.of(1).getClass(), serializer);
        kryo.register(ImmutableList.of(1,2,3).subList(1, 2).getClass(), serializer);
        kryo.register(ImmutableList.of().reverse().getClass(), serializer);

        kryo.register(Lists.charactersOf("kryo").getClass(), serializer);

        Table<Integer,Integer,Integer> baseTable = HashBasedTable.create();
        baseTable.put(1, 2, 3);
        baseTable.put(4, 5, 6);
        Table<Integer, Integer, Integer> table = ImmutableTable.copyOf(baseTable);
        kryo.register(table.values().getClass(), serializer);

    }
}
