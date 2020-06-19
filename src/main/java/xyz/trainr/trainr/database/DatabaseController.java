package xyz.trainr.trainr.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * Controls the behavior of the MongoDB connection
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseController {

    // Define local variables
    private final String connectionString;
    private final String databaseName;

    // Define the MongoDB client
    private MongoClient client;

    /**
     * Creates a new MongoDB database controller
     *
     * @param host     The host of the MongoDB instance
     * @param port     The port of the MongoDB instance
     * @param username The name of the user to authenticate with
     * @param password The password of the user to authenticate with
     * @param authDB   The name of the authorization database
     * @param dataDB   The name of the data database
     */
    public DatabaseController(String host, int port, String username, String password, String authDB, String dataDB) {
        this.connectionString = "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + authDB;
        this.databaseName = dataDB;
    }

    /**
     * Opens the MongoDB connection
     */
    public void openConnection() {
        // Initialize the POJO codec registry
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // Initialize the MongoDB client
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        client = MongoClients.create(settings);
    }

    /**
     * @return The MongoDB database with the pre-specified name
     */
    public MongoDatabase getDatabase() {
        return client.getDatabase(databaseName);
    }

    /**
     * Closes the MongoDB connection
     */
    public void closeConnection() {
        client.close();
    }

}
