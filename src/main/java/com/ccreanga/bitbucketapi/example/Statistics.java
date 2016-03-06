package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.gateway.BitBucketGateway;
import com.ccreanga.bitbucketapi.example.interactors.PullRequestsStatistics;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class Statistics {

    @Autowired
    PullRequestsStatistics pullRequestsStatistics;

    @Autowired
    @Qualifier("bitBucketGateway")
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

        for (Iterator<PullRequest> iterator = pullRequests.iterator(); iterator.hasNext(); ) {
            PullRequest next = iterator.next();
            Set<PullRequestActivity> activities = gateway.getPullRequestsActivities("",next.getFrom().getRepositorySlug(),next.getId());
            System.out.println(activities.size());
        }

        Set<String> repoNames = pullRequests.stream().map(pr->pr.getFrom().getRepositorySlug()).distinct().collect(Collectors.toSet());
        System.out.println(repoNames.toString());

        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();

        calendar.add(Calendar.MONTH,-12);
        Date start = calendar.getTime();

        Map<User, List<PullRequest>> userPullRequests = statistics.getPullReqsByUsers("","",PullRequestState.MERGED,new Interval(start,end));

        Set<User> users = new TreeSet<>((Comparator<User>) (o1, o2) -> o1.getEmailAddress().compareTo(o2.getEmailAddress()));
        users.addAll(userPullRequests.keySet());

        String[][] data = new String[users.size()][2];


        int counter = 0;
        for (User next : users) {
            data[counter][0]=next.getEmailAddress();
            data[counter][1]=""+userPullRequests.get(next).size();
            counter++;
            //System.out.println(next.getName() + "-" + userPullRequests.get(next).size());
        }

        System.out.println("----");


        Arrays.sort(data, (o1, o2) -> {
            int freq1 = Integer.parseInt(o1[1]);
            int freq2 = Integer.parseInt(o2[1]);
            return freq1-freq2;
        });
        for (int i = 0; i < data.length; i++) {
            String[] strings = data[i];
            System.out.println(strings[0] + "-" + strings[1]);
        }

    }



}
