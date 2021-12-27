package databases;
import beans.Review;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import databases.Crud_graph;
import exception.AlreadyPopulatedException;

import java.util.ArrayList;

/**
 * The class contains a method that take all the reviews that are stored in Review collection inside MongoDB and
 * add them inside Neo4J automatically.
 */
public class Populating_function_social {

    /**
     * The method will create automatically nodes on Neo4J graph. Will be call only once to create a base for the graph,
     * then will be called after scraper's actions.
     */
    public void populateSocial() throws AlreadyPopulatedException {
        MongoClient mongoClient = MongoClients.create();
        Crud_mongo mongo = new Crud_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        ArrayList<Review> reviews = null;

        //Put th ecount function and check if the nodes are over 10000 for example
        //throw AlreadyPopException that means that the graph is already populated
        //and execution has to go further without execution populateSocial in main.
        reviews = mongo.findAllReview();
        for (Review review : reviews) {
            graph.addPostComplete(review.getTaster_name(),review.getTitle(),review.getDescription(),review.getWinery(),review.getCountry());
        }
    }
}
