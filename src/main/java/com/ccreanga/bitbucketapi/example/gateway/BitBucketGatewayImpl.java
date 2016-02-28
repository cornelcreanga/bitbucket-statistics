package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.Wildcard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;

import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
@ComponentScan(basePackages = {"com.ccreanga.bitbucket.rest.client.http","com.ccreanga.bitbucketapi.example"})
public class BitBucketGatewayImpl implements BitBucketGateway {


    private final ProjectClient projectClient;

    @Autowired
    public BitBucketGatewayImpl(ProjectClient projectClient) {
        this.projectClient = projectClient;
    }

    @Override
    @Cacheable(value="bitbucket")
    public Set<Project> getProjects() {
        return projectClient.getProjects();
    }

    @Override
    @Cacheable(value="bitbucket")
    public Set<Repository> getRepositories(String projectKey) {
        return projectClient.getProjectRepositories(projectKey);
    }

    @Override
    @Cacheable(value="bitbucket")
    public Set<PullRequest> getPullRequests(String projectKey, String repositorySlug, PullRequestState pullRequestState) {
        Set<Repository> repositories = projectClient.getProjectRepositories(projectKey);
        return repositories.stream().
                filter(r-> Wildcard.matches(repositorySlug,r.getSlug())).
                flatMap(r->projectClient.getPullRequests(projectKey, r.getSlug(), pullRequestState, true, null).stream()).
                collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value="bitbucket")
    public Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId) {
        return projectClient.getPullRequestsActivities(projectKey, repositorySlug, pullRequestId);    }
}
