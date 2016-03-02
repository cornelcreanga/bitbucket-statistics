package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.Utils;
import com.ccreanga.bitbucketapi.example.Wildcard;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;

import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
@ComponentScan(basePackages = {"com.ccreanga.bitbucket.rest.client.http","com.ccreanga.bitbucketapi.example"})
@SuppressWarnings("unchecked")
public class BitBucketGatewayImpl implements BitBucketGateway {

    @Autowired
    HTreeMap cache;

    @Autowired
    DB db;

    private final ProjectClient projectClient;

    @Autowired
    public BitBucketGatewayImpl(ProjectClient projectClient) {
        this.projectClient = projectClient;
    }

    @Override
    public Set<Project> getProjects() {
        byte[] projectsCached = (byte[]) cache.get("projects");
        if (projectsCached!=null)
            return (Set<Project>) Utils.deserialize(projectsCached);

        Set<Project> projects = projectClient.getProjects();
        cache.put("projects",Utils.serialize(projects));
        db.commit();
        return projects;
    }

    @Override
    public Set<Repository> getRepositories(String projectKey) {
        byte[] repos = (byte[]) cache.get("repos_"+projectKey);
        if (repos!=null)
            return (Set<Repository>) Utils.deserialize(repos);

        Set<Repository> repositories = projectClient.getProjectRepositories(projectKey);
        cache.put("repos_"+projectKey,Utils.serialize(repositories));
        db.commit();
        return repositories;
    }

    @Override
    public Set<PullRequest> getPullRequests(String projectKey, String repositorySlug, PullRequestState pullRequestState) {

        byte[] pr = (byte[]) cache.get("pr_"+projectKey+"_"+repositorySlug+"_"+pullRequestState);
        if (pr!=null)
            return (Set<PullRequest>) Utils.deserialize(pr);


        Set<Repository> repositories = projectClient.getProjectRepositories(projectKey);
        Set<PullRequest> pullRequests = repositories.stream().
                filter(r-> Wildcard.matches(repositorySlug,r.getSlug())).
                flatMap(r->projectClient.getPullRequests(projectKey, r.getSlug(), pullRequestState, true, null).stream()).
                collect(Collectors.toSet());
        cache.put("pr_"+projectKey+"_"+repositorySlug+"_"+pullRequestState,pullRequests);
        db.commit();
        return pullRequests;
    }

    @Override
    public Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId) {


        byte[] pr = (byte[]) cache.get("pra_"+projectKey+"_"+repositorySlug+"_"+pullRequestId);
        if (pr!=null)
            return (Set<PullRequestActivity>) Utils.deserialize(pr);

        Set<PullRequestActivity> pullRequestsActivities =projectClient.getPullRequestsActivities(projectKey, repositorySlug, pullRequestId);
        cache.put("pra_"+projectKey+"_"+repositorySlug+"_"+pullRequestId,pullRequestsActivities);
        db.commit();
        return pullRequestsActivities;
    }
}
