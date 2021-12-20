package login;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;


public class DistinctUsers {

    public void distinctUser() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        MongoCollection<Document> newCollection = database.getCollection("user_credentials");
        try {
            DistinctIterable<String> docs = collection.distinct("taster_name",String.class);
            MongoCursor<String> results = docs.iterator();
            while (results.hasNext()) {
                Document doc = new Document("_id",new ObjectId());
                doc.append("Name",results.next());
                doc.append("Password","abcd");
                try {
                    newCollection.insertOne(doc);
                } catch (MongoWriteException mwe) {
                    if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                        System.out.println("Document with that id already exists");
                    }
                }
            }
            System.out.println("Success!");
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }

    }

}



