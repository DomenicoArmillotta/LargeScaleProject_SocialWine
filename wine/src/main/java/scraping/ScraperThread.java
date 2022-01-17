package scraping;

import com.mongodb.*;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import databases.Crud_graph;
import databases.Crud_mongo;
import exception.ReviewAlreadyInserted;
import exception.ServerWinmagOufOfServiceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.*;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains scraper that allow to retrieve new reviews from winemag website.
 * New reviews that are detected are stored automatically in MongoDB, after a check
 * to figure out if a review is already inside or not. Automatically are added
 * nodes in Neo4J with addPostComplete and in the same way in user-credentials' collection
 * are stored the new user with their password.
 */
public class ScraperThread implements Runnable{

    @Override
    public void run() {

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "0000" ) );
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        //com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27018,localhost:27019,localhost:27020/" + "?retryWrites=true&w=majority&wtimeout=10000");

        //ConnectionString uri= new ConnectionString("mongodb://localhost:27018");
     // MongoClientSettings mcs= MongoClientSettings.builder().applyConnectionString(uri).readPreference(ReadPreference.nearest()).retryWrites(true).writeConcern(WriteConcern.ACKNOWLEDGED).build();
     // com.mongodb.client.MongoClient mongoClient=  MongoClients.create(mcs);


        MongoDatabase database = mongoClient.getDatabase("Wines");
        MongoCollection<org.bson.Document> collection = database.getCollection("wines");

        MongoCollection<org.bson.Document> usersCollection = database.getCollection("wines");
        Crud_mongo crud=new Crud_mongo();
        Document doc = null;
        try {
            //doc = Jsoup.connect("https://www.winemag.com/ratings/#").get();
            //connection to the website
            doc = Jsoup.connect("https://www.winemag.com/ratings/#").get();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new ServerWinmagOufOfServiceException("WineMag server is down, the execution will resume taking in consideration" +
                        " the 96 k reviews already in review collection.");
            } catch (ServerWinmagOufOfServiceException ex) {
                ex.printStackTrace();
            }
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
            String email=CheckEmpty(tasterDoc.getElementsByClass("email").first().text());
            map.put("taster_email",email);




            map.put("title", reviewDoc.getElementsByClass("header__title").first().getElementsByTag("h1").first().text());
            map.put("description", reviewDoc.getElementsByClass("description").first().ownText());
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

            try {
                crud.createWine(map.get("title"),map.get("Variety"),map.get("Country"),map.get("Province"),map.get("Designation"),parseIntOrNull(map.get("Price")),map.get("taster_name"),parseIntOrNull(map.get("rating")),map.get("description"),map.get("Winery"),map.get("taster_twitter"),"None",map.get("taster_email"));
            } catch (ReviewAlreadyInserted e) {
                e.printStackTrace();
            }




            // add the new taster to the graph database
            //Crud_graph crud_graph = new Crud_graph("bolt://localhost:7687","neo4j","0000");

                //crud.addUser(map.get("taster_name"));
                //crud.addPostComplete(map.get("taster_name"),map.get("title"),map.get("description"),map.get("Winery"),map.get("Country"));

        }

    }

    /**
     * Check if something is empty or not.
     * @param input: String to check
     * @return input
     */
    private String CheckEmpty(String input) {
        if(input.isEmpty()) {
            return null;
        }
        return input;
    }

    /**
     * Transform a string value in an integer value.
     * @param value: string value.
     * @return integer value or null.
     */
    public Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -100;
        }
    }

}
