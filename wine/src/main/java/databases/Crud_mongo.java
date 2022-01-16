package databases;
import beans.Review;
import beans.User;
import beans.Wine;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import exception.NoCountryToShowException;
import exception.ReviewAlreadyInserted;
import exception.UserNotPresentException;
import exception.WineNotExistsException;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.unwind;

/**
 * Contains all the crud operation that could be done on MongoDB.
 */
public class Crud_mongo {

    //TO FIX - Wine with same title but different variety or country or provinice or price (or maybe same title but all  different
    // aforementioned attributes MUST be stored in same document
    public void createWine(String title, String variety, String country, String province, String designation, int price, String taster_name, int points,
                           String description, String winery, String taster_twitter_handle, String country_user, String mail) throws ReviewAlreadyInserted {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        AggregateIterable<Document> wineq = collection.aggregate(Arrays.asList(new Document("$group", new Document("_id", new Document("wineName", "" + title + "")))));
        for (Document dbObject : wineq) {
            Document wine = (Document) dbObject.get("_id");
            if (wine.get("wineName") != null) {
                BasicDBObject query = new BasicDBObject("wineName", wine.get("wineName"));
                MongoCursor<Document> cursor = collection.find(query).iterator();
                // wine document not found , do insertion
                if (!cursor.hasNext()) {
                    // find all reviews belong to that title and add them
                    List<Document> reviews=new ArrayList<>();
                    Document rev= new Document("rating",  points )
                            .append("description", "" + description + "")
                            .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                            .append("taster_name", "" + taster_name + "")
                            .append("country", "" + country_user + "")
                            .append("email", "" + mail + "");
                    reviews.add(rev);


                    Document mongoWine = new org.bson.Document("wineName", title)
                            .append("variety", variety).append("country",country).append("province",province).append("price",price).append("winery", winery).append("designation",designation).append("reviews", reviews);
                    collection.insertOne(mongoWine);

                }
                // wine document is already inserted
                else {
                    Document wineDocument=cursor.next();
                    wineDocument.put("variety",variety);
                    wineDocument.put("country",country);
                    wineDocument.put("province",province);
                    wineDocument.put("price",price);
                    wineDocument.put("winery",winery);
                    wineDocument.put("designation",designation);


                    List<Document> reviews=(List<Document>)   wineDocument.get("reviews");
                    boolean isReviewInserted=false;
                    for (Document review:reviews) {
                        if(review.get("taster_name").toString().equals(taster_name)) {
                            isReviewInserted=true;
                            break;
                        }
                    }
                    // the review is not inserted yet, do insertion
                    if(!isReviewInserted) {
                        Document review= new Document("rating",  points )
                                .append("description", "" + description + "")
                                .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                                .append("taster_name", "" + taster_name + "")
                                .append("country", "" + country_user + "")
                                .append("email", "" + mail + "");
                        reviews.add(review);
                        wineDocument.put("reviews",reviews);
                        // update and save
                        Document update = new Document();

                        update.append("$set", wineDocument);
                        collection.updateOne(query, update);
                    }else {
                        // throw Already Inserted exception
                        System.out.println("The review is already inserted for that wine");
                        throw new ReviewAlreadyInserted("The review is already inserted for that wine");
                    }
                }
            }
        }
    }

    //WORKS
    public void deleteWine (String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        collection.deleteMany(Filters.eq("wineName", "" + title + ""));
        System.out.println("Wine deleted successfully");
    }

    //WORKS
    public void deleteComment (String description, String taster_name, String title){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        BasicDBObject match = new BasicDBObject("wineName", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name));
        collection.updateOne(match, new BasicDBObject("$pull", update));
        System.out.println("Comment " + description + " deleted successfully");
    }

    //WORKS
    public void addComment (String title, String taster_name, int score, String description, String taster_twitter_handle, String country, String email, Boolean admin) throws WineNotExistsException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Document myDoc = collection.find(Filters.eq("wineName", title)).first();
        if (myDoc == null)
            throw new WineNotExistsException(title + " doesn't exists");

        BasicDBObject match = new BasicDBObject("wineName", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name).append("rating",score)
                .append("taster_twitter_handle",taster_twitter_handle).append("country",country).append("email",email));
        collection.updateOne(match, new BasicDBObject("$push", update));
        System.out.println("Comment " + description + " added successfully");
    }

    //WORK
    public void deleteAllCommentForGivenUser (String taster_name) throws UserNotPresentException {
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
        System.out.println("All comments of " + taster_name + " deleted successfully");
    }


    public ArrayList<User> findAllUser(){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        User user = null;
        ArrayList<User> users = new ArrayList<>();
        Bson unwind = unwind("$reviews");
        Document group=new Document("$group", new Document("_id", new Document("taster_name", "$reviews.taster_name").append("taster_twitter_handle", "$reviews.taster_twitter_handle").append("country","$reviews.user_country").append("email","$reviews.email")));

        AggregateIterable<Document>  results = collection.aggregate(Arrays.asList(unwind,group));

       // List<String> cursor =(List<String>)  collection.distinct("reviews.taster_name", String.class);

        for (Document result:results ){


            Document temp_user_doc =(Document) result.get("_id");
            String username = temp_user_doc.getString("taster_name");
            String twitter_taster_handle = temp_user_doc.getString("taster_twitter_handle");
            String country = temp_user_doc.getString("country");
            String email = temp_user_doc.getString("email");
            user = new User(username,"",twitter_taster_handle,country,email,false);
            users.add(user);
        }
        mongoClient.close();
        return users;

    }
    public ArrayList<Review> findAllReview (){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Review review = null;
        ArrayList<Review> reviews = new ArrayList<>();
        Bson unwind = unwind("$reviews");

        AggregateIterable<Document> cursor = collection.aggregate(Arrays.asList(unwind));

        for (Document tempReview:cursor ){

            Document nestedReview=(Document) tempReview.get("reviews");

            Integer rating= nestedReview.getInteger("rating");
            String description = nestedReview.getString("description");

            review = new Review(description,rating);
            reviews.add(review);
        }
        mongoClient.close();
        return reviews;
    }

    public ArrayList<Wine> findAllWine (){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Wine wine = null;
        ArrayList<Wine> wines = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()){
            Document temp_wine_doc = cursor.next();
            String wineName = temp_wine_doc.getString("wineName");
            String variety = temp_wine_doc.getString("variety");
            String country = temp_wine_doc.getString("country");
            String province = temp_wine_doc.getString("province");
            Integer price = temp_wine_doc.getInteger("price");
            String winery = temp_wine_doc.getString("winery");
            String designation = temp_wine_doc.getString("designation");
            wine = new Wine(wineName,designation,price,province,variety,winery,country);
            wines.add(wine);
        }
        mongoClient.close();
        return wines;
    }
}




