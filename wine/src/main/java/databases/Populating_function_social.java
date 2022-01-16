package databases;
import beans.Review;
import beans.User;
import beans.Wine;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import exception.AlreadyPopulatedException;

import java.util.ArrayList;

/**
 * The class contains a method that take all the reviews that are stored in Review collection inside MongoDB and
 * add them inside Neo4J automatically.
 */
public class Populating_function_social {


    //TO DO -- FUNCTION THAT TAKES WINES E REVIEWS AND POPULATE SOCIAL
    public void populateSocial(){
        MongoClient mongoClient = MongoClients.create();
        Crud_mongo mongo = new Crud_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        DbOperations db = new DbOperations();
        ArrayList<Review> reviews = null;
        ArrayList<Wine> wines = null;
        ArrayList<User> users = null;

        reviews = mongo.findAllReview();
        wines = mongo.findAllWine();
        users = mongo.findAllUser();
        int i=0;

        for (Review review : reviews) {
                for (Wine wine: wines) {
                    for (User user: users){
                        graph.addPostComplete(wine.getWineName(), wine.getVariety(), wine.getCountry(), wine.getProvince(), wine.getPrice().toString(),
                                wine.getWinery(), wine.getDesignation(), review.getRating(), review.getDescription(), user.getTwitter_taster_handle(),
                                user.getUsername(), user.getCountry(), user.getEmail());
                        System.out.println(i);
                        i++;
                    }
                }
        }
    }
}































    /**
     * The method will create automatically nodes on Neo4J graph. Will be call only once to create a base for the graph,
     * then will be called after scraper's actions.
     *//*
    public void populateSocial() throws AlreadyPopulatedException {
        MongoClient mongoClient = MongoClients.create();
        //connection with DB
        Crud_mongo mongo = new Crud_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");

        ArrayList<Review> reviews = null;
        if (graph.countGraphNodes().get(0).length()==1){
            reviews = mongo.findAllReview();
            //iterate among all review on MongoDb
            for (Review review : reviews) {
                //create the node "User"
                graph.addUser(review.getTaster_name());
                //use the function addPostComplete to add other node and edge
                graph.addPostComplete(review.getTaster_name(),review.getTitle(),review.getDescription(),review.getWinery(),review.getCountry());
            }
        } else throw new AlreadyPopulatedException("Graph is already populated!");
    }*/

