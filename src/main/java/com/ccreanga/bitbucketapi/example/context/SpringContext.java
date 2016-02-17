package com.ccreanga.bitbucketapi.example.context;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.SshClient;
import com.ccreanga.bitbucket.rest.client.http.BitBucketClientFactory;
import com.ccreanga.bitbucket.rest.client.http.BitBucketCredentials;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;

@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "bitbucket", ignoreUnknownFields = false)
@EnableCaching
public class SpringContext {

    private String bitBucketUrl;
    private String bitBucketUser;
    private String bitBucketPassword;


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
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
        cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cmfb.setShared(true);
        return cmfb;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
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
