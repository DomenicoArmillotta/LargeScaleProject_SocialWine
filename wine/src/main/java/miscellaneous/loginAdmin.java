package miscellaneous;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class loginAdmin {

    public boolean logIn() {
        Scanner input1 = new Scanner(System.in);
        System.out.println("Enter Username : ");
        String username = input1.next();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();

        if (username.equals(getNameAdmin()) && password.equals(getPwdAdmin())) {
            System.out.println("Access Granted! Welcome!");
            return true;
        } else if (username.equals(getNameAdmin())) {
            System.out.println("Invalid Password!");
            return false;
        } else if (password.equals(getPwdAdmin())) {
            System.out.println("Invalid Username!");
            return false;
        } else {
            System.out.println("Invalid Username & Password!");
            return false;
        }
    }

    protected void addAdmin() {
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

    private String getNameAdmin() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Name", "admin");
        FindIterable<Document> resultName = collection.find(filter);
        String name = "";
        for (Document document : resultName) {
            name = name + document.toJson() + "\n";
        }
        return name;
    }

    private String getPwdAdmin() {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Password", "root");
        FindIterable<Document> resultPwd = collection.find(filter);
        String pwd = "";
        for (Document document : resultPwd) {
            pwd = pwd + document.toJson() + "\n";
        }
        return pwd;
    }
}

