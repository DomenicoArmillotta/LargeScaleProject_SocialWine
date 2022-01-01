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
import org.iq80.leveldb.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static java.lang.System.exit;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

/**
 *This class provides a login system for the users. In deep, check if the credentials
 * that have been inserted from the user are correct or not, comparing  the users credentials
 * stored in user_credentials stored on MongoDB
 */
public class LoginUser {

    /**
     * This method it's called by the menu and user could insert his credentials.
     * If the credentials are correct the user can access to the social otherwise
     * will be rejected. If the credentials are correct, the name will be stored
     * inside Level DB (Key Value Database).
     * @return str: the user's name taken from Level DB;
     * @return null: if the user's credentials are incorrect;
     */
    private String logIn() throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = openDB();
        BufferedReader input1 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Username : ");
        String username = input1.readLine();
        byte[] byteArrrayUsername = username.getBytes();

        if (username.equals("X")){
            System.out.println("Exiting program...");
            System.exit(0);
        }else{
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
                    String str = new String(name, StandardCharsets.UTF_8);
                    db.close();
                    return str;
                }
            } catch (Exception e) {
                System.out.println("User credentials are incorrect!");
                exit(0);
            }
            return null;
        }
        return username;
    }

    /**
     * It's to take only the user's name
     * @return name: the user's name derived from the login method
     * @throws IOException
     */
    public String getName() throws IOException {
        String name = logIn();
        return name;
    }

    /**
     * Executes the comparison between the name that the user has inserted and the username stored
     * in mongoDB collection that store all the users' name and passwords.
     * @param username: the user's name coming from the input scanner
     * @return name:
     */
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


    /**
     * Executes the comparison between the name that the user has inserted and the username stored
     * in mongoDB collection that store all the users' name and passwords.
     * @param password: the user's password coming from the input scanner
     * @return
     */
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

    /**
     * Open connection with LevelDB
     * @return db: the folder userlog that stores the temporarly name and password of the user that want to access
     */
    private DB openDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try{
            factory.destroy(new File("userlog"), options);
            DB db = factory.open(new File("userlog"), options);
            return db;
        }
        catch (IOException ioe) {  }
        return null;
    }
}