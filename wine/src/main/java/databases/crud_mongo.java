package databases;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.*;


public class crud_mongo {

    //find review by winery review DONE
    //create review DONE
    //delete all review by taster_name DONE
    //update all price under a threshold DONE


    //insert the review inside the collection review
    public void createReview (String points , String title , String description , String taster_name , String taster_twitter_handle, int price , String designation, String variety , String region_1 , String region_2 , String province , String country , String winery   ) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Document doc = new Document("points" , "\"" + points + " \"")
                .append("title" , "\"" + title + " \"")
                .append("description" , "\"" + description + " \"")
                .append("taster_name" , "\"" + taster_name + " \"")
                .append("taster_twitter_handle" , "\"" + taster_twitter_handle + " \"")
                .append("price" , "\"" + price + " \"")
                .append("designation" , "\"" + designation + " \"")
                .append("variety" , "\"" + variety + " \"")
                .append("region_1" , "\"" + region_1 + " \"")
                .append("region_2" , "\"" + region_2 + " \"")
                .append("province" , "\"" + province + " \"")
                .append("country" , "\"" + country + " \"")
                .append("winery" , "\"" + winery + " \"");
        collection.insertOne(doc);
        mongoClient.close();
    }

    public  void findReviewByWinery (String winery){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Bson query = eq("winery" ,  winery);
        MongoCursor<Document> cursor = collection.find(query).iterator();
        while (cursor.hasNext()){
            System.out.println(cursor.next().toJson());
        }
        mongoClient.close();
    }

    public void deleteReviewsByTaster_Name (String taster_name) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Bson query = eq("taster_name" ,  taster_name);
        DeleteResult deleteResult = collection.deleteMany(query);
        System.out.println("Sono state eliminate "  + deleteResult.getDeletedCount() + "reviews");
    }

    public void updateAllPrice (int selectOldPrice , int newPrice) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        BasicDBObject set = new BasicDBObject("price", newPrice);
        UpdateResult updateResult = collection.updateMany(lt("price", selectOldPrice), set);
        System.out.println(updateResult.getModifiedCount());
    }

}




