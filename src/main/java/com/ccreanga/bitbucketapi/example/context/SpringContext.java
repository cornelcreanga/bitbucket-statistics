package com.ccreanga.bitbucketapi.example.context;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.SshClient;
import com.ccreanga.bitbucket.rest.client.http.BitBucketClientFactory;
import com.ccreanga.bitbucket.rest.client.http.BitBucketCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URL;

@Configuration
@EnableMBeanExport
@ComponentScan(basePackages = {"com.ccreanga.bitbucketapi.example"})

@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
//@PropertySource("classpath:${spring.profile}.properties")
public class SpringContext {

    @Value("${bitBucketUrl}")
    private String bitBucketUrl;
    @Value("${bitBucketUser}")
    private String bitBucketUser;
    @Value("${bitBucketPassword}")
    private String bitBucketPassword;


    @Bean(destroyMethod = "shutdown")
    BitBucketClientFactory getBitBucketClientFactory() {
        try {
            return new BitBucketClientFactory(new URL(bitBucketUrl), new BitBucketCredentials(bitBucketUser, bitBucketPassword));
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
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
