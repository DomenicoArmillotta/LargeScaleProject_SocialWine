package login;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class LoginAdmin {

    public Boolean logIn() {
        Scanner input1 = new Scanner(System.in);
        System.out.println("Enter Username : ");
        String username = input1.next();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();

        try {
            if (username.equals(getNameAdmin(username)) &&
                    password.equals(getPwdAdmin(password))) {
                System.out.println("Access Granted! Welcome Admin!");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Admin credentials are incorrect!");
        }
        return false;
    }

    public void addAdmin() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");

        Document document = new Document();
        document.append("Name", "admin");
        document.append("Password", "root");

        try {
            collection.insertOne(document);
            System.out.println("Successfully inserted documents. \n");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
        mongoClient.close();
    }

    public Object getNameAdmin(String username) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Name", "admin");
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

    public Object getPwdAdmin(String password) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Password", "root");
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
}

