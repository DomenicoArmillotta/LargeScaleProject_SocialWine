package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import com.google.common.base.Strings;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import exception.AlreadyPopulatedException;
import exception.ReviewAlreadyInserted;
import exception.UserNotPresentException;
import exception.WineNotExistsException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;

/**
 * Contains all the crud operation that could be done on MongoDB.
 */
public class Crud_mongo {

    //WORKS ON NEO4J
    public void addWine(String title, String variety, String country, String province, String designation, String winery, int price) {
        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(variety) || Strings.isNullOrEmpty(country) || Strings.isNullOrEmpty(province) || Strings.isNullOrEmpty(designation) || Strings.isNullOrEmpty(winery) || price <= 0) {
            System.out.println("Fields for wine must not be null");
            return;
        }

        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        BasicDBObject query = new BasicDBObject("wineName", title);
        MongoCursor<Document> cursor = collection.find(query).iterator();
        if (!cursor.hasNext()){
            List<Document> reviews = new ArrayList<>();
            Document onlyWine = new Document()
                    .append("wineName", "" + title +"")
                    .append("variety", "" + variety + "")
                    .append("country", "" + country + "")
                    .append("province", "" +  province + "")
                    .append("price", price)
                    .append("winery", "" + winery + "")
                    .append("designation", "" + designation + "")
                    .append("reviews",reviews);
            collection.insertOne(onlyWine);
        } else {
            try {
                throw new AlreadyPopulatedException("A wine with this name already exists");
            } catch (AlreadyPopulatedException e) {
                System.out.println(e.getMessage());
            }
        }

    }



    public void createWine(String title, String variety, String country, String province, String designation, int price, String taster_name, int points,
                           String description, String winery, String taster_twitter_handle, String country_user, String mail) throws ReviewAlreadyInserted {

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(variety) || Strings.isNullOrEmpty(country) || Strings.isNullOrEmpty(province) || Strings.isNullOrEmpty(designation) || Strings.isNullOrEmpty(description) || price <= 0 || Strings.isNullOrEmpty(taster_name) || Strings.isNullOrEmpty(winery) || points <= 0 || Strings.isNullOrEmpty(taster_twitter_handle) || Strings.isNullOrEmpty(country_user) || Strings.isNullOrEmpty(mail)) {
            System.out.println("Fields for wine must not be null");
            return;
        }
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");


        BasicDBObject query = new BasicDBObject("wineName", title);
        MongoCursor<Document> cursor = collection.find(query).iterator();
        // wine document not found , do insertion
        if (!cursor.hasNext()) {
            // find all reviews belong to that title and add them
            List<Document> reviews = new ArrayList<>();
            Document rev = new Document("rating", points)
                    .append("description", "" + description + "")
                    .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                    .append("taster_name", "" + taster_name + "")
                    .append("user_country", "" + country_user + "")
                    .append("email", "" + mail + "");
            reviews.add(rev);


            Document mongoWine = new org.bson.Document("wineName", title)
                    .append("variety", variety).append("country", country).append("province", province).append("price", price).append("winery", winery).append("designation", designation).append("reviews", reviews);
            collection.insertOne(mongoWine);

        }
        // wine document is already inserted
        else {
            Document wineDocument = cursor.next();
            wineDocument.put("variety", variety);
            wineDocument.put("country", country);
            wineDocument.put("province", province);
            wineDocument.put("price", price);
            wineDocument.put("winery", winery);
            wineDocument.put("designation", designation);

            List<Document> reviews = (List<Document>) wineDocument.get("reviews");
            boolean isReviewInserted = false;
            for (Document review : reviews) {
                if (review.get("taster_name").toString().equals(taster_name)) {
                    isReviewInserted = true;
                    break;
                }
            }
            // the review is not inserted yet, do insertion
            if (!isReviewInserted) {
                Document review = new Document("rating", points)
                        .append("description", "" + description + "")
                        .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                        .append("taster_name", "" + taster_name + "")
                        .append("user_country", "" + country_user + "")
                        .append("email", "" + mail + "");
                reviews.add(review);
                wineDocument.put("reviews", reviews);
                // update and save
                Document update = new Document();

                update.append("$set", wineDocument);
                collection.updateOne(query, update);
            } else {
                // throw Already Inserted exception
                System.out.println("The review is already inserted for that wine");
                throw new ReviewAlreadyInserted("The review is already inserted for that wine");
            }
        }
    }

    //WORKS ON NEO4J
    public void deleteWine(String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        try {
            collection.deleteMany(Filters.eq("wineName", "" + title + ""));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    //WORKS ON NEO4J
    public void deleteComment(String description, String taster_name, String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        BasicDBObject match = new BasicDBObject("wineName", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name));
        try {
            collection.updateOne(match, new BasicDBObject("$pull", update));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    //WORKS ON NEO4J
    public void addComment(String title, String taster_name, int score, String description, String taster_twitter_handle, String country, String email) throws WineNotExistsException, ReviewAlreadyInserted {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        BasicDBObject query = new BasicDBObject("wineName", title);
        MongoCursor<Document> cursor = collection.find(query).iterator();
        if (!cursor.hasNext()) {
            try {
                throw new WineNotExistsException(title + " doesn't exists please create it");
            } catch (WineNotExistsException wex) {
                wex.getMessage();
            }
        } else {
            Document wineDocument = cursor.next();
            List<Document> reviews = (List<Document>) wineDocument.get("reviews");
            boolean isReviewInserted = false;
            for (Document review : reviews) {
                if (review.get("taster_name").toString().equals(taster_name)) {
                    isReviewInserted = true;
                    break;
                }
            }
            // the review is not inserted yet, do insertion
            if (!isReviewInserted) {
                Document review = new Document("rating", score)
                        .append("description", "" + description + "")
                        .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                        .append("taster_name", "" + taster_name + "")
                        .append("user_country", "" + country + "")
                        .append("email", "" + email + "");
                reviews.add(review);
                wineDocument.put("reviews", reviews);
                // update and save
                Document update = new Document();

                update.append("$set", wineDocument);
                collection.updateOne(query, update);
            } else {
                // throw Already Inserted exception
                try {
                    throw new ReviewAlreadyInserted("You already insert a review for this wine");

                } catch (ReviewAlreadyInserted rex) {
                    System.out.println(rex.getMessage());
                }
            }
        }
    }

    //WORK
    public void deleteAllCommentForGivenUser(String taster_name) throws UserNotPresentException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        MongoCursor<Document> myDoc = collection.find(Filters.eq("reviews.taster_name", taster_name)).cursor();
        if (myDoc.hasNext() == false) {
            throw new UserNotPresentException(taster_name + " doesn't exists");
        }
        BasicDBObject match = new BasicDBObject("reviews.taster_name", taster_name);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("taster_name", taster_name));
        collection.updateMany(match, new BasicDBObject("$pull", update));
    }


    public ArrayList<User> findAllUser() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        User user = null;
        ArrayList<User> users = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        Document group = new Document("$group", new Document("_id", new Document("taster_name", "$reviews.taster_name")
                .append("taster_twitter_handle", "$reviews.taster_twitter_handle")
                .append("country", "$reviews.user_country")
                .append("email", "$reviews.email")));
        AggregateIterable<Document> results = collection.aggregate(Arrays.asList(unwind, group));
        for (Document result : results) {
            Document temp_user_doc = (Document) result.get("_id");
            String username = temp_user_doc.getString("taster_name");
            String twitter_taster_handle = temp_user_doc.getString("taster_twitter_handle");
            String country = temp_user_doc.getString("country");
            String email = temp_user_doc.getString("email");
            user = new User(username, "0000", twitter_taster_handle, country, email, false);
            users.add(user);
        }
        mongoClient.close();
        return users;

    }

    public ArrayList<Review> findAllReview() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        Review review = null;
        ArrayList<Review> reviews = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        AggregateIterable<Document> cursor = collection.aggregate(Arrays.asList(unwind));
        for (Document tempReview : cursor) {
            Document nestedReview = (Document) tempReview.get("reviews");
            Integer rating = nestedReview.getInteger("rating");
            String description = nestedReview.getString("description");
            review = new Review(description, rating);
            reviews.add(review);
        }
        mongoClient.close();
        return reviews;
    }

    public ArrayList<Wine> findAllWine() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        Wine wine = null;
        ArrayList<Wine> wines = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document temp_wine_doc = cursor.next();
            String wineName = temp_wine_doc.getString("wineName");
            String variety = temp_wine_doc.getString("variety");
            String country = temp_wine_doc.getString("country");
            String province = temp_wine_doc.getString("province");
            Integer price = temp_wine_doc.getInteger("price");
            String winery = temp_wine_doc.getString("winery");
            String designation = temp_wine_doc.getString("designation");
            wine = new Wine(wineName, designation, price, province, variety, winery, country);
            wines.add(wine);
        }
        mongoClient.close();
        return wines;
    }

    public List[] findAllReviewAndUserForSpecificWine(String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Bson filter = Filters.eq("wineName", title);
        ArrayList<Review> reviews = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        Review review = null;
        User user = null;

        AggregateIterable<Document> cursor = collection.aggregate(Arrays.asList(match(filter), unwind));

        for (Document doc : cursor) {
            Document nestedReview = (Document) doc.get("reviews");
            Integer rating = nestedReview.getInteger("rating");
            String description = nestedReview.getString("description");
            String username = nestedReview.getString("taster_name");
            String twitter_taster_handle = nestedReview.getString("taster_twitter_handle");
            String country = nestedReview.getString("user_country");
            String email = nestedReview.getString("email");
            review = new Review(description, rating);
            user = new User(username, "", twitter_taster_handle, country, email, false);
            reviews.add(review);
            users.add(user);
        }
        return new List[]{reviews, users};
    }

}




