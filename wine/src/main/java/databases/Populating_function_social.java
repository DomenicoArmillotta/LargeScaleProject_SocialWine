package databases;
import beans.Review;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.ArrayList;

public class Populating_function_social {
    public void populateSocial(){
        MongoClient mongoClient = MongoClients.create();
        Crud_mongo mongo = new Crud_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "root");
        ArrayList<Review> reviews = null;
        reviews = mongo.findAllReview();
        for (Review review : reviews) {
            graph.addPostComplete(review.getTaster_name(),review.getTitle(),review.getDescription(),review.getWinery(),review.getCountry());
        }
    }
}
