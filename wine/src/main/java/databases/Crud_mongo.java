package databases;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import exception.UserNotPresentException;
import exception.WineNotExistsException;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * Contains all the crud operation that could be done on MongoDB.
 */
public class Crud_mongo {

    public void createWine(String title, String variety, String country, String province, int price, String taster_name, String points,
                           String description, String taster_twitter_handle, String country_user, String e_address, Boolean admin) throws UserAlreadyPresentException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Document wine = new Document("title", "" + title + "" )
                .append("title",""+ title + "")
                .append("variety", "" + variety + "")
                .append("country", "" + country + "")
                .append("province", "" + province + "")
                .append("price", "" + price + "");

        Document user = new Document("taster_name", "" + taster_name + "")
                .append("score", "" + points + "")
                .append("description", "" + description + "")
                .append("taster_twitter_handle", "" + taster_twitter_handle + "")
                .append("country", "" + country_user + "")
                .append("email", "" + e_address + "")
                .append("admin", "" + admin + "");

        UpdateOptions options = new UpdateOptions().upsert(true);

        Bson filter = Filters.eq(wine);
        Bson setUpdate = Updates.push("wine_reviews", user);
        collection.updateMany(filter, setUpdate, options);
        System.out.println("Successfully inserted review. \n");
    }

    public void deleteWine (String title) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        collection.deleteMany(Filters.eq("_id.title", "" + title + ""));
        System.out.println("Wine deleted successfully");
    }


    public void deleteComment (String description, String taster_name, String title){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        BasicDBObject match = new BasicDBObject("_id.title", title);
        BasicDBObject update = new BasicDBObject("wine_reviews", new BasicDBObject("description", description).append("taster_name", taster_name));
        collection.updateOne(match, new BasicDBObject("$pull", update));
        System.out.println("Comment " + description + " deleted successfully");
    }

    public void addComment (String title, String taster_name, String score, String description, String taster_twitter_handle, String country, String email, Boolean admin) throws WineNotExistsException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Document myDoc = collection.find(Filters.eq("_id.title", title)).first();
        if (myDoc == null)
            throw new WineNotExistsException(title + " doesn't exists");

        BasicDBObject match = new BasicDBObject("_id.title", title);
        BasicDBObject update = new BasicDBObject("wine_reviews", new BasicDBObject("description", description).append("taster_name", taster_name).append("score",score)
                .append("taster_twitter_handle",taster_twitter_handle).append("country",country).append("email",email).append("admin",admin));
        collection.updateOne(match, new BasicDBObject("$push", update));
        System.out.println("Comment " + description + " added successfully");
    }

    public void deleteAllCommentForGivenUser (String taster_name) throws UserNotPresentException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        MongoCursor<Document> myDoc = collection.find(Filters.eq("wine_reviews.taster_name", taster_name)).cursor();
        if (myDoc.hasNext() == false){
            throw new UserNotPresentException(taster_name + " doesn't exists");
        }
        BasicDBObject match = new BasicDBObject("wine_reviews.taster_name", taster_name);
        BasicDBObject update = new BasicDBObject("wine_reviews", new BasicDBObject("taster_name", taster_name));
        collection.updateMany(match, new BasicDBObject("$pull", update));
        System.out.println("All comments of " + taster_name + " deleted successfully");
    }
}




