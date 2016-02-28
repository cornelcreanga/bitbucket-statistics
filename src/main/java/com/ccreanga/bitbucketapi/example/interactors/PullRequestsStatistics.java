package com.ccreanga.bitbucketapi.example.interactors;

import com.ccreanga.bitbucket.rest.client.model.Comment;
import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequestState;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivity;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.PullRequestActivityActionType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface PullRequestsStatistics {


    /**
     1.Per fiecare proiect

     a ) numarul de PR ridicate de fiecare developer - opened PR
     b) nr de merge-uri facute de fiecare developer - merged PR
     c) primii 10 developeri cu cele mai multe comentarii adaugate pe PRs (adica top reviewers) - top comments
     d) pentru fiecare developer numarul de PRs ridicate de el si declinate - opened=>declined PR
     e) pentru fiecare developer numarul de PRs pe care el le declina pentru Pr-urile facute de altii=>declined PR
     f) ) numarul de comituri din doua in doua saptamani ( sau pe luna daca e mai simplu)=>no of commits per DEV
     g) top committers - primii 10 contributori - adica cei cu cele mai multe Pruri ridicate de ei si care se si merge-uiesc - pe luna sau daca e mai simplu, de la crearea proiectului     */

    /**
     * Get a map containing all the users/all the comments related to pull request activities
     * @param projectKey - project key
     * @param repositorySlug - repository. also accepts wildcards *?^()\
     * @param startDate - start date
     * @param endDate - end date
     * @return - map of user/list of comments
     */
    Map<User,List<Comment>> getUserComments(String projectKey, String repositorySlug, PullRequestState pullRequestState, Date startDate, Date endDate);

    /**
     * Get a map of days/ list of pull requests per day
     * @param projectKey - project key
     * @param repositorySlug - repository. also accepts wildcards *?^()\
     * @param startDate - start date
     * @param endDate - end date
     * @return - map of user/list of pull requests/day
     */
    Map<Date, List<PullRequest>> getPullReqsGroupedByDay(String projectKey, String repositorySlug, PullRequestState pullRequestState, Date startDate, Date endDate);

    /**
     * Get a map of users/ list of pull requests per user
     * @param projectKey - project key
     * @param repositorySlug - repository. also accepts wildcards *?^()\
     * @param startDate - start date
     * @param endDate - end date
     * @return - map of user/list of pull requests/user
     */
    Map<User, List<PullRequest>> getPullReqsGroupedByUsers(String projectKey, String repositorySlug, PullRequestState pullRequestState, Date startDate, Date endDate);

    /**
     * Get a map of users/ list of pull requests activities (eg opening/declining/merge) per user
     * @param projectKey - project key
     * @param repositorySlug - repository. also accepts wildcards *?^()\
     * @param activityType - activity type (one of )
     * @param startDate - start date
     * @param endDate - end date
     * @return - map of user/list of pull requests activities/user
     */
    Map<User, List<PullRequestActivity>> getPullReqsActivitiesGroupedByUsers(
            String projectKey, String repositorySlug, PullRequestState pullRequestState, PullRequestActivityActionType activityType, Date startDate, Date endDate);

}
