package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucketapi.example.gateway.BitBucketGateway;
import com.ccreanga.bitbucketapi.example.interactors.PullRequestsStatistics;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class Statistics {

    @Autowired
    PullRequestsStatistics pullRequestsStatistics;

    @Autowired
    @Qualifier("localRepoGateway")
    BitBucketGateway bitBucketGateway;

    public PullRequestsStatistics getPullRequestsStatistics() {
        return pullRequestsStatistics;
    }

    public BitBucketGateway getBitBucketGateway() {
        return bitBucketGateway;
    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Statistics.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx= app.run(args);
        Statistics printStatistics = ctx.getBean(Statistics.class);
        PullRequestsStatistics statistics = printStatistics.getPullRequestsStatistics();
        BitBucketGateway gateway = printStatistics.getBitBucketGateway();
        Set<PullRequest> pullRequests = gateway.getPullRequests("","*", PullRequestState.MERGED);
        System.out.println(pullRequests.size());

        Set<String> repoNames = pullRequests.stream().map(pr->pr.getFrom().getRepositorySlug()).distinct().collect(Collectors.toSet());
        System.out.println(repoNames.toString());

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();

        calendar.add(Calendar.MONTH,-36);
        Date start = calendar.getTime();

        Map<User, List<PullRequest>> userPullRequests = statistics.getPullReqsGroupedByUsers("","",PullRequestState.MERGED,start,end);

        Set<User> users = userPullRequests.keySet();
        for (User next : users) {
            System.out.println(next.getName() + "-" + userPullRequests.get(next).size());
        }

    }

    private static void mergeMaps(Map<User, List<PullRequest>> map1,Map<User, List<PullRequest>> map2){
        Map<User, List<PullRequest>> userPullRequests = new HashMap<>();
        Set<User> users = map2.keySet();
        for (User next : users) {
            List<PullRequest> pullRequests = map1.get(next);
            if (pullRequests == null)
                map1.put(next, map2.get(next));
            else {
                pullRequests.addAll(map2.get(next));
                map1.put(next, pullRequests);
            }
        }



    }


}
