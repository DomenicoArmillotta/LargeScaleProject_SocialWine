package databases;
import beans.Review;
import com.mongodb.*;
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
    //findReviewFromId DONE


    //insert the review inside the collection review
    public void createReview (String points , String title , String description , String taster_name , String taster_twitter_handle, int price , String designation, String variety , String region_1 , String region_2 , String province , String country , String winery   ) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Document doc = new Document("points" , "" + points + "")
                .append("title" , "" + title + "")
                .append("description" , "" + description + "")
                .append("taster_name" , "" + taster_name + "")
                .append("taster_twitter_handle" , "" + taster_twitter_handle + "")
                .append("price" , "" + price + "")
                .append("designation" , "" + designation + "")
                .append("variety" , "" + variety + "")
                .append("region_1" , "" + region_1 + "")
                .append("region_2" , "" + region_2 + "")
                .append("province" , "" + province + "")
                .append("country" , "" + country + "")
                .append("winery" , "" + winery + "");

        try {
            collection.insertOne(doc);
            System.out.println("Successfully inserted documents. \n");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
        mongoClient.close();
    }
    //find all review by name of winery
    public  void findReviewByWinery (String winery){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        Bson query = eq("winery" , "" + winery + "");
        try{
            MongoCursor<Document> cursor = collection.find(query).iterator();
            while (cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        }catch (MongoCursorNotFoundException mce){
            mce.printStackTrace();
        }
        mongoClient.close();
    }

    //delete all review by taster name
    public void deleteReviewsByTaster_Name (String taster_name) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        try {
            Bson query = eq("taster_name" , "" +  taster_name + "");
            DeleteResult deleteResult = collection.deleteMany(query);
            System.out.println("Document dropped successfully");
            System.out.println("Were dropped "  + deleteResult.getDeletedCount() + " reviews");
        } catch (MongoException me){
            me.printStackTrace();
        }
        mongoClient.close();

    }

    //update all price under a treshold with new price
    public void updateAllPrice (int selectOldPrice , int newPrice) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");
        try {
        BasicDBObject set = new BasicDBObject("price","" + newPrice + "");
        UpdateResult updateResult = collection.updateMany(lt("price", "" + selectOldPrice + ""), set);
        System.out.println("Document updated successfully");
        System.out.println(updateResult.getModifiedCount());
        } catch (MongoException me){
            me.printStackTrace();
        }
        mongoClient.close();

    }
    //find a review by _id and create the beans
    public Review findReviewFromId (String id){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("review");

        Review review = null;
        Bson query = eq("_id" , "" + id + "");
        MongoCursor<Document> cursor = collection.find(query).iterator();
        while (cursor.hasNext()){
            Document temp_review_doc = cursor.next();
            String points = temp_review_doc.getString("points");
            System.out.println(points);
            String title = temp_review_doc.getString("title");
            String description = temp_review_doc.getString("description");
            String taster_name = temp_review_doc.getString("taster_name");
            String taster_twitter_handle = temp_review_doc.getString("taster_twitter_handle");
            Integer price = temp_review_doc.getInteger("price");
            String designation = temp_review_doc.getString("designation");
            String variety = temp_review_doc.getString("variety");
            String region_1 = temp_review_doc.getString("region_1");
            String region_2 = temp_review_doc.getString("region_2");
            String province = temp_review_doc.getString("province");
            String country = temp_review_doc.getString("country");
            String winery = temp_review_doc.getString("winery");
            review = new Review(points,title,description,taster_name,taster_twitter_handle,price,designation,variety,region_1,region_2,province,country,winery);
        }
        mongoClient.close();
        return review;
    }


}




