package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util;

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.lifecycle.Startable;

public class MongoContainer implements Startable {

    private Startable container;
    private static final ArtifactVersion LIMIT_VERSION = new DefaultArtifactVersion("4.0.0");

    public static Builder builder() {
        return new Builder();
    }

    private MongoContainer(Startable container) {
        this.container = container;
    }

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void stop() {
        container.stop();
    }

    @Override
    public void close() {
        this.container.close();
    }

    public String getReplicaSetUrl() {
        return isReplicaSet()
                ? ((MongoDbReplicaSet) container).getReplicaSetUrl()
                : ((MongoDBContainer)container).getReplicaSetUrl();

    }

    public boolean isTransactionable() {
        return isReplicaSet();//for now it's the same
    }
    private boolean isReplicaSet() {
        return MongoDbReplicaSet.class.isAssignableFrom(container.getClass());
    }



    public static class Builder {
        private String mongoDockerImage;

        private Builder() {
        }

        public Builder mongoDockerImageName(String mongoDockerImage) {
            this.mongoDockerImage = mongoDockerImage;
            return this;
        }

        public MongoContainer build() {
            String[] imageParts = mongoDockerImage.split(":");
            ArtifactVersion actualVersion = new DefaultArtifactVersion(imageParts[1]);
            Startable container = actualVersion.compareTo(LIMIT_VERSION) >= 0
                    ? getMongoContainer4()
                    : getMongoContainer3();
            return new MongoContainer(container);
        }

        private Startable getMongoContainer3() {
            return new MongoDBContainer(mongoDockerImage);
        }

        private Startable getMongoContainer4() {
            return MongoDbReplicaSet.builder()
                    .mongoDockerImageName(mongoDockerImage)
//                .replicaSetNumber(2)
                    .build();
        }


    }
}
