package com.okta.developer.listings;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongoRestoreExecutable;
import de.flapdoodle.embed.mongo.MongoRestoreProcess;
import de.flapdoodle.embed.mongo.MongoRestoreStarter;
import de.flapdoodle.embed.mongo.config.IMongoRestoreConfig;
import de.flapdoodle.embed.mongo.config.MongoRestoreConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Profile("seed")
public class MongoDBSeeder {

    private static Logger logger = LoggerFactory.getLogger(MongoDBSeeder.class);

    @Value("${mongo-dump}")
    private String mongoDump;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Autowired
    private MongoClient mongoClient;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        for (String name : mongoClient.listDatabaseNames()) {
            logger.info("DB: {}", name);
        }

        restore();

        for (String name : mongoClient.listDatabaseNames()) {
            logger.info("DB: {}", name);
            if ("airbnb".equalsIgnoreCase(name)) {
                MongoDatabase db = mongoClient.getDatabase(name);
                for (String collection : db.listCollectionNames()) {
                    logger.info("Collection: {}", collection);
                    MongoCollection<Document> mongoCollection = db.getCollection(collection);
                    logger.info("Documents count {}", mongoCollection.countDocuments());
                }
            }
        }
    }


    public void restore() {
        try {
            File file = new File(mongoDump);
            if (!file.exists()) {
                throw new RuntimeException("File does not exist");
            }
            String name = file.getAbsolutePath();

            IMongoRestoreConfig mongoconfig = new MongoRestoreConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongoPort, Network.localhostIsIPv6()))
                .db("airbnb")
                .collection("airbnb")
                .dropCollection(true)
                .dir(name)
                .build();

            MongoRestoreExecutable mongoRestoreExecutable = MongoRestoreStarter.getDefaultInstance().prepare(mongoconfig);
            MongoRestoreProcess mongoRestore = mongoRestoreExecutable.start();
            mongoRestore.stop();
        } catch (IOException e) {
            logger.error("Unable to restore mongodb", e);
        }
    }

}
