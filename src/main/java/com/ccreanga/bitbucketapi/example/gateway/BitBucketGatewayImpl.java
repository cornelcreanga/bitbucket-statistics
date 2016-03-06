package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.cache.Cache;
import com.ccreanga.bitbucketapi.example.serializers.Serializer;
import com.ccreanga.bitbucketapi.example.Wildcard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
@Component(value = "bitBucketGateway")
@SuppressWarnings("unchecked")
public class BitBucketGatewayImpl implements BitBucketGateway {

    private Cache cache;

    private Serializer serializer;

    private final ProjectClient projectClient;

    @Autowired
    public BitBucketGatewayImpl(ProjectClient projectClient,@Qualifier("kryoSerializer") Serializer serializer,Cache cache) {
        this.projectClient = projectClient;
        this.serializer = serializer;
        this.cache = cache;
    }

    @Override
    public Set<Project> getProjects() {
        byte[] projectsCached = cache.get("projects");
        if (projectsCached!=null)
            return (Set<Project>) serializer.deserialize(projectsCached);

        Set<Project> projects = projectClient.getProjects();
        cache.put("projects",serializer.serialize(projects),24*3600);
        return projects;
    }

    @Override
    public Set<Repository> getRepositories(String projectKey) {
        byte[] repos = cache.get("repos_"+projectKey);
        if (repos!=null)
            return (Set<Repository>) serializer.deserialize(repos);

        Set<Repository> repositories = projectClient.getProjectRepositories(projectKey);
        cache.put("repos_"+projectKey,serializer.serialize(repositories),24*3600);
        return repositories;
    }

    @Override
    public Set<PullRequest> getPullRequests(String projectKey, String repositorySlug, PullRequestState pullRequestState) {

        byte[] pr = cache.get("pr_"+projectKey+"_"+repositorySlug+"_"+pullRequestState);
        if (pr!=null)
            return (Set<PullRequest>) serializer.deserialize(pr);

        Set<Repository> repositories = projectClient.getProjectRepositories(projectKey);
        Set<PullRequest> pullRequests = repositories.stream().
                filter(r-> Wildcard.matches(r.getSlug(),repositorySlug)).
                flatMap(r->projectClient.getPullRequests(projectKey, r.getSlug(), pullRequestState, true, null).stream()).
                collect(Collectors.toSet());

        cache.put("pr_"+projectKey+"_"+repositorySlug+"_"+pullRequestState,serializer.serialize(pullRequests),3600);
        return pullRequests;
    }

    @Override
    public Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId) {


        byte[] pr = cache.get("pra_"+projectKey+"_"+repositorySlug+"_"+pullRequestId);
        if (pr!=null)
            return (Set<PullRequestActivity>) serializer.deserialize(pr);

        Set<PullRequestActivity> pullRequestsActivities =projectClient.getPullRequestsActivities(projectKey, repositorySlug, pullRequestId);

        cache.put("pra_"+projectKey+"_"+repositorySlug+"_"+pullRequestId,serializer.serialize(pullRequestsActivities),3600);
        return pullRequestsActivities;
    }
}
