package login;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class LoginUser {
    SaveLogin level = new SaveLogin();

    public String logIn() {
        Scanner input1 = new Scanner(System.in);
        System.out.println("Enter Username : ");
        String username = input1.next();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();

        try {
            if (username.equals(getNameUser(username)) && password.equals(getPwdUser(password))) {
                System.out.println("Access Granted! Welcome!");
                level.putAsString(username, password);
                ArrayList<String> name = level.findKeysByPrefix(password);
                String str = Arrays.toString(name.toArray());
                return str;
            }
        } catch (Exception e) {
            System.out.println("User credentials are incorrect!");
        }
        return null;
    }


    public String getNameUser(String username) {
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

    public String getPwdUser(String password){
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

    public Boolean checkLogIn (){
        if (logIn() != null){
            return true;
        }
        return false;
    }

}