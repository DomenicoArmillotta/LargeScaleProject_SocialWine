package login;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Class that works to store all the user credentials in a MongoDB collection
 */
public class DistinctUsers {

    /**
     * The method will take from the review collection in MongoDB the distinct users
     * and will give them a passowrd and will store the data (username and password)
     * in a MongoDB collection called user_credentials. More, the method will check
     * if that username and password are already inside the collection in order to
     * don't add them again.
     */
    public void distinctUser() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        MongoCollection<Document> newCollection = database.getCollection("user_credentials");
        try {
            DistinctIterable<String> docs = collection.distinct("taster_name",String.class);
            MongoCursor<String> results = docs.iterator();
            while (results.hasNext()) {
                String user_name=results.next();
                BasicDBObject query = new BasicDBObject("Name", user_name);

                FindIterable<Document> cursor = newCollection.find(query);
                if( (cursor.iterator().hasNext())) {
                } else {
                    Document doc = new Document("_id",new ObjectId());
                    doc.append("Name",user_name);
                    doc.append("Password","abcd");
                    try {
                        newCollection.insertOne(doc);
                    } catch (MongoWriteException mwe) {
                        if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                            System.out.println("Document with that id already exists");
                        }
                    }
                }

            }
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }
    }
}



