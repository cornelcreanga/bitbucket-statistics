package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.Comment;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivityActionType;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestCommentActivity;
import com.ccreanga.bitbucketapi.example.gateway.BitBucketGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PullRequestsStatisticsImpl implements PullRequestsStatistics {


    private final BitBucketGateway gateway;

    @Autowired
    public PullRequestsStatisticsImpl(BitBucketGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Map<User, List<Comment>> getUserComments(String projectKey, String repositorySlug, Date startDate, Date endDate) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug);
        return pullRequests.stream().
                filter(pr -> pr.getCreatedDate().after(startDate) && pr.getCreatedDate().before(endDate)).
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
    public Map<User, List<PullRequestActivity>> getPullReqsActivitiesGroupedByUsers(
            String projectKey, String repositorySlug, PullRequestActivityActionType activityType, Date startDate, Date endDate) {
        Set<PullRequest> pullRequests = gateway.getPullRequests(projectKey, repositorySlug);

        return pullRequests.stream().
                filter(pr -> pr.getCreatedDate().after(startDate) && pr.getCreatedDate().before(endDate)).
                flatMap(pr -> gateway.getPullRequestsActivities(projectKey, repositorySlug, pr.getId()).
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
