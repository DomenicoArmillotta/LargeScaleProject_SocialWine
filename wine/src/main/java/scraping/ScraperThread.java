package scraping;

import com.mongodb.*;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import databases.Crud_graph;
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


        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<org.bson.Document> collection = database.getCollection("review");
        MongoCollection<org.bson.Document> usersCollection = database.getCollection("user_credentials");

        Document doc = null;
        try {
            //doc = Jsoup.connect("https://www.winemag.com/ratings/#").get();

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
            BasicDBObject query = new BasicDBObject();
            List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
            obj1.add(new BasicDBObject("title", map.get("title")));
            obj1.add(new BasicDBObject("taster_name", map.get("taster_name")));
            obj1.add(new BasicDBObject("variety",map.get("Variety") ));
            obj1.add(new BasicDBObject("winery", map.get("Winery")));
            query.put("$and", obj1);
            //System.out.println(query.toString());
            MongoCursor<org.bson.Document> cursor = collection.find(query).iterator();
            if( (cursor.hasNext()))
            {
                System.out.println("Not empty Cursor");
                org.bson.Document currentObject= cursor.next();


                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("points", map.get("rating"));
                newDocument.put("description", map.get("description"));
                newDocument.put("title", map.get("title"));

                BasicDBObject updateObject = new BasicDBObject();
                updateObject.put("$set", newDocument);

                collection.updateOne(currentObject, updateObject);


            }
            else
            {
                System.out.println("empty Cursor");
                org.bson.Document  person = new   org.bson.Document ("points", map.get("rating"))
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
                collection.insertOne(person);
            }
            // add users to mongoDb database
            query = new BasicDBObject("Name", map.get("taster_name"));
            System.out.println(query.toString());
             cursor = usersCollection.find(query).iterator();
            if( (cursor.hasNext()))
            {
                System.out.println("Not added to MongoDB");
            }
            else
            {
                org.bson.Document user = new org.bson.Document("Name", map.get("taster_name"))
                        .append("Password", "abcd");

                usersCollection.insertOne(user);
            }



            // add the new taster to the graph database
            Crud_graph crud = new Crud_graph("bolt://localhost:7687","neo4j","0000");

                crud.addUser(map.get("taster_name"));
                crud.addPostComplete(map.get("taster_name"),map.get("title"),map.get("description"),map.get("Winery"),map.get("Country"));

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
            return null;
        }
    }

}
