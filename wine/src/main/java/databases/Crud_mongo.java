package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import exception.ReviewAlreadyInserted;
import exception.UserNotPresentException;
import exception.WineNotExistsException;
import exception.WrongInsertionException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.unwind;

/**
 * Contains all the basic CRUD operation done on MongoDB
 */
public class Crud_mongo {
    MongoClient mongoClient = MongoClients.create();
    //MongoClient mongoClient = MongoClients.create("mongodb://172.16.4.79:27020,172.16.4.80:27020/"+ "?retryWrites=true&w=W1&readPreference=nearest&wtimeout=5000");
    MongoDatabase database = mongoClient.getDatabase("Wines");
    MongoCollection<Document> collection = database.getCollection("wines");
    /**
     * Add a wine, with all his features, on MongoDB but only if it's not already present
     *
     * @param title:       wine name
     * @param designation: the name of the wine given to the wine by the producer
     * @param price:       wine price
     * @param province:    production province
     * @param variety:     types of grapes used
     * @param winery:      producer
     * @param country:     production country
     */
    public void addWine(String title, String variety, String country, String province, String designation, String winery, int price) {
        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(variety) || Strings.isNullOrEmpty(country) || Strings.isNullOrEmpty(province) || Strings.isNullOrEmpty(designation) || Strings.isNullOrEmpty(winery) || price <= 0) {
            return;
        }


