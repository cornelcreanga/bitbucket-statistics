package com.ccreanga.bitbucketapi.example.context;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.SshClient;
import com.ccreanga.bitbucket.rest.client.http.BitBucketClientFactory;
import com.ccreanga.bitbucket.rest.client.http.BitBucketCredentials;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "bitbucket", ignoreUnknownFields = false)
@EnableCaching
public class SpringContext {

    private String bitBucketUrl;
    private String bitBucketUser;
    private String bitBucketPassword;

    private String path;
    private String name;
    private int expiration;

    @Bean(destroyMethod = "shutdown")
    BitBucketClientFactory getBitBucketClientFactory() {
        try {
            return new BitBucketClientFactory(bitBucketUrl, new BitBucketCredentials(bitBucketUser, bitBucketPassword));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @DependsOn("getBitBucketClientFactory")
    public ProjectClient getStashClient() {
        return getBitBucketClientFactory().getProjectClient();
    }

    @Bean
    @DependsOn("getBitBucketClientFactory")
    public SshClient getSshClient() {
        return getBitBucketClientFactory().getSshClient();
    }

    @Bean
    @DependsOn("cache")
    HTreeMap<String,byte[]> cache(){
        DB db = db();
        DB.HTreeMapMaker mapMaker = db.createHashMap(name);
        mapMaker.expireAfterWrite(expiration, TimeUnit.SECONDS);
        return mapMaker.makeOrGet();
    }

    @Bean
    DB db(){
        return DBMaker.newFileDB(new File(path))
                .closeOnJvmShutdown()
                .make();
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public void setBitBucketUrl(String bitBucketUrl) {
        this.bitBucketUrl = bitBucketUrl;
    }

    public void setBitBucketPassword(String bitBucketPassword) {
        this.bitBucketPassword = bitBucketPassword;
    }

    public void setBitBucketUser(String bitBucketUser) {
        this.bitBucketUser = bitBucketUser;
    }
}
