package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.UserType;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestOpenedActivity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TestMapDb {



//    public static void main(String[] args) {
//        DB db = DBMaker.newFileDB(new File("/tmp/testdb"))
//                .closeOnJvmShutdown()
//                .make();
//
//
//
//        DB.HTreeMapMaker mapMaker = db.createHashMap("bitBucket");
//        mapMaker.expireAfterWrite(2, TimeUnit.HOURS);
//        HTreeMap<String,byte[]> map = mapMaker.makeOrGet();
//
//        Set<PullRequestActivity> activitySet = new HashSet<>();
//        activitySet.add(new PullRequestOpenedActivity(21L,new Date(),new User(1,"name1","email1","display1",true,"slug", UserType.NORMAL)));
//        activitySet.add(new PullRequestOpenedActivity(51L,new Date(),new User(1,"name2","email2","display2",true,"slug", UserType.NORMAL)));
//        activitySet.add(new PullRequestOpenedActivity(81L,new Date(),new User(1,"name3","email3","display3",true,"slug", UserType.NORMAL)));
//
//        Kryo kryo = new Kryo();
//        Output output = new Output(4096);
//
//
//        kryo.register(PullRequestOpenedActivity.class);
//
//
//        kryo.writeClassAndObject(output,activitySet);
//        output.close();
//        System.out.println(output.toBytes().length);
//
//        byte[] data = output.toBytes();
//        Input input = new Input(data);
//
//        Object o = kryo.readClassAndObject(input);
//
//
//
//        map.put("cucu",output.toBytes());
//        db.commit();
//        db.close();
//    }

}
