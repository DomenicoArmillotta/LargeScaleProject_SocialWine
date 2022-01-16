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
        MongoCollection<Document> reviewCollection = database.getCollection("review");

        MongoCollection<Document> wineCollection = database.getCollection("wines");
        AggregateIterable<Document> output1 = reviewCollection.aggregate(Arrays.asList(new Document("$group", new Document("_id", new Document("title", "$title")))));

        for (Document dbObject : output1) {

            Document wine = (Document) dbObject.get("_id");

            if (wine.get("title") != null  ) {

                BasicDBObject query = new BasicDBObject("title", wine.get("title"));


                MongoCursor<Document> reviews = reviewCollection.find(query).iterator();
                Document lastReview=null;

                while(reviews.hasNext())
                {
                    lastReview =reviews.next();

                }

                   // to check if the document is already inserted
                //System.out.println(query.toString());
                MongoCursor<Document> cursor = wineCollection.find(query).iterator();
                // document not found , do insertion
                if (!cursor.hasNext()) {

                    // find all reviews belong to that title and add them
                   // var cursor1 = reviewCollection.find(query).projection(fields(exclude("title", "country", "variety", "province")));
                    Boolean s=false;

                    AggregateIterable<Document> output2 = reviewCollection.aggregate(Arrays.asList(Aggregates.match(query),
                            new Document("$group", new Document("_id", new Document("score", "$points").append("price","$price").append("description", "$description").append("taster_twitter_handle", "$taster_twitter_handle").append("taster_name","$taster_name").append("taster_twitter_handle","$taster_twitter_handle").append("country","None").append("email","None").append("admin","false")))));
                    List<Document> distinctReviews=new ArrayList<>();


                    for(Document tempReview:output2) {
                        Document review = (Document) tempReview.get("_id");
                        if (review.get("price")==null || review.get("score")==null || review.get("taster_name")==null|| review.get("taster_twitter_handle")==null||  review.get("description")==null )
                        {
                            continue;
                        }
                        else
                        {
                            review.put("score",Integer.parseInt(review.get("score").toString()));
                            review.put("admin",Boolean.parseBoolean(review.get("admin").toString()));
                            distinctReviews.add(review);
                        }


                    }



                    // find all provinces for the wine
                    /*MongoCursor<String> provinces = reviewCollection.distinct("province", query, String.class).iterator();

                    // convert to string list
                    List<String> provincesStrings = new ArrayList<String>();

                    try{
                        while(provinces.hasNext()){
                            provincesStrings.add(provinces.next());
                        }
                    }finally{
                        provinces.close();
                    }*/



                    Document mongoWine = new org.bson.Document("title", wine.get("title"))
                            .append("variety", lastReview.get("variety")).append("country", lastReview.get("country")).append("province", lastReview.get("province")).append("reviews", distinctReviews);
                    wineCollection.insertOne(mongoWine);

                }


            }


        }

    }
}
