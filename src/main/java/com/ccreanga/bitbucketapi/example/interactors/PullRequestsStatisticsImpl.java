package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.gateway.BitBucketGateway;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PullRequestsStatisticsImpl implements PullRequestsStatistics {

    @Inject
    private BitBucketGateway gateway;

    @Override
    public Map<Date, List<PullRequest>> getPullReqsGroupedByDay(String projectKey, String repositorySlug, Date startDate, Date endDate) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug);

        return pullRequests.stream().
                filter(pr -> pr.getCreatedDate().after(startDate) && pr.getCreatedDate().before(endDate)).
                collect(Collectors.groupingBy(pr -> truncate(pr.getCreatedDate())));
    }

    @Override
    public Map<User, List<PullRequest>> getPullReqsGroupedByUsers(String projectKey, String repositorySlug, Date startDate, Date endDate) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug);

        return pullRequests.stream().
                filter(pr -> pr.getCreatedDate().after(startDate) && pr.getCreatedDate().before(endDate)).
                collect(Collectors.groupingBy(pr -> pr.getAuthor().getUser()));
    }

    @Override
    public Map<User, List<PullRequestActivity>> getPullReqsMergeActivitiesGroupedByUsers(String projectKey, String repositorySlug, Date startDate, Date endDate) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug);

        //Set<PullRequestActivity> activities
        Map<User, List<PullRequestActivity>> activities = pullRequests.stream().
                filter(pr -> pr.getCreatedDate().after(startDate) && pr.getCreatedDate().before(endDate)).
                flatMap(pr -> gateway.getPullRequestsActivities(projectKey, repositorySlug, pr.getId()).stream()).
                collect(Collectors.toSet()).
                stream().
                collect(Collectors.groupingBy(PullRequestActivity::getUser));


        return activities;
    }

    private Date truncate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        return calendar.getTime();

    }
}
