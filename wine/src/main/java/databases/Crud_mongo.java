package databases;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import exception.NoCountryToShowException;
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
                           String description, String winery, String taster_twitter_handle, String country_user, String mail, Boolean admin) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        AggregateIterable<Document> wineq = collection.aggregate(Arrays.asList(new Document("$group", new Document("_id", new Document("title", "" + title + "")
                .append("variety", "" + variety + "")
                .append("country", "" + country + "")
                .append("province", "" + province + "")))));


        for (Document dbObject : wineq) {

            Document wine = (Document) dbObject.get("_id");

            if (wine.get("title") != null) {
                BasicDBObject query = new BasicDBObject();
                List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
                obj1.add(new BasicDBObject("title", wine.get("title")));
                obj1.add(new BasicDBObject("variety", wine.get("variety")));
                obj1.add(new BasicDBObject("country", wine.get("country")));

                query.put("$and", obj1);
                MongoCursor<Document> cursor = collection.find(query).iterator();

                System.out.println(query);
                // document not found , do insertion
                if (!cursor.hasNext()) {

                    // find all reviews belong to that title and add them
                    // var cursor1 = reviewCollection.find(query).projection(fields(exclude("title", "country", "variety", "province")));

                    AggregateIterable<Document> review = collection.aggregate(Arrays.asList(Aggregates.match(query),
                            new Document("$group", new Document("_id", new Document("score", "" + points + "")
                                    .append("price", "" + price + "")
                                    .append("description", "" + description + "")
                                    .append("winery","" + winery + "")
                                    .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                                    .append("taster_name", "" + taster_name + "")
                                    .append("country", "" + country_user + "")
                                    .append("email", "" + mail + "")
                                    .append("admin", "" + admin + "")))));

                    List<Document> distinctReviews=new ArrayList<>();

                    for(Document dbObject2 : review) {
                        Document reviews = (Document) dbObject2.get("_id");
                        distinctReviews.add(reviews);
                    }

                    // find all provinces for the wine
                    MongoCursor<String> provinces = collection.distinct("province", query, String.class).iterator();

                    // convert to string list
                    List<String> provincesStrings = new ArrayList<String>();

                    try{
                        while(provinces.hasNext()){
                            provincesStrings.add(provinces.next());
                        }
                    }finally{
                        provinces.close();
                    }



                    Document mongoWine = new org.bson.Document("title", wine.get("title"))
                            .append("variety", wine.get("variety")).append("country", wine.get("country")).append("province", provincesStrings).append("reviews", distinctReviews);
                    collection.insertOne(mongoWine);

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




