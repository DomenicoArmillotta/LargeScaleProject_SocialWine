package miscellaneous;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class LoginUser {
    public boolean logIn() {
        Scanner input1 = new Scanner(System.in);
        System.out.println("Enter Username : ");
        String username = input1.next();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();

        if (username.equals(getNameUser(username)) && password.equals(getPwdUser(password))) {
            System.out.println("Access Granted! Welcome!");
            return true;
        } else if (username.equals(getNameUser(username))) {
            System.out.println("Invalid Password!");
            return false;
        } else if (password.equals(getPwdUser(password))) {
            System.out.println("Invalid Username!");
            return false;
        } else {
            System.out.println("Invalid Username & Password!");
            return false;
        }
    }


    private String getNameUser(String username) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Name", ""+username+"");
        FindIterable<Document> resultName = collection.find(filter);
        String name = "";
        for (Document document : resultName) {
            name = name + document.toJson() + "\n";
        }
        return name;
    }

    private String getPwdUser(String password){
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("wine");
        MongoCollection<Document> collection = database.getCollection("user_credentials");
        Bson filter = eq("Password", ""+password+"");
        FindIterable<Document> resultPwd = collection.find(filter);
        String pwd = "";
        for (Document document : resultPwd) {
            pwd = pwd + document.toJson() + "\n";
        }
        return pwd;
    }
}