package databases;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains all the crud operation that could be done on MongoDB.
 */
public class Crud_mongo {

    //TO FIX - Wine with same title but different variety or country or provinice or price (or maybe same title but all  different
    // aforementioned attributes MUST be stored in same document
    public void createWine(String title, String variety, String country, String province, int price, String taster_name, int points,
                           String description, String winery, String taster_twitter_handle, String country_user, String mail, Boolean admin) throws ReviewAlreadyInserted {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        AggregateIterable<Document> wineq = collection.aggregate(Arrays.asList(new Document("$group", new Document("_id", new Document("title", "" + title + "")))));


        for (Document dbObject : wineq) {

            Document wine = (Document) dbObject.get("_id");

            if (wine.get("title") != null) {
                BasicDBObject query = new BasicDBObject("title", wine.get("title"));

                MongoCursor<Document> cursor = collection.find(query).iterator();

                System.out.println(query);
                // wine document not found , do insertion
                if (!cursor.hasNext()) {

                    // find all reviews belong to that title and add them
                    // var cursor1 = reviewCollection.find(query).projection(fields(exclude("title", "country", "variety", "province")));

                    /*AggregateIterable<Document> review = collection.aggregate(Arrays.asList(Aggregates.match(query),
                            new Document("$group", new Document("_id", new Document("score", "" + points + "")
                                    .append("price", "" + price + "")
                                    .append("description", "" + description + "")
                                    .append("winery","" + winery + "")
                                    .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                                    .append("taster_name", "" + taster_name + "")
                                    .append("country", "" + country_user + "")
                                    .append("email", "" + mail + "")
                                    .append("admin", "" + admin + "")))));*/

                    List<Document> Reviews=new ArrayList<>();

                    Document review= new Document("score",  points )
                            .append("price", price )
                            .append("description", "" + description + "")
                            .append("winery","" + winery + "")
                            .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                            .append("taster_name", "" + taster_name + "")
                            .append("country", "" + country_user + "")
                            .append("email", "" + mail + "")
                            .append("admin", admin );
                    Reviews.add(review);

                    // find all provinces for the wine






                    Document mongoWine = new org.bson.Document("title", title)
                            .append("variety", variety).append("country", country).append("province", province).append("reviews", Reviews);
                    collection.insertOne(mongoWine);

                }
                // wine document is already inserted
                else
                {
                    Document wineDocument=cursor.next();
                    wineDocument.put("country",country);
                    wineDocument.put("province",province);
                    wineDocument.put("variety",variety);

                    //BasicDBObject checkQuery = new BasicDBObject("reviews.taster_name", taster_name);
                    //MongoCursor<Document> cursor1 = collection.find(checkQuery).iterator();

                    List<Document> reviews=(List<Document>)   wineDocument.get("reviews");
                    boolean isReviewInserted=false;
                    for (Document review:reviews)
                    {
                        if(review.get("taster_name").toString().equals(taster_name))
                        {
                            isReviewInserted=true;
                            break;
                        }
                    }
                    // the review is not inserted yet, do insertion
                    if(!isReviewInserted)
                    {
                        Document review= new Document("score",  points )
                                .append("price", price )
                                .append("description", "" + description + "")
                                .append("winery","" + winery + "")
                                .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                                .append("taster_name", "" + taster_name + "")
                                .append("country", "" + country_user + "")
                                .append("email", "" + mail + "")
                                .append("admin", admin );
                        reviews.add(review);
                        wineDocument.put("reviews",reviews);
                        // update and save
                        Document update = new Document();

                        update.append("$set", wineDocument);
                        collection.updateOne(query, update);
                    }
                    else
                    {

                        // throw Already Inserted exception
                        System.out.println("The review is already inserted for that wine");
                        throw new ReviewAlreadyInserted("The review is already inserted for that wine");
                    }


                }
               /* if (!cursor.hasNext()) {
                    UpdateOptions options = new UpdateOptions().upsert(true);

                    Bson filter = Filters.eq(wine);
                    Bson setUpdate = Updates.push("reviews", user);
                    collection.updateMany(filter, setUpdate, options);
                    System.out.println("Successfully inserted review. \n");
                }*/
            }
        }
    }

    //WORKS
    public void deleteWine (String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        collection.deleteMany(Filters.eq("title", "" + title + ""));
        System.out.println("Wine deleted successfully");
    }

    //WORKS
    public void deleteComment (String description, String taster_name, String title){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        BasicDBObject match = new BasicDBObject("title", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name));
        collection.updateOne(match, new BasicDBObject("$pull", update));
        System.out.println("Comment " + description + " deleted successfully");
    }

    //WORKS
    public void addComment (String title, String taster_name, int score, String description, String taster_twitter_handle, String country, String email, Boolean admin) throws WineNotExistsException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Document myDoc = collection.find(Filters.eq("title", title)).first();
        if (myDoc == null)
            throw new WineNotExistsException(title + " doesn't exists");

        BasicDBObject match = new BasicDBObject("title", title);
        BasicDBObject update = new BasicDBObject("reviews", new BasicDBObject("description", description).append("taster_name", taster_name).append("score",score)
                .append("taster_twitter_handle",taster_twitter_handle).append("country",country).append("email",email).append("admin",admin));
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
}




