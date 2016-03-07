package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucket.rest.client.model.Commit;
import com.ccreanga.bitbucket.rest.client.model.MinimalCommit;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.UserType;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestOpenedActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestRescopedActivity;
import com.ccreanga.bitbucketapi.example.cache.BerkeleyDbCache;
import com.ccreanga.bitbucketapi.example.cache.Cache;
import com.ccreanga.bitbucketapi.example.serializers.jdefault.JavaDefaultSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableListSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableMapSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableMultimapSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableSetSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestMapDb {



    public static void main(String[] args) throws InterruptedException {

        Set<PullRequestActivity> activitySet = new HashSet<>();
        activitySet.add(new PullRequestOpenedActivity(21L,new Date(),new User(1,"name1","email1","display1",true,"slug", UserType.NORMAL),1));
        activitySet.add(new PullRequestOpenedActivity(51L,new Date(),new User(1,"name2","email2","display2",true,"slug", UserType.NORMAL),1));
        activitySet.add(new PullRequestOpenedActivity(81L,new Date(),new User(1,"name3","email3","display3",true,"slug", UserType.NORMAL),1));

        List<Commit> added = new ArrayList<>();
        List<MinimalCommit> minimalCommits = new ArrayList<>();
        minimalCommits.add(new MinimalCommit("abcdef0123abcdef4567abcdef8987abcdef6543","abcdef0"));
        added.add(new Commit("abcdef0123abcdef4567abcdef8987abcdef6543","abcdef0123a","charlie","charlie@example.com",1442553509807l,"WIP on feature 1",minimalCommits));
        added.add(new Commit("abcdef0123abcdef4567abcdef8987abcdef6543","abcdef0123a","charlie","charlie@example.com",1442553509156l,"WIP on feature 1",minimalCommits));

        List<Commit> removed = new ArrayList<>();
        minimalCommits = new ArrayList<>();
        minimalCommits.add(new MinimalCommit("abcdef0123abcdef4567abcdef8987abcdef6543","abcdef0"));
        removed.add(new Commit("def0123abcdef4567abcdef8987abcdef6543abc","def0123abcd","charlie","charlie@example.com",1442553509807l,"More work on feature 1",minimalCommits));
        removed.add(new Commit("def0123abcdef4567abcdef8987abcdef6543abc","def0123abcd","charlie","charlie@example.com",1442553509156l,"More work on feature 1",minimalCommits));

        PullRequestActivity pullRequestActivity = new PullRequestRescopedActivity(
                101L,
                new Date(1359065920),
                new User(101,"jcitizen", "jane@example.com", "Jane Citizen", true, "jcitizen", UserType.NORMAL),1L,
                "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde",
                "bcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdea",
                "cdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeab",
                "ddeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabc",
                added,
                removed
        );
        activitySet.add(pullRequestActivity);

        Kryo kryo = new Kryo();
        Output output = new Output(4096);


        kryo.register(PullRequestOpenedActivity.class);
        kryo.register(PullRequestRescopedActivity.class);
        ImmutableListSerializer.registerSerializers( kryo );
        ImmutableSetSerializer.registerSerializers( kryo );
        ImmutableMapSerializer.registerSerializers( kryo );
        ImmutableMultimapSerializer.registerSerializers( kryo );


        kryo.writeClassAndObject(output,activitySet);
        output.close();
        System.out.println(output.toBytes().length);


        JavaDefaultSerializer  serializer = new JavaDefaultSerializer();
        byte[] data = serializer.serialize(activitySet);
        System.out.println(data.length);

        data = output.toBytes();

        Cache cache = new BerkeleyDbCache("/tmp/dbEnv","cache");
        cache.put("key1",new byte[]{2,2,2},111);
        cache.put("key2",new byte[]{2,2,2},111);


        System.out.println(cache.size());
        cache.clear();
        System.out.println(cache.size());
        cache.put("key1",new byte[]{2,2,2},111);
        System.out.println(cache.size());

//        byte[] v = cache.get("key");
//        System.out.println(cache.get("key").length);
//        cache.remove("key");
//        //Thread.sleep(1000);
//
//        System.out.println(cache.get("key"));
//        cache.remove("key");
    }

}
