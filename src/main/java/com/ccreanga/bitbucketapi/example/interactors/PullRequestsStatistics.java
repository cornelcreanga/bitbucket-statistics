package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.Comment;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivityActionType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface PullRequestsStatistics {

    Map<User,List<Comment>> getUserComments(String projectKey, String repositorySlug, Date startDate, Date endDate);

    Map<Date, List<PullRequest>> getPullReqsGroupedByDay(String projectKey, String repositorySlug, Date startDate, Date endDate);

    Map<User, List<PullRequest>> getPullReqsGroupedByUsers(String projectKey, String repositorySlug, Date startDate, Date endDate);

    Map<User, List<PullRequestActivity>> getPullReqsActivitiesGroupedByUsers(
            String projectKey, String repositorySlug, PullRequestActivityActionType activityType, Date startDate, Date endDate);

}
