package databases;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import exception.ResultsNotFoundException;
import exception.WrongInsertionException;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.text.DecimalFormat;
import java.util.*;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * This class contains MongoDB advanced queries made with aggregation pipeline.
 */
public class Advanced_mongo {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    MongoClient mongoClient = MongoClients.create();
    MongoDatabase database = mongoClient.getDatabase("Wines");
    MongoCollection<Document> collection = database.getCollection("wines");
    /**
     * 1 Query: Shows the Top-3 countries that own higher average wines' ratings
     */
    public void topFiveCountryAccordingRating() {
        //MongoClient mongoClient = MongoClients.create("mongodb://172.16.4.79:27020,172.16.4.80:27020/"+ "?retryWrites=true&w=W1&readPreference=nearest&wtimeout=5000");
        Bson limit = limit(3);
        Bson sort = sort(descending("Average"));
        Bson unwind = unwind("$reviews");
        Bson group = group("$country", avg("Average", "$reviews.rating"));
        List<Document> results = collection.aggregate(Arrays.asList(unwind, group, sort, limit)).into(new ArrayList<>());
        System.out.println("=====TOP FIVE COUNTRY ACCORDING AVERAGE RATING=====");
        if (!results.iterator().hasNext()) {
            try {
                throw new ResultsNotFoundException("The are no items for this query!");
            } catch (ResultsNotFoundException rex) {
                System.out.println(rex.getMessage());
                System.out.println("==================================================");
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                String countryName = (String) results.get(i).get("_id");
                Double avg = results.get(i).getDouble("Average");
                System.out.println("Country: " + countryName + " --- Average: " + df.format(avg));
            }
            System.out.println("==================================================");
        }
    }

    /**
     * 2 Query: Shows Top-10 username ,with wine varieties and prices, that made highest number of reviews per variety
     */
    public void topTenUsersMadeHighestumberOfReveiwsPerVarieties() {
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
                System.out.println("================================================================");

            }
        } else {
            System.out.println("=======TOP 10 USERS THAT MADE HIGHEST NUMBER OF REVIEWS PER VARIETIES=======");
            for (int i = 0; i < results.size(); i++) {
                Document doc = (Document) results.get(i).get("_id");
                Integer count = results.get(i).getInteger("count");
                System.out.println("Name: " + doc.get("taster_name") + " --- Variety: " + doc.get("variety") + " .....Count: " + count);
            }
            System.out.println("================================================================");
        }

    }

    /**
     * 3 Query: Shows Top-50 wines, wines' prices, highest average rating, below a price treshold inserted by admin by keyboard
     */
    public void topFiftyWinesWithPriceLowerThan() {
        System.out.println("=========TOP 50 WINES WITH PRICE LOWER THAN A TRESHOLD AND HIGHEST AVERAGE RATING FIXED BY ADMIN========");
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
                    System.out.println("=================================================================");
                }
            } else {
                Bson limit = limit(50);
                Bson filter = Filters.lt("price", treshold);
                Bson unwind = unwind("$reviews");
                Bson match = match(filter);
                Bson sort = sort(descending("Average"));
                List<Object> list1 = new BasicDBList();
                list1.add(new BasicDBObject("wineName", "$wineName"));
                list1.add(new BasicDBObject("price", "$price"));
                Bson group = group(list1, avg("Average", "$reviews.rating"));
                AggregateIterable<Document> results = collection.aggregate(Arrays.asList(match, unwind, group,limit, sort));
                if (!results.iterator().hasNext()) {
                    try {
                        throw new ResultsNotFoundException("The are no items for this query!");
                    } catch (ResultsNotFoundException rex) {
                        System.out.println(rex.getMessage());
                    }
                }
                for (Document doc : results) {
                    ArrayList<Document> wine = (ArrayList<Document>) doc.get("_id");
                    System.out.println("Wine name: " + wine.get(0).get("wineName") + "\n" + "Wine's price: " + wine.get(1).get("price") + "\n" + "Wine's rating: " + doc.get("Average").toString() +"\n");
                }
                System.out.println("=================================================================");
            }
        } catch (InputMismatchException ixe) {
            System.out.println("You have to insert a number not a string");
            System.out.println("=================================================================");
        }
    }
}
