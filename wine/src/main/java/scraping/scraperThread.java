package scraping;

import com.mongodb.*;


import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import org.apache.poi.xssf.usermodel.XSSFRow;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Sorts.descending;

public class scraperThread implements Runnable{
    @Override
    public void run() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("wine");
        DBCollection collection = database.getCollection("review");

        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.winemag.com/?s=&drink_type=wine&page=1&sort_by=pub_date_web&sort_dir=desc").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> links = doc.getElementsByClass("review-listing").eachAttr("href");
        for(String link : links) {
            HashMap<String, String> map = new HashMap<String, String>();
            Document reviewDoc = null;
            try {
                reviewDoc = Jsoup.connect(link).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            Element tasterElement = reviewDoc.getElementsByClass("taster-area").first().getElementsByTag("a").first();
            // check for empty string
            String taster_name = CheckEmpty(tasterElement.text());
            map.put("taster_name",taster_name);
            Document tasterDoc = null;
            try {
                tasterDoc = Jsoup.connect(tasterElement.attr("href")).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String taster_twitter=CheckEmpty(tasterDoc.getElementsByClass("twitter").first().text());
            map.put("taster_twitter",taster_twitter);




            map.put("title", reviewDoc.getElementsByClass("header__title").first().getElementsByTag("h1").first().text());
            map.put("description", reviewDoc.getElementsByClass("description").first().text());
            Element attribuiteTable = reviewDoc.select("ul.primary-info").first();
            Elements rows = attribuiteTable.select("li.row");
            for (Element row : rows) {
                String label = row.getElementsByClass("info-label").first().text();
                String value = row.getElementsByClass("info").first().text();
                map.put(label, value);
            }
            if (map.containsKey("Price")) {
                String z = map.get("Price").split(",")[0].substring(1);
                map.replace("Price", map.get("Price").split(",")[0].substring(1));
            } else {
                map.put("Price", "");
            }
            if (map.containsKey("Appellation")) {
                String[] Address = map.get("Appellation").replaceAll("\\s+", "").split(",");
                if (Address.length == 3) {
                    map.put("region1", Address[0]);
                    map.put("region2", null);
                    map.put("Province", Address[1]);
                    map.put("Country", Address[2]);
                } else if (Address.length == 4) {
                    map.put("region1", Address[0]);
                    map.put("region2", Address[1]);
                    map.put("Province", Address[2]);
                    map.put("Country", Address[3]);

                } else {
                    map.put("region1", null);
                    map.put("region2", null);
                    map.put("Province", Address[0]);
                    map.put("Country", Address[1]);
                }
            }
            BasicDBObject query = new BasicDBObject();
            List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
            obj1.add(new BasicDBObject("title", map.get("title")));
            obj1.add(new BasicDBObject("taster_name", map.get("taster_name")));
            obj1.add(new BasicDBObject("variety",map.get("Variety") ));
            obj1.add(new BasicDBObject("winery", map.get("Winery")));
            query.put("$and", obj1);
            //System.out.println(query.toString());
            DBCursor cursor = collection.find(query);
            if( (cursor.count()>= 1))
            {
                System.out.println("Not empty Cursor");
                DBObject currentObject= cursor.next();


                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("points", map.get("rating"));
                newDocument.put("description", map.get("description"));
                newDocument.put("title", map.get("title"));

                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", newDocument);

                collection.update(currentObject, updateObject);


            }
            else
            {
                System.out.println("empty Cursor");
                DBObject person = new BasicDBObject("points", map.get("rating"))
                        .append("title", map.get("title"))
                        .append("description", map.get("description"))
                        .append("taster_name", map.get("taster_name"))
                        .append("taster_twitter_handle", map.get("taster_twitter"))
                        .append("price", parseIntOrNull(map.get("Price")))
                        .append("designation", map.get("Designation"))
                        .append("variety", map.get("Variety"))
                        .append("region_1", map.get("region1"))
                        .append("region_2", map.get("region2"))
                        .append("province", map.get("Province"))
                        .append("country", map.get("Country"))
                        .append("winery", map.get("Winery"));
                collection.insert(person);
            }
        }

    }

    private String CheckEmpty(String input) {

        if(input.isEmpty())
        {
            return null;
        }
        return input;
    }

    public Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static void fillSheet(HashMap<String, String> map, String hashValue, XSSFRow row) {
        row.createCell(0).setCellValue(map.get("Country"));
        row.createCell(1).setCellValue(map.get("description"));
        row.createCell(2).setCellValue(map.get("Designation"));
        row.createCell(3).setCellValue(map.get("rating"));
        row.createCell(4).setCellValue(map.get("Price"));
        row.createCell(5).setCellValue(map.get("Province"));
        row.createCell(6).setCellValue(map.get("region1"));
        row.createCell(7).setCellValue(map.get("region2"));
        row.createCell(8).setCellValue(map.get("Variety"));
        row.createCell(9).setCellValue(map.get("Winery"));
        row.createCell(10).setCellValue(map.get("title"));
        row.createCell(11).setCellValue(map.get("taster_name"));
        row.createCell(12).setCellValue(map.get("taster_twitter"));
        row.createCell(13).setCellValue(hashValue);
    }

    //Top 10 countries that have most wineries in descending order
    //WORK
    public void topTenCountriesWineries() {
        com.mongodb.client.MongoClient mongoClient = MongoClients.create();
        //i use the Database = "wine" and collection = "review"
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<org.bson.Document> collection = database.getCollection("review");

        // {"country": "US" , "wineries" : ["winery1" , "winery2" , "winery3"]}
        Bson firstGroup = group("$country", Accumulators.addToSet("wineries", new org.bson.Document("wineries","$winery")));
        // {""country": "US" , "wineries" : "winery1"}
        // {""country": "US" , "wineries" : "winery2"}
        // {""country": "US" , "wineries" : "winery3"}
        Bson unwind = unwind("$wineries");
        //  {"_id" : "US" , "wineryCount" : 3}
        Bson secondGroup = group("$_id", sum("wineryCount",1));
        //sort to extraxt the top 10
        Bson sort = sort(descending("wineryCount"));
        //limit to extraxt top 10
        Bson limit = limit(10);

        List<org.bson.Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }

    //Display top-20 wines' varieties according to their mean price
    //WORK
    public void topTwentyVarietiesAvgPrice() {
        com.mongodb.client.MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<org.bson.Document> collection = database.getCollection("review");

        // {"variety" : "variety1" , "prices" : ["1" , "2" , "3"]}
        Bson firstGroup = group("$variety", Accumulators.addToSet("prices", "$price"));
        // {"variety" : "variety1" , "prices" : "1"}
        // {"variety" : "variety1" , "prices" : "2"}
        // {"variety" : "variety1" , "prices" : "3"}
        Bson unwind = unwind("$prices");
        // {"_id" : "variety1" , "avgPrice" : 2}
        Bson secondGroup = group("$_id", avg("avgPrice","$prices"));
        //used to sort and extraxt top 20 wine variety with avg price highter
        Bson sort = sort(descending("avgPrice"));
        Bson limit = limit(20);

        List<org.bson.Document> results = collection.aggregate(Arrays.asList(firstGroup,unwind,secondGroup,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }




    //Top-5 users with the highest average of them review scores.
    //WORK
    public void topFiveUsersHighestAvgScores(){
        com.mongodb.client.MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<org.bson.Document> collection = database.getCollection("review");

        //iterator of all collection
        MongoCursor<org.bson.Document> cursor = collection.find().cursor();
        while (cursor.hasNext()){
            org.bson.Document cur = cursor.next();
            String id = cur.get("_id").toString();
            String pts = cur.get("points").toString();
            //convert to string into int to do avg operation of all collection
            int updatePts = Integer.parseInt(pts);

            //set the attribute to replace in this query "POINTS"
            BasicDBObject updateQuery = new BasicDBObject();
            updateQuery.append("$set", new BasicDBObject().append("points", updatePts));
            //set the attribute to search in this query "_id"
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", new ObjectId(id));
            //make the update query with the update and searchquery
            collection.updateMany(searchQuery, updateQuery);
        }
        // {"taster_name" : "name1" , "avg" : "2"}
        Bson group = group("$taster_name",avg("avg","$points"));
        //used to extract top 5 user in descending order
        Bson sort = sort(descending("avg"));
        Bson limit = limit(5);
        List<org.bson.Document> results = collection.aggregate(Arrays.asList(group,sort,limit)).into(new ArrayList<>());
        results.forEach( doc -> System.out.println(doc.toJson()));
    }


}
