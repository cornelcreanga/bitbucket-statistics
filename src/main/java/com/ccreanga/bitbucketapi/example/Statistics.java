package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucketapi.example.interactors.PullRequestsStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

@SpringBootApplication
public class Statistics {

    @Autowired
    PullRequestsStatistics pullRequestsStatistics;

    public PullRequestsStatistics getPullRequestsStatistics() {
        return pullRequestsStatistics;
    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Statistics.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx= app.run(args);
        Statistics printStatistics = ctx.getBean(Statistics.class);
        PullRequestsStatistics pullRequestsStatistics = printStatistics.getPullRequestsStatistics();

        pullRequestsStatistics.getUserComments("","",new Date(),new Date());

//        PullRequestsStatistics pullRequestsStatistics = new PullRequestsStatisticsImpl();
//        pullRequestsStatistics.getUserComments("","",new Date(),new Date());
    }

}