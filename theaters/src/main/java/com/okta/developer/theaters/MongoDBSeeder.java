package com.okta.developer.theaters;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongoRestoreExecutable;
import de.flapdoodle.embed.mongo.MongoRestoreProcess;
import de.flapdoodle.embed.mongo.MongoRestoreStarter;
import de.flapdoodle.embed.mongo.config.IMongoRestoreConfig;
import de.flapdoodle.embed.mongo.config.MongoRestoreConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        Flux<String> databaseNames = Flux.from(mongoClient.listDatabaseNames());
        databaseNames.subscribe(name -> {
            logger.info("DB: {}", name);
        });

        restore();

        databaseNames = Flux.from(mongoClient.listDatabaseNames());
        databaseNames.subscribe(database -> {
            logger.info("DB: {}", database);
            if ("theaters".equalsIgnoreCase(database)) {
                MongoDatabase db = mongoClient.getDatabase(database);

                Flux<String> collectionNames = Flux.from(db.listCollectionNames());
                collectionNames.subscribe(collection -> {
                    logger.info("Collection: {}", collection);
                    MongoCollection mongoCollection = db.getCollection(collection);
                    Mono<Long> count = Mono.from(mongoCollection.countDocuments());
                    count.subscribe(c -> {
                        logger.info("Documents count {}", c);
                    });
                });
            }
        });
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
                .db("theaters")
                .collection("theaters")
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
