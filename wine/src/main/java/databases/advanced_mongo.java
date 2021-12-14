package databases;

//here there are all the advanced query with aggregation
//top k user with average highest of review

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

public class advanced_mongo {

    //Top 10 countries that have most wineries in descending order
    //WORK
    public void topTenCountriesWineries() {
        MongoClient mongoClient = MongoClients.create();
        //i use the Database = "wine" and collection = "review"
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        // {"country": "US" , "wineries" : ["winery1" , "winery2" , "winery3"]}
        Bson firstGroup = group("$country", Accumulators.addToSet("wineries", new Document("wineries","$winery")));
        // {""country": "US" , "wineries" : "winery1"}
        // {""country": "US" , "wineries" : "winery2"}
        // {""country": "US" , "wineries" : "winery3"}
        Bson unwind = unwind("$wineries");
        //  {"_id" : "US" , "wineryCount" : 3}
        Bson secondGroup = group("$_id", sum("wineryCount",1));
        //sort to extraxt the top 10
        Bson sort = sort(descending("wineryCount"));
        //limit to extraxt top 10
        Bson limit = limit(10);

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }

    //Display top-20 wines' varieties according to their mean price
    //WORK
    public void topTwentyVarietiesAvgPrice() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        // {"variety" : "variety1" , "prices" : ["1" , "2" , "3"]}
        Bson firstGroup = group("$variety", Accumulators.addToSet("prices", "$price"));
        // {"variety" : "variety1" , "prices" : "1"}
        // {"variety" : "variety1" , "prices" : "2"}
        // {"variety" : "variety1" , "prices" : "3"}
        Bson unwind = unwind("$prices");
        // {"_id" : "variety1" , "avgPrice" : 2}
        Bson secondGroup = group("$_id", avg("avgPrice","$prices"));
        //used to sort and extraxt top 20 wine variety with avg price highter
        Bson sort = sort(descending("avgPrice"));
        Bson limit = limit(20);

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }




    //Top-5 users with the highest average of them review scores.
    //WORK
    public void topFiveUsersHighestAvgScores(){
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        //iterator of all collection
        MongoCursor<Document> cursor = collection.find().cursor();
        while (cursor.hasNext()){
            Document cur = cursor.next();
            String id = cur.get("_id").toString();
            String pts = cur.get("points").toString();
            //convert to string into int to do avg operation of all collection
            int updatePts = Integer.parseInt(pts);


            BasicDBObject updateQuery = new BasicDBObject();
            updateQuery.append("$set", new BasicDBObject().append("points", updatePts));
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", new ObjectId(id));
            collection.updateMany(searchQuery, updateQuery);
        }
        // {"taster_name" : "name1" , "avg" : "2"}
        Bson group = group("$taster_name",avg("avg","$points"));
        //used to extract top 5 user in descending order
        Bson sort = sort(descending("avg"));
        Bson limit = limit(5);
        List<Document> results = collection.aggregate(Arrays.asList(group,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }



}

