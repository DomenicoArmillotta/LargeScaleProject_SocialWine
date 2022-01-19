package databases;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import exception.ResultsNotFoundException;
import exception.WrongInsertionException;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.text.DecimalFormat;
import java.util.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * This class contains MongoDB advanced queries made with aggregation pipeline.
 */
public class Advanced_mongo{
    Crud_mongo mongo = new Crud_mongo();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    /*
       db.wines.aggregate( [ { $unwind:"$reviews"}, { $group:{_id:"$country", Average:{$avg:"$reviews.rating"}}}, {$limit:3}])
     */
    public void topFiveCountryAccordingRating() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        Bson limit = limit(3);
        Bson sort = sort(descending("Average"));
        Bson unwind = unwind("$reviews");
        Bson group = group("$country", avg("Average", "$reviews.rating"));
        List<Document> results = collection.aggregate(Arrays.asList(unwind, group, sort, limit)).into(new ArrayList<>());
        System.out.println("*******************TOP FIVE COUNTRY ACCORDING AVERAGE RATING*******************");
        if (!results.iterator().hasNext()) {
            try {
                throw new ResultsNotFoundException("The are no items for this query!");
            } catch (ResultsNotFoundException rex) {
                System.out.println(rex.getMessage());
                System.out.println("*******************************************************************************" + "\n");
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                String countryName = (String) results.get(i).get("_id");
                Double avg = results.get(i).getDouble("Average");
                System.out.println("Country: " + countryName + " --- Average: " + df.format(avg) + "\n");
            }
            System.out.println("*******************************************************************************" + "\n");

        }
    }

    /*
        db.wines.aggregate( [ { $unwind:"$reviews"}, { $group:{_id: { taster_name : "$reviews.taster_name", variety : "$variety"}, count:{$sum:1}}},{$limit:10}])
     */
    public void topTenUsersMadeHighestumberOfReveiwsPerVarieties() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");

        Bson limit = limit(10);
        Bson sort = sort(descending("count"));
        Bson unwind = unwind("$reviews");
        Bson group = new Document("$group", new Document("_id", new Document("taster_name", "$reviews.taster_name").append("variety", "$variety")).append(
                "count", new Document("$sum", 1)));
        List<Document> results = collection.aggregate(Arrays.asList(unwind, group, sort, limit)).into(new ArrayList<>());
        if (!results.iterator().hasNext()) {
            try {
                throw new ResultsNotFoundException("The are no items for this query!");
            } catch (ResultsNotFoundException rex) {
                System.out.println(rex.getMessage());
                System.out.println("*******************************************************************************" + "\n");

            }
        } else {
            System.out.println("********TOP TEN USERS THAT MADE HIGHEST NUMBER OF REVIEWS PER VARIETIES********");
            for (int i = 0; i < results.size(); i++) {
                Document doc = (Document) results.get(i).get("_id");
                Integer count = results.get(i).getInteger("count");
                System.out.println("Taster_name: " + doc.get("taster_name") + " --- Variety: " + doc.get("variety") + " --- Count: " + count + "\n");
            }
            System.out.println("*******************************************************************************" + "\n");
        }

    }

    /*
    db.wines.aggregate( [ { $unwind : "$reviews" }, { "$match": { "price": { "$gte": userInput } } },
    { $project : { wineName : 1 , "reviews.taster_name" : 1 } } ] )
     */
    public void topThirtyWinesWithPriceLowerThan() {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> collection = database.getCollection("wines");
        System.out.println("********TOP TWENTY WINES WITH PRICE LOWER THAN A TRESHOLD FIXED BY ADMIN**********");
        System.out.println("Insert price treshold:");
        Scanner sc = new Scanner(System.in);
        try {
            Integer treshold = sc.nextInt();
            int len = Integer.toString(treshold).length();
            String tresh = Integer.toString(treshold);
            if (treshold < 0 || (Integer.toString(treshold)).length() > 4) {
                try {
                    throw new WrongInsertionException("You inserted a number below 0 or a price higher than 9.999");
                } catch (WrongInsertionException ime) {
                    System.out.println(ime.getMessage());
                    System.out.println("*******************************************************************************" + "\n");
                }
            } else {
                Bson limit = limit(30);
                Bson filter = Filters.lt("price", treshold);
                Bson unwind = unwind("$reviews");
                Bson match = match(filter);
                Bson project = Aggregates.project(Projections.fields(Projections.include("wineName", "price", "reviews.taster_name")));
                AggregateIterable<Document> results = collection.aggregate(Arrays.asList(unwind, match, project, limit));
                if (!results.iterator().hasNext()) {
                    try {
                        throw new ResultsNotFoundException("The are no items for this query!");
                    } catch (ResultsNotFoundException rex) {
                        System.out.println(rex.getMessage());
                    }
                }
                for (Document doc : results) {
                    Document review = (Document) doc.get("reviews");
                    System.out.println("Username: " + review.getString("taster_name") + "\n" + "Wine name: " + doc.getString("wineName") + "\n" + "Wine's price: " + doc.getInteger("price").toString() + "\n");
                }
                System.out.println("*******************************************************************************" + "\n");
            }
        } catch (InputMismatchException ixe) {
            System.out.println("You have to insert a number not a string");
            System.out.println("*******************************************************************************" + "\n");
        }
    }
}
