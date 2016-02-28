package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import org.springframework.context.annotation.ComponentScan;

import java.util.Set;

@org.springframework.stereotype.Repository
@ComponentScan(basePackages = {"com.ccreanga.bitbucket.rest.client.http","com.ccreanga.bitbucketapi.example"})
public interface BitBucketGateway {

    /**
     * get all the projects
     * @return a set of project
     */
    Set<Project> getProjects();

    /**
     * Get all the repositories from a project
     * @param projectKey project key
     * @return set of repositories
     */
    Set<Repository> getRepositories(String projectKey);

    /**
     * Get all the pull requests from the specified project/repository (repositories)
     * @param projectKey project key
     * @param repositorySlug repository slug (can use wildcards *?^/()
     * @return set of pull requests
     */
    Set<PullRequest> getPullRequests(String projectKey, String repositorySlug, PullRequestState pullRequestState);

    /**
     * Get all the activities associated with a pull request id
     * @param projectKey project key
     * @param repositorySlug repository slug
     * @param pullRequestId pull request id
     * @return set of pull request activities
     */
    Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId);
}