        BasicDBObject query = new BasicDBObject("wineName", title);
        MongoCursor<Document> cursor = collection.find(query).iterator();
        if (!cursor.hasNext()) {
            List<Document> reviews = new ArrayList<>();
            Document onlyWine = new Document()
                    .append("wineName", "" + title + "")
                    .append("variety", "" + variety + "")
                    .append("country", "" + country + "")
                    .append("province", "" + province + "")
                    .append("price", price)
                    .append("winery", "" + winery + "")
                    .append("designation", "" + designation + "")
                    .append("reviews", reviews);
            collection.insertOne(onlyWine);
        } else {
            try {
                throw new WrongInsertionException("A wine with this name already exists");
            } catch (WrongInsertionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Function used by scraper to populate MongoDB with new Wines and associated reviews
     *
     * @param title:       wine name
     * @param designation: the name of the wine given to the wine by the producer
     * @param price:       wine price
     * @param province:    production province
     * @param variety:     types of grapes used
     * @param winery:      producer
     * @param country:     production country
     * @param country:     user's country
     * @param mail:        user's email
     * @param description: comment body
     * @param points:      wine's rating
     * @throws ReviewAlreadyInserted: if a review for a wine already exits
     */
    public void createWine(String title, String variety, String country, String province, String designation, int price, String taster_name, int points,
                           String description, String winery, String taster_twitter_handle, String country_user, String mail) throws ReviewAlreadyInserted {

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(variety) || Strings.isNullOrEmpty(country) || Strings.isNullOrEmpty(province) || Strings.isNullOrEmpty(designation) || Strings.isNullOrEmpty(description) || price <= 0 || Strings.isNullOrEmpty(taster_name) || Strings.isNullOrEmpty(winery) || points <= 0 || Strings.isNullOrEmpty(taster_twitter_handle) || Strings.isNullOrEmpty(country_user) || Strings.isNullOrEmpty(mail)) {
            return;
        }

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
                throw new ReviewAlreadyInserted(" ");
            }
        }
    }

    /**
     * Delete a wine by his name
     *
     * @param title: wine's name to delete
     */
    public void deleteWine(String title) {
        try {
            collection.deleteMany(Filters.eq("wineName", "" + title + ""));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    /**
     * Find a wine and its features by its name
     *
     * @param title: Wine's name
     * @return wine: retrieved wine
     * @throws WineNotExistsException: if the searched wine doesn't exits in wines collection
     */
    public Wine findSpecificWine(String title) throws WineNotExistsException {
        BasicDBObject query = new BasicDBObject("wineName", title);
        MongoCursor<Document> cursor = collection.find(query).iterator();

        Wine wine;
        if (!cursor.hasNext()) {
            throw new WineNotExistsException(title + "doesn't exists");
        } else {
            Document temp_wine_doc = cursor.next();
            String wineName = temp_wine_doc.getString("wineName");
            String variety = temp_wine_doc.getString("variety");
            String country = temp_wine_doc.getString("country");
            String province = temp_wine_doc.getString("province");
            Integer price = temp_wine_doc.getInteger("price");
            String winery = temp_wine_doc.getString("winery");
            String designation = temp_wine_doc.getString("designation");
            wine = new Wine(wineName, designation, price, province, variety, winery, country);

        }
        return wine;
    }

    /**
     * Delete a comment of a given user for a specific wine
     *
     * @param description: comment body
     * @param taster_name: username
     * @param title:       wine name
     */
    public void deleteComment(String description, String taster_name, String title) {
        BasicDBObject match = new BasicDBObject("wineName", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name));
        try {
            collection.updateOne(match, new BasicDBObject("$pull", update));
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    /**
     * Add a comment for a given wine
     *
     * @param title:                 wine's name
     * @param taster_name:           username that made the comment
     * @param score:                 rating associated to wine
     * @param description:           comment associated to wine
     * @param taster_twitter_handle: twitter nickname of user that did the comment
     * @param country:               user's country
     * @param email:                 user's email
     * @throws WineNotExistsException: if user wants to do a comment for a wine that doesn't exists
     * @throws ReviewAlreadyInserted:  if user wants to do another review for same wine
     */
    public void addComment(String title, String taster_name, int score, String description, String taster_twitter_handle, String country, String email) throws WineNotExistsException, ReviewAlreadyInserted {
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

    /**
     * Scan all wines collection and delete all comments that a given user did
     *
     * @param taster_name: user name
     * @throws UserNotPresentException: if given user doesnt' exists
     */
    public void deleteAllCommentForGivenUser(String taster_name) throws UserNotPresentException {
        MongoCursor<Document> myDoc = collection.find(Filters.eq("reviews.taster_name", taster_name)).cursor();
        if (myDoc.hasNext() == false) {
            throw new UserNotPresentException(taster_name + " doesn't exists");
        }
        BasicDBObject match = new BasicDBObject("reviews.taster_name", taster_name);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("taster_name", taster_name));
        collection.updateMany(match, new BasicDBObject("$pull", update));
    }


    public ArrayList<Review> findAllCommentForGivenUser (String taster_name){
        Bson filter = Filters.eq("reviews.taster_name", taster_name);
        ArrayList<Review> reviews = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        Review review = null;
        AggregateIterable<Document> cursor = collection.aggregate(Arrays.asList(unwind,match(filter)));
        for (Document doc : cursor) {
            Document nestedReview = (Document) doc.get("reviews");
            Integer rating = nestedReview.getInteger("rating");
            String description = nestedReview.getString("description");
            review = new Review(description, rating);
            reviews.add(review);
        }
        return reviews;
    }


    /**
     * List of all users that are inside wines collection
     *
     * @return users: List of all user
     */
    public ArrayList<User> findAllUser() {
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
        return users;

    }

    /**
     * List of all comment made in the wines collection
     *
     * @return reviews: list of all comment made
     */
    public ArrayList<Review> findAllReview() {
        Review review = null;
        ArrayList<Review> reviews = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        Document group = new Document("$group", new Document("_id", new Document("description", "$reviews.description")
                .append("rating", "$reviews.rating")));
        AggregateIterable<Document> cursor = collection.aggregate(Arrays.asList(unwind,group));
        for (Document tempReview : cursor) {
            Document temp_rev_doc = (Document) tempReview.get("_id");
            Integer rating = temp_rev_doc.getInteger("rating");
            String description = temp_rev_doc.getString("description");
            review = new Review(description, rating);
            reviews.add(review);
        }
        return reviews;
    }

    /**
     * List of all wine that are inside wines collection
     *
     * @return wines: list of all wines
     */
    public ArrayList<Wine> findAllWine() {
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
        return wines;
    }

    /**
     * Retrieve a wine if it contains a given word or multiple word
     *
     * @param prefix: substring or entire wine name to retrieve
     * @return wines: list of all wines that contains at least the given word
     * @throws WineNotExistsException: if no wine(s) was/were retrieved in wines collection
     */
    public ArrayList<Wine> findWineByPrefix(String prefix) throws WineNotExistsException {
        Wine wine = null;
        ArrayList<Wine> wines = new ArrayList<>();
        Bson Filter = Filters.text("/" + prefix + " /i");
        MongoCursor<Document> cursor = collection.find(Filter).iterator();
        if (!cursor.hasNext()) {
            throw new WineNotExistsException("No wine found with " + prefix);
        } else {
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
            return wines;
        }
    }


    /**
     * Return all comment and user for a given wine
     *
     * @param title: wine's name
     * @return List: Array composed by list of comments and a list of users
     */
    public List[] findAllReviewAndUserForSpecificWine(String title) {
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
            String username =  nestedReview.getString("taster_name");
            String twitter_taster_handle =nestedReview.getString("taster_twitter_handle");
            String country = nestedReview.getString("user_country");
            String email = nestedReview.getString("email");
            review = new Review(description, rating);
            user = new User(username, "0000", twitter_taster_handle, country, email, false);
            reviews.add(review);
            users.add(user);
        }
        return new List[] {reviews,users};
    }
}




