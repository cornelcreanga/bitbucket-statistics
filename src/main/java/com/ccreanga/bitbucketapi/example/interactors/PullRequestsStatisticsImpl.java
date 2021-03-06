package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.Comment;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivityActionType;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestCommentActivity;
import com.ccreanga.bitbucketapi.example.Interval;
import com.ccreanga.bitbucketapi.example.Utils;
import com.ccreanga.bitbucketapi.example.gateway.BitBucketGateway;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PullRequestsStatisticsImpl implements PullRequestsStatistics {

    private final BitBucketGateway gateway;

    @Autowired
    public PullRequestsStatisticsImpl(@Qualifier("bitBucketGateway") BitBucketGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Map<User, List<Comment>> getUserComments(String projectKey, String repositorySlug, PullRequestState pullRequestState, Interval interval) {

        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug, pullRequestState);
        return pullRequests.stream().
                filter(pr -> pr.createdBetween(interval.getStartDate(),interval.getEndDate())).
                flatMap(pr->gateway.getPullRequestsActivities(projectKey,repositorySlug,pr.getId()).
                        stream().
                        filter(PullRequestActivity::isComment).
                        flatMap(c-> {
                                    Comment comment = ((PullRequestCommentActivity) c).getComment();
                                    List<Comment> allComments = new ArrayList<>(5);
                                    traverse(allComments,comment);
                                    return allComments.stream();
                                }
                        )
                ).collect(Collectors.groupingBy(Comment::getAuthor));
    }

    @Override
    public Map<Date, List<PullRequest>> getPullReqsByDay(String projectKey, String repositorySlug,PullRequestState pullRequestState, Interval interval) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug, pullRequestState);

        return pullRequests.stream().
                filter(pr -> pr.createdBetween(interval.getStartDate(),interval.getEndDate())).
                collect(Collectors.groupingBy(pr -> Utils.truncate(pr.getCreatedDate())));
    }

    @Override
    public Map<User, List<PullRequest>> getPullReqsByUsers(String projectKey, String repositorySlug,PullRequestState pullRequestState, Interval interval) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug, pullRequestState);

        return pullRequests.stream().
                filter(pr -> pr.createdBetween(interval.getStartDate(),interval.getEndDate())).
                collect(Collectors.groupingBy(pr -> pr.getAuthor().getUser()));
    }

    @Override
    public Map<User, List<PullRequestActivity>> getPullReqsActivitiesByUsers(
            String projectKey, String repositorySlug,PullRequestState pullRequestState, PullRequestActivityActionType activityType, Interval interval) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug,pullRequestState);

        return pullRequests.stream().
                filter(pr -> pr.createdBetween(interval.getStartDate(),interval.getEndDate())).
                flatMap(pr -> gateway.getPullRequestsActivities(projectKey, pr.fromRepository(), pr.getId()).
                        stream().
                        filter(act->act.getActionType().equals(activityType))).
                collect(Collectors.toSet()).
                stream().
                collect(Collectors.groupingBy(PullRequestActivity::getUser));
    }


    private static void traverse(List<Comment> toAdd,Comment comment){
        toAdd.add(comment);
        List<Comment> comments = comment.getComments();
        for (Comment item : comments) {
            traverse(toAdd, item);
        }
    }


}
