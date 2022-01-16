package databases;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;

public class Populating_wine_document {
    public void poplulateData() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<Document> reviewCollection = database.getCollection("reviews");

        MongoCollection<Document> wineCollection = database.getCollection("wines");
        AggregateIterable<Document> output1 = reviewCollection.aggregate(Arrays.asList(new Document("$group", new Document("_id", new Document("title", "$title")))));

        for (Document dbObject : output1) {

            Document wine = (Document) dbObject.get("_id");

            if (wine.get("title") != null  ) {
                BasicDBObject query = new BasicDBObject("title", wine.get("title"));
                MongoCursor<Document> reviews = reviewCollection.find(query).iterator();

                Document lastReview = null;
                while(reviews.hasNext()) {
                    lastReview =reviews.next();
                }

                   // to check if the document is already inserted
                MongoCursor<Document> cursor = wineCollection.find(query).iterator();
                // document not found , do insertion
                if (!cursor.hasNext()) {
                    // find all reviews belong to that title and add them
                    AggregateIterable<Document> output2 = reviewCollection.aggregate(Arrays.asList(Aggregates.match(query),
                            new Document("$group", new Document("_id", new Document("rating", "$points").append("description", "$description").append("taster_twitter_handle", "$taster_twitter_handle").append("taster_name","$taster_name").append("taster_twitter_handle","$taster_twitter_handle").append("user_country","None").append("email","None")))));
                    List<Document> distinctReviews=new ArrayList<>();


                    for(Document tempReview:output2) {
                        Document review = (Document) tempReview.get("_id");
                        if (review.get("rating")==null || review.get("taster_name")==null|| review.get("taster_twitter_handle")==null||  review.get("description")==null ) {
                            continue;
                        }else{
                            review.put("rating",Integer.parseInt(review.get("rating").toString()));
                            distinctReviews.add(review);
                        }
                    }
                    Document mongoWine = new org.bson.Document("wineName", wine.get("title"))
                            .append("variety", lastReview.get("variety"))
                            .append("country", lastReview.get("country"))
                            .append("province", lastReview.get("province"))
                            .append("price", lastReview.get("price"))
                            .append("winery", lastReview.get("winery"))
                            .append("designation",lastReview.get("designation"))
                            .append("reviews", distinctReviews);
                    if (mongoWine.get("designation")== null || mongoWine.get("price")== null|| mongoWine.get("wineName")== null || mongoWine.get("country")== null|| mongoWine.get("province")== null|| mongoWine.get("winery")== null||  mongoWine.get("variety")== null){
                        continue;
                    }else{
                        wineCollection.insertOne(mongoWine);
                    }
                }
            }
        }
    }
}
