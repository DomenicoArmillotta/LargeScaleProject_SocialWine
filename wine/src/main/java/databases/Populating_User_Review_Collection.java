package databases;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.merge;
import static com.mongodb.client.model.Projections.*;
import static java.util.Collections.singletonList;

public class Populating_User_Review_Collection {

public void poplulateData()
{
    final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
    MongoDatabase database = mongoClient.getDatabase("wine");
    MongoCollection<Document> collection = database.getCollection("review");
    MongoCollection<Document> usersCollection = database.getCollection("user");
    AggregateIterable<Document> output = collection.aggregate(Arrays.asList( new Document("$group",new Document("_id",new Document("taster_name", "$taster_name").append("taster_twitter_handle", "$taster_twitter_handle")))));
    for (Document dbObject : output)
    {
        //System.out.println(dbObject.get("_id"));
        //System.out.println(dbObject);
        Document user =(Document) dbObject.get("_id");

        if(user.get("taster_name") !=null )
        {
            // to check if the document is already inserted
            BasicDBObject query = new BasicDBObject();
            List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
            obj1.add(new BasicDBObject("Name", user.get("taster_name")));
            obj1.add(new BasicDBObject("twitter_name", user.get("taster_twitter_handle")));

            query.put("$and", obj1);
            //System.out.println(query.toString());
            MongoCursor<Document> cursor = usersCollection.find(query).iterator();
            // document not found , do insertion
            if( !cursor.hasNext())
            {

                // find all reviews belong to that taster and add them
                query = new BasicDBObject("taster_name", user.get("taster_name"));
                //var review_cursor = usersCollection.find(query).projection(fields(include("item", "status")));
                var cursor1 = collection.find(query).projection(fields(exclude("taster_twitter_handle","taster_name")));
               //var x= cursor.next().get("points");


                Document mongouser = new org.bson.Document("Name", user.get("taster_name"))
                        .append("twitter_name",user.get("taster_twitter_handle")).append("Password", "abcd").append("reviews",cursor1);
                usersCollection.insertOne(mongouser);

            }







        }




    }



}
}
