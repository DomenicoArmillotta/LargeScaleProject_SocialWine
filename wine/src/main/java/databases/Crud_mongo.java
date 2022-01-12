package databases;
import beans.Review;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;

/**
 * Contains all the crud operation that could be done on MongoDB.
 */
public class Crud_mongo {




}

























   /* *//**
     * Create a new review inside review collection taking from parameters and insert it in the document
     * @param points: the score that user give for a certain wine;
     * @param title: title of the reivew;
     * @param description: the body of the review;
     * @param taster_name: user's name;
     * @param taster_twitter_handle: user twitter nickname;
     * @param price: wine's price that has been reviewed;
     * @param designation: wine's name;
     * @param variety: wine's typology;
     * @param region_1: region's origin;
     * @param region_2: region's origin;
     * @param province: province's origin of the wine;
     * @param country: winery's origin country;
     * @param winery: name of the winery that produces the wine.
     *//*
    public void createReview (String points , String title , String description , String taster_name , String taster_twitter_handle, int price , String designation, String variety , String region_1 , String region_2 , String province , String country , String winery   ) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Document doc = new Document("points" , "" + points + "")
                .append("title" , "" + title + "")
                .append("description" , "" + description + "")
                .append("taster_name" , "" + taster_name + "")
                .append("taster_twitter_handle" , "" + taster_twitter_handle + "")
                .append("price" , "" + price + "")
                .append("designation" , "" + designation + "")
                .append("variety" , "" + variety + "")
                .append("region_1" , "" + region_1 + "")
                .append("region_2" , "" + region_2 + "")
                .append("province" , "" + province + "")
                .append("country" , "" + country + "")
                .append("winery" , "" + winery + "");

        try {
            collection.insertOne(doc);
            System.out.println("Successfully inserted review. \n");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
        mongoClient.close();
    }

    *//**
     * Create a new user.
     * @param taster_name: user's name;
     * @param taster_twitter_handle: user's twitter nickname.
     *//*
    public void addUser (String taster_name , String taster_twitter_handle) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Document doc = new Document ("taster_name" , "" + taster_name + "")
                .append("taster_twitter_handle" , "" + taster_twitter_handle + "");
        try {
            collection.insertOne(doc);
            System.out.println("Successfully inserted user (MongoDB). \n");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
        mongoClient.close();
    }

    *//**
     * Create a new winery.
     * @param winery: winery's name;
     * @param country: country's name of the winery.
     *//*
    public void addWinery (String winery, String country) {
        final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        //final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Document doc = new Document ("winery" , "" + winery + "")
                .append("country" , "" + country + "");
        try {
            collection.insertOne(doc);
            System.out.println("Successfully inserted winery (MongoDB). \n");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
        mongoClient.close();
    }

    *//**
     * Delete an user.
     * @param twitterName: user's nickname to drop.
     *//*
    public void deleteUser (String twitterName) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        //use the function "DeleteONE" filtering by taster_twitter_handle
        collection.deleteOne(Filters.eq("taster_twitter_handle", ""+twitterName+""));
        System.out.println("User deleted successfully...(MongoDB).");
    }

    *//**
     * Delete a winery.
     * @param winery: winery's name to drop.
     *//*
    public void deleteWinery (String winery) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        // use the function "DeleteONE" filtering by winery name
        collection.deleteOne(Filters.eq("winery", ""+winery+""));
        System.out.println("Winery deleted successfully... (MongoDB).");
    }


    *//**
     * Retrieve all the review that refers to a specific winery.
     * @param winery: winery's name.
     *//*
    public  void findReviewByWinery (String winery){
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        //we select collection review
        MongoCollection<Document> collection = database.getCollection("review");
        //retrieve all winery review
        Bson query = eq("winery" , "" + winery + "");
        try{
            MongoCursor<Document> cursor = collection.find(query).iterator();
            while (cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        }catch (MongoCursorNotFoundException mce){
            mce.printStackTrace();
        }
        mongoClient.close();
    }

    *//**
     * Delete all the reviews of a specific user.
     * @param taster_name: user's name.
     *//*
    public void deleteReviewsByTaster_Name (String taster_name) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        try {
            Bson query = eq("taster_name" , "" +  taster_name + "");
            //used deleteMany becouse one user could write a lot of reviews
            DeleteResult deleteResult = collection.deleteMany(query);
            System.out.println("Document dropped successfully");
            System.out.println("Were dropped "  + deleteResult.getDeletedCount() + " (MongoDB).");
        } catch (MongoException me){
            me.printStackTrace();
        }
        mongoClient.close();

    }

    *//**
     * Update the price under a certain threshold.
     * @param selectOldPrice: price to update;
     * @param newPrice: new price.
     *//*
    public void updateAllPrice (int selectOldPrice , int newPrice) {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        try {
        BasicDBObject set = new BasicDBObject("price","" + newPrice + "");
        UpdateResult updateResult = collection.updateMany(lt("price", "" + selectOldPrice + ""), set);
        System.out.println("Document updated successfully (MongoDB).");
        System.out.println(updateResult.getModifiedCount());
        } catch (MongoException me){
            me.printStackTrace();
        }
        mongoClient.close();

    }


    *//**
     * Find review from the _id and create a new review.
     * @param id: review's id;
     * @return: added review.
     *//*
    public Review findReviewFromId (String id){
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Review review = null;
        Bson query = eq("_id" , "" + id + "");
        MongoCursor<Document> cursor = collection.find(query).iterator();
        while (cursor.hasNext()){
            Document temp_review_doc = cursor.next();
            String points = temp_review_doc.getString("points");
            System.out.println(points);
            String title = temp_review_doc.getString("title");
            String description = temp_review_doc.getString("description");
            String taster_name = temp_review_doc.getString("taster_name");
            String taster_twitter_handle = temp_review_doc.getString("taster_twitter_handle");
            Integer price = temp_review_doc.getInteger("price");
            String designation = temp_review_doc.getString("designation");
            String variety = temp_review_doc.getString("variety");
            String region_1 = temp_review_doc.getString("region_1");
            String region_2 = temp_review_doc.getString("region_2");
            String province = temp_review_doc.getString("province");
            String country = temp_review_doc.getString("country");
            String winery = temp_review_doc.getString("winery");
            review = new Review(points,title,description,taster_name,taster_twitter_handle,price,designation,variety,region_1,region_2,province,country,winery);
        }
        mongoClient.close();
        return review;
    }

    *//**
     * Show all the review made by a specific user.
     * @param taster_name: user's name.
     *//*
    public void showReviewFromTaster_name (String taster_name){
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Bson query = eq("taster_name" , "" + taster_name + "");

        MongoCursor<Document> cursor = collection.find(query).iterator();
        while (cursor.hasNext()){
            Document temp_review_doc = cursor.next();
            String title = temp_review_doc.getString("title");
            String description = temp_review_doc.getString("description");
            System.out.println("Title of the post = " + title);
            System.out.println("Description of the post = " + description);
        }
        mongoClient.close();
    }


    *//**
     * Find all the review stored in the review collection in MongoDB.
     * @return: All the review stored.
     *//*
    public ArrayList<Review> findAllReview (){
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Review review = null;
        ArrayList<Review> reviews = new ArrayList<Review>();
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()){
            Document temp_review_doc = cursor.next();
            Object points = temp_review_doc.get("points");
            if (points instanceof String)
                points = temp_review_doc.getString("points");
            else
                points = temp_review_doc.getInteger("points");
            //System.out.println(points);
            String title = temp_review_doc.getString("title");
            String description = temp_review_doc.getString("description");
            String taster_name = temp_review_doc.getString("taster_name");
            String taster_twitter_handle = temp_review_doc.getString("taster_twitter_handle");
            Integer price = temp_review_doc.getInteger("price");
            if(price==null) {
                price=0;
            }
            String designation = temp_review_doc.getString("designation");
            String variety = temp_review_doc.getString("variety");
            String region_1 = temp_review_doc.getString("region_1");
            String region_2 = temp_review_doc.getString("region_2");
            String province = temp_review_doc.getString("province");
            String country = temp_review_doc.getString("country");
            String winery = temp_review_doc.getString("winery");
            review = new Review(points,title,description,taster_name,taster_twitter_handle,price,designation,variety,region_1,region_2,province,country,winery);
            reviews.add(review);
        }
        mongoClient.close();
        return reviews;
    }
*/






