package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a method that take all the reviews that are stored inside MongoDB and
 * add them inside Neo4J
 */
public class Populating_function_social {

    /**
     * Populate Neo4J with data stored in wines collection
     */
    public void populateSocial() {
        MongoClient mongoClient = MongoClients.create();
        Crud_mongo mongo = new Crud_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        DbOperations db = new DbOperations();
        ArrayList<Review> reviews = null;
        ArrayList<Wine> wines = null;
        ArrayList<User> users = null;

        wines = mongo.findAllWine();
        reviews = mongo.findAllReview();
        users = mongo.findAllUser();


        for (Review review : reviews) {
            graph.addComment(review.getDescription(), review.getRating().toString());
        }

        for (User user : users) {
            graph.registerUser(user.getUsername(), user.getPassword(), user.getAdmin().toString(), user.getTwitter_taster_handle(), user.getCountry(), user.getEmail());
        }

        for (Wine wine : wines) {
            List[] userList = mongo.findAllReviewAndUserForSpecificWine(wine.getWineName());
            reviews = (ArrayList<Review>) userList[0];
            users = (ArrayList<User>) userList[1];
            graph.addWine(wine.getWineName(), wine.getDesignation(), wine.getPrice().toString(), wine.getProvince(), wine.getVariety(), wine.getWinery());
            for (int i = 0; i < reviews.size(); i++) {
                Review rev = reviews.get(i);
                User us = users.get(i);
                graph.registerUser(us.getUsername(), "0000", "false", us.getTwitter_taster_handle(), us.getCountry(), us.getEmail());
                graph.addComment(rev.getDescription(), rev.getRating().toString());
                graph.createRelationCreated(rev.getDescription(), us.getUsername());
                graph.createRelationRelated(wine.getWineName(), rev.getDescription());
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

