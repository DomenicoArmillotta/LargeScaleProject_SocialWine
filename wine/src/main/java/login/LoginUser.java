package login;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static java.lang.System.exit;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LoginUser {

    public String logIn() throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("user_log"), options);
        BufferedReader input1 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Username : ");
        String username = input1.readLine();
        byte[] byteArrrayUsername = username.getBytes();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();
        byte[] byteArrrayPassword = password.getBytes();


        try {
            if (username.equals(getNameUser(username)) &&
                    password.equals(getPwdUser(password))) {
                System.out.println("Access Granted! Welcome!");
                db.put(byteArrrayPassword, byteArrrayUsername);
                byte[] name = db.get(byteArrrayPassword);
                String st = Base64.getEncoder().encodeToString(name);
                return st;
            }
        } catch (Exception e) {
            System.out.println("User credentials are incorrect!");
            exit(0);
        }
        return null;
    }


    public Object getNameUser(String username) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Name", "" + username + "");
        MongoCursor<Document> resultName = collection.find(filter).iterator();
        BasicDBList list = new BasicDBList();
        Object name = null;
        if (!resultName.next().isEmpty()) {
            Document doc = resultName.next();
            list.add(doc);
            name = doc.get("Name");
        }
        return name;
    }



    public Object getPwdUser(String password){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Password", ""+password+"");
        MongoCursor<Document> resultPwd = collection.find(filter).iterator();
        BasicDBList list = new BasicDBList();
        Object pwd = null;
        if (!resultPwd.next().isEmpty()) {
            Document doc = resultPwd.next();
            list.add(doc);
            pwd = doc.get("Password");
        }
        return pwd;
    }

    public Boolean checkLogIn () throws IOException {
        if (logIn() != null){
            return true;
        }
        return false;
    }
}