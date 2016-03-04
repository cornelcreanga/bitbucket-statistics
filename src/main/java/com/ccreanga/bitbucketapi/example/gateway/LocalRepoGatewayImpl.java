package com.ccreanga.bitbucketapi.example.gateway;

import com.ccreanga.bitbucket.rest.client.model.Project;
import com.ccreanga.bitbucket.rest.client.model.Repository;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucketapi.example.context.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Set;

@Component(value = "localRepoGateway")
public class LocalRepoGatewayImpl implements BitBucketGateway {

    @Autowired
    SpringContext context;

    @Override
    public Set<Project> getProjects() {
        return null;
    }

    @Override
    public Set<Repository> getRepositories(String projectKey) {
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(context.getRepositoryPath()+ File.separatorChar+"repos"))) {
            inputStream.readObject();
            return (Set<Repository>)inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<PullRequest> getPullRequests(String projectKey, String repositorySlug, PullRequestState pullRequestState) {
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(context.getRepositoryPath()+ File.separatorChar+"pullRequests"))) {
            return (Set<PullRequest>)inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Set<PullRequestActivity> getPullRequestsActivities(String projectKey, String repositorySlug, Long pullRequestId) {
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(context.getRepositoryPath()+ File.separatorChar+"prAct_"+repositorySlug+"_"+pullRequestId))) {
            return (Set<PullRequestActivity>)inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
