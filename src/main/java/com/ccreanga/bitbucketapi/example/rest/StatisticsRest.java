package com.ccreanga.bitbucketapi.example.rest;

import com.ccreanga.bitbucketapi.example.interactors.PullRequestsStatistics;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Component
@Path("/hello")
public class StatisticsRest {

    @Inject
    private PullRequestsStatistics pullRequestsStatistics;

    @GET
    @Path("{name}")
    public Response sayHello(@PathParam("name") String name) {
        return Response.status(200).entity("salut " + name).build();
    }

}
