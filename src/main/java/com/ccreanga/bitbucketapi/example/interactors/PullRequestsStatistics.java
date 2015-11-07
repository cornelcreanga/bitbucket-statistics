package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public interface PullRequestsStatistics {

    Map<Date, List<PullRequest>> getPullReqsGroupedByDay(String projectKey, String repositorySlug, Date startDate, Date endDate);

    Map<User, List<PullRequest>> getPullReqsGroupedByUsers(String projectKey, String repositorySlug, Date startDate, Date endDate);

    Map<User, List<PullRequestActivity>> getPullReqsMergeActivitiesGroupedByUsers(String projectKey, String repositorySlug, Date startDate, Date endDate);

}
