package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Set;

@Repository
public class BitBucketGatewayImpl implements BitBucketGateway {

    @Inject
    ProjectClient projectClient;

    @Override
    public Set<Project> getProjects() {
        return projectClient.getProjects();
    }

    @Override
    public Set<com.ccreanga.bitbucket.rest.client.model.Repository> getRepositories(String projectKey) {
        return projectClient.getProjectRepositories(projectKey);
    }

    @Override
    public Set<PullRequest> getPullRequests(String projectKey, String repositorySlug) {
        return projectClient.getPullRequests(projectKey, repositorySlug, PullRequestState.OPEN, true, null);
    }

    @Override
    public Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId) {
        return projectClient.getPullRequestsActivities(projectKey, repositorySlug, pullRequestId);
    }
}
