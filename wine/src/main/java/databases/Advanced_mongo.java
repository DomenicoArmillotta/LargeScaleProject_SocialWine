package databases;


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

/**
 * This class contains MongoDB advanced queries made with aggregation pipeline.
 */
public class Advanced_mongo {

}





































    /*
    public void topTenCountriesWineries() {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");
        MongoClient mongoClient = MongoClients.create();
        //select reviews collecton
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        //group with a field of country and a field with list of winery name
        Bson firstGroup = group("$country", Accumulators.addToSet("wineries", new Document("wineries", "$winery")));
        //with unwind operation we separate the winery list for create multiple row
        // sizes=[A,B,C] name = aaa
        //unwind operation
        // sizes=A name=aaa
        // sizes=B name=aaa
        // sizes=C name=aaa
        Bson unwind = unwind("$wineries");
        //we group and add one field name "wineryCount" where is the count for each country that is the "1" expression
        Bson secondGroup = group("$_id", sum("wineryCount", 1));
        //order in descending order by winery count
        Bson sort = sort(descending("wineryCount"));
        //limit to 10 the result
        Bson limit = limit(10);
        //i use aggregate to  process multiple documents and return result and isert them into arrayList
        List<Document> results = collection.aggregate(Arrays.asList(firstGroup, unwind, secondGroup, sort, limit)).into(new ArrayList<>());
        //we print the result
        results.forEach(doc -> System.out.println(doc.toJson()));
    }


    public void topTwentyVarietiesAvgPrice() {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        //selected the reviews collections
        MongoCollection<Document> collection = database.getCollection("review");

        //group with varietis and the price value
        Bson firstGroup = group("$variety", Accumulators.addToSet("prices", "$price"));
        //separate and create row with variety and price for each review
        Bson unwind = unwind("$prices");
        //insert new field name "avg_price" and calculate avg of price of varieties
        Bson secondGroup = group("$_id", avg("avgPrice", "$prices"));
        //sort descending by avg price
        Bson sort = sort(descending("avgPrice"));
        Bson limit = limit(20);

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup, unwind, secondGroup, sort, limit)).into(new ArrayList<>());
        results.forEach(doc -> System.out.println(doc.toJson()));
    }


    public void topFiveUsersHighestAvgScores() throws NumberFormatException {
        //final com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27020/\" + \"?retryWrites=true&w=majority&readPreference=nearest&wtimeout=10000");

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        //iterate on all reviews of mongo db collection
        //THIS operations is needed to convert POINTS from STRING to INT and do operation of average
        MongoCursor<Document> cursor = collection.find().cursor();
        while (cursor.hasNext()) {
            Document cur = cursor.next();
            String id = cur.get("_id").toString();
            Object pts = cur.get("points");
            if (pts instanceof Integer) {
                //do the casting from string to integer to do operations
                Integer ptsInt = (Integer) cur.get("points");
            } else if (pts instanceof String) {
                int updatePts = Integer.parseInt((String) pts);
                BasicDBObject updateQuery = new BasicDBObject();
                updateQuery.append("$set", new BasicDBObject().append("points", updatePts));
                BasicDBObject searchQuery = new BasicDBObject();
                searchQuery.put("_id", new ObjectId(id));
                collection.updateMany(searchQuery, updateQuery);
            } else {
                throw new NumberFormatException();
            }
        }
        //group with taster name and average of point in all reviews
        Bson group = group("$taster_name", avg("avg", "$points"));
        //sort in descending order
        Bson sort = sort(descending("avg"));
        //limit the result to 5
        Bson limit = limit(5);
        List<Document> results = collection.aggregate(Arrays.asList(group, sort, limit)).into(new ArrayList<>());
        results.forEach(doc -> System.out.println(doc.toJson()));
    }

    */



