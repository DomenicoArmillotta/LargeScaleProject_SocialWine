package databases;

import beans.Review;
import beans.User;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.HashMap;

import static org.neo4j.driver.Values.parameters;

public class advanced_graph implements AutoCloseable {
    private final Driver driver;


    public advanced_graph( String uri, String user, String password ) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }


    public ArrayList<User> suggestedUserByFriends(final String taster_name) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH p=(n:User{taster_name: $taster_name})-[:Follow]->(:User)<-[:Follow]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]-(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN toString(u.id) AS Id, u.taster_name AS taster_name\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("taster_name", taster_name));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("taster_name").asString());
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }

    public HashMap<String,String> FiveMostLikePost(){
        HashMap<String,String>  likePost;
        try (Session session = driver.session()) {
            likePost = session.readTransaction((TransactionWork<HashMap<String,String> >) tx -> {
                Result result = tx.run("MATCH (p:Review)-[r:Like]-(u:User)\n" +
                        "RETURN p.title AS Title, COUNT(r) AS numLike \n" +
                        "ORDER BY numLike DESC\n" +
                        "LIMIT 5");
                HashMap<String,String>  likeResult = new HashMap<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    likeResult.put(r.get("Title").asString(), r.get("numLike").toString());
                }
                return likeResult;
            });
        }catch (Exception e){
            likePost = null;
        }
        return likePost;
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
