package scraping;

import com.mongodb.*;

import databases.Crud_graph;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.*;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ScraperThread implements Runnable{

    @Override
    public void run() {

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "0000" ) );
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("wine");
        DBCollection collection = database.getCollection("reviewTest");
        DBCollection usersCollection = database.getCollection("user_credentials");

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
            // add users to mongoDb database
            query = new BasicDBObject("Name", map.get("taster_name"));
            System.out.println(query.toString());
             cursor = usersCollection.find(query);
            if( (cursor.count()>= 1))
            {
                System.out.println("Adding to MongoDB");
            }
            else
            {
                DBObject user = new BasicDBObject("Name", map.get("taster_name"))
                        .append("Password", "abcd");

                usersCollection.insert(user);
            }



            // add the new taster to the graph database
            Crud_graph crud = new Crud_graph("bolt://localhost:7687","neo4j","0000");

                crud.addUser(map.get("taster_name"));
                crud.addPostComplete(map.get("taster_name"),map.get("title"),map.get("description"),map.get("Winery"),map.get("Country"));

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
}
