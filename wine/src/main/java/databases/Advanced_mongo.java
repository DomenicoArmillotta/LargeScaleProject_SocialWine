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

    /**
     * Top ten countries that own most wineries.
     */
    public void topTenCountriesWineries() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Bson firstGroup = group("$country", Accumulators.addToSet("wineries", new Document("wineries", "$winery")));
        Bson unwind = unwind("$wineries");
        Bson secondGroup = group("$_id", sum("wineryCount", 1));
        Bson sort = sort(descending("wineryCount"));
        Bson limit = limit(10);

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup, unwind, secondGroup, sort, limit)).into(new ArrayList<>());
        results.forEach(doc -> System.out.println(doc.toJson()));
    }

    /**
     * Display to twenty wines' varietis according to thei mean price.
     */
    public void topTwentyVarietiesAvgPrice() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Bson firstGroup = group("$variety", Accumulators.addToSet("prices", "$price"));
        Bson unwind = unwind("$prices");
        Bson secondGroup = group("$_id", avg("avgPrice", "$prices"));
        Bson sort = sort(descending("avgPrice"));
        Bson limit = limit(20);

        List<Document> results = collection.aggregate(Arrays.asList(firstGroup, unwind, secondGroup, sort, limit)).into(new ArrayList<>());
        results.forEach(doc -> System.out.println(doc.toJson()));
    }

    /**
     * Top five users with the highest aerage of them review scores.
     */
    public void topFiveUsersHighestAvgScores() throws NumberFormatException {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        MongoCursor<Document> cursor = collection.find().cursor();
        while (cursor.hasNext()) {
            Document cur = cursor.next();
            String id = cur.get("_id").toString();
            Object pts = cur.get("points");
            if (pts instanceof Integer) {
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
        Bson group = group("$taster_name", avg("avg", "$points"));
        Bson sort = sort(descending("avg"));
        Bson limit = limit(5);
        List<Document> results = collection.aggregate(Arrays.asList(group, sort, limit)).into(new ArrayList<>());
        results.forEach(doc -> System.out.println(doc.toJson()));
    }

}

