package databases;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class crud_mongo {

    //find
    //create done
    //delete
    //update


    //insert the review inside the collection review
    public void createReview (String points , String title , String description , String taster_name , String taster_twitter_handle, int price , String designation, String variety , String region_1 , String region_2 , String province , String country , String winery   ) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("winery");
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
        mongoClient.close();
    }
}




