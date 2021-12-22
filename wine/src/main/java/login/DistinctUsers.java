package login;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
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
                String user_name=results.next();
                //checking if the user already inserted
                BasicDBObject query = new BasicDBObject("Name", user_name);

                FindIterable<Document> cursor = newCollection.find(query);
                if( (cursor.iterator().hasNext()))
                {
                    System.out.println("Not empty Cursor");
                }
                else
                {
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
            System.out.println("Success!");
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }

    }

}



