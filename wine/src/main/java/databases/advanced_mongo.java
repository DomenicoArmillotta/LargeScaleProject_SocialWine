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
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Bson limit = limit(10);
        Bson sort = sort(descending("wineryCount"));
        Bson unwind = unwind("$wineries");
        Bson secondGroup = group("$_id", sum("wineryCount",1));
        Bson firstGroup = group("$country", Accumulators.addToSet("wineries", new Document("wineries","$winery")));

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }

    //Display top-20 wines' varieties according to their mean price
    //WORK
    public void topTwentyVarietiesAvgPrice() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Bson limit = limit(20);
        Bson sort = sort(descending("avgPrice"));
        Bson unwind = unwind("$prices");

        Bson firstGroup = group("$variety", Accumulators.addToSet("prices", "$price"));
        Bson secondGroup = group("$_id", avg("avgPrice","$prices"));
        List<Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }




    //Top-5 users with the highest average of them review scores.
    //WORK
    public void topFiveUsersHighestAvgScores(){
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        MongoCursor<Document> cursor = collection.find().cursor();
        while (cursor.hasNext()){
            Document cur = cursor.next();
            String id = cur.get("_id").toString();
            String pts = cur.get("points").toString();
            int updatePts = Integer.parseInt(pts);
            BasicDBObject updateQuery = new BasicDBObject();
            updateQuery.append("$set", new BasicDBObject().append("points", updatePts));
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", new ObjectId(id));
            collection.updateMany(searchQuery, updateQuery);
        }

        Bson limit = limit(5);
        Bson sort = sort(descending("avg"));
        Bson group = group("$taster_name",avg("avg","$points"));
        List<Document> results = collection.aggregate(Arrays.asList(group,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }



}

