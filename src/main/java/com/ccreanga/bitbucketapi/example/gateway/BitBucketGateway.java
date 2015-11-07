package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BitBucketGateway {

    Set<Project> getProjects();

    Set<com.ccreanga.bitbucket.rest.client.model.Repository> getRepositories(String projectKey);

    Set<PullRequest> getPullRequests(String projectKey, String repositorySlug);

    Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId);
}
