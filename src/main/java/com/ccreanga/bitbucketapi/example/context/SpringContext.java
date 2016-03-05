package com.ccreanga.bitbucketapi.example.context;

import com.ccreanga.bitbucket.rest.client.ProjectClient;
import com.ccreanga.bitbucket.rest.client.SshClient;
import com.ccreanga.bitbucket.rest.client.http.BitBucketClientFactory;
import com.ccreanga.bitbucket.rest.client.http.BitBucketCredentials;
import com.ccreanga.bitbucket.rest.client.model.*;
import com.ccreanga.bitbucket.rest.client.model.diff.*;
import com.ccreanga.bitbucket.rest.client.model.pull.*;
import com.ccreanga.bitbucket.rest.client.model.pull.activity.*;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableListSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableMapSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableMultimapSerializer;
import com.ccreanga.bitbucketapi.example.serializers.kryo.ImmutableSetSerializer;
import com.ccreanga.bitbucketapi.example.cache.Cache;
import com.ccreanga.bitbucketapi.example.cache.MapDbCache;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "props", ignoreUnknownFields = false)
@EnableCaching
public class SpringContext {

    private String bitBucketUrl;
    private String bitBucketUser;
    private String bitBucketPassword;

    private String cachePath;
    private String cacheName;
    private int cacheExpiration;

    private String repositoryPath;

    @Bean(destroyMethod = "shutdown")
    BitBucketClientFactory getBitBucketClientFactory() {
        try {
            return new BitBucketClientFactory(bitBucketUrl, new BitBucketCredentials(bitBucketUser, bitBucketPassword));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @DependsOn("getBitBucketClientFactory")
    public ProjectClient getStashClient() {
        return getBitBucketClientFactory().getProjectClient();
    }

    @Bean
    @DependsOn("getBitBucketClientFactory")
    public SshClient getSshClient() {
        return getBitBucketClientFactory().getSshClient();
    }

    @Bean(destroyMethod = "close")
    public Cache<String,byte[]> getShortLivedCache(){
        return new MapDbCache<>(cachePath,cacheName,cacheExpiration);
    }

    @Bean
    @DependsOn("db")
    HTreeMap<String,byte[]> cache(){
        DB db = db();
        DB.HTreeMapMaker mapMaker = db.createHashMap(cacheName);
        mapMaker.expireAfterWrite(cacheExpiration, TimeUnit.SECONDS);
        return mapMaker.makeOrGet();
    }

    @Bean(destroyMethod = "close")
    DB db(){
        return DBMaker.newFileDB(new File(cachePath))
                .make();
    }

    @Bean
    KryoPool getKryoPool(){
        KryoFactory factory = () -> {
            Kryo kryo = new Kryo();
            kryo.register(PullRequestOpenedActivity.class);
            kryo.register(ConflictMarker.class);
            kryo.register(Diff.class);
            kryo.register(DiffHunk.class);
            kryo.register(DiffLine.class);
            kryo.register(DiffSegment.class);
            kryo.register(DiffSegmentType.class);
            kryo.register(PullRequestActivity.class);
            kryo.register(PullRequestActivityActionType.class);
            kryo.register(PullRequestApprovedActivity.class);
            kryo.register(PullRequestCommentActivity.class);
            kryo.register(PullRequestDeclinedActivity.class);
            kryo.register(PullRequestMergedActivity.class);
            kryo.register(PullRequestOpenedActivity.class);
            kryo.register(PullRequestReOpenedActivity.class);
            kryo.register(PullRequestRescopedActivity.class);
            kryo.register(PullRequestUnapprovedActivity.class);
            kryo.register(PullRequest.class);
            kryo.register(PullRequestBranch.class);
            kryo.register(PullRequestChange.class);
            kryo.register(PullRequestParticipant.class);
            kryo.register(PullRequestRole.class);
            kryo.register(PullRequestState.class);
            kryo.register(Branch.class);
            kryo.register(Comment.class);
            kryo.register(CommentAnchor.class);
            kryo.register(Commit.class);
            kryo.register(FileChangeType.class);
            kryo.register(FileType.class);
            kryo.register(LineType.class);
            kryo.register(Link.class);
            kryo.register(MinimalCommit.class);
            kryo.register(NodeType.class);
            kryo.register(Page.class);
            kryo.register(Path.class);
            kryo.register(PermittedOperations.class);
            kryo.register(Project.class);
            kryo.register(ProjectType.class);
            kryo.register(Repository.class);
            kryo.register(RepositorySshKey.class);
            kryo.register(RepositoryState.class);
            kryo.register(SshKey.class);
            kryo.register(Task.class);
            kryo.register(TaskOperations.class);
            kryo.register(TaskState.class);
            kryo.register(User.class);
            kryo.register(UserSshKey.class);
            kryo.register(UserType.class);
            ImmutableListSerializer.registerSerializers( kryo );
            ImmutableSetSerializer.registerSerializers( kryo );
            ImmutableMapSerializer.registerSerializers( kryo );
            ImmutableMultimapSerializer.registerSerializers( kryo );

            return kryo;
        };
        return new KryoPool.Builder(factory).build();
    }



    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setCacheExpiration(int cacheExpiration) {
        this.cacheExpiration = cacheExpiration;
    }

    public void setBitBucketUrl(String bitBucketUrl) {
        this.bitBucketUrl = bitBucketUrl;
    }

    public void setBitBucketPassword(String bitBucketPassword) {
        this.bitBucketPassword = bitBucketPassword;
    }

    public void setBitBucketUser(String bitBucketUser) {
        this.bitBucketUser = bitBucketUser;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }
}
