package databases;
import beans.User;
import org.neo4j.driver.*;

import java.util.*;

import static org.neo4j.driver.Values.parameters;

/**
 * This class contains Neo4J advanced queries.
 */
public class Advanced_graph implements AutoCloseable {
    private final Driver driver;


    /**
     * Constructor that allows to start the connection with Neo4J.
     *
     * @param uri:      address of Neo4J where the DB is on;
     * @param user:     user's name;
     * @param password: DB's password;
     */
    public Advanced_graph(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
    /**
     * Find the top five post on the social according to their number of like,
     * in descending order.
     * @return likePost: list of post with their likes.
     */
    public HashMap<String,String> showFiveMostLikePost(){
        HashMap<String,String>  likePost;
        try (Session session = driver.session()) {
            likePost = session.readTransaction((TransactionWork<HashMap<String,String> >) tx -> {
                Result result = tx.run("MATCH (p:Post)-[r:Like]-(u:User)\n" +
                        "RETURN p.titlePost AS titlePost, COUNT(r) AS numLike \n" +
                        "ORDER BY numLike DESC\n" +
                        "LIMIT 5");
                HashMap<String,String>  likeResult = new HashMap<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.print("title: ");
                    System.out.print(r.get("titlePost").asString());
                    System.out.print("num of like: ");
                    System.out.print( r.get("numLike").toString());
                }
                System.out.println(likeResult);
                return likeResult;
            });
        }catch (Exception e){
            likePost = null;
        }
        return likePost;
    }

    /**
     * Suggest five users to given user, that are friend of friend that are not yet followed
     * Dom--> Matt <--Giov ==> to Dom the friendship Giov is suggested
     * @param taster_name: user's name.
     * @return
     */
    public HashSet<User> showSuggestedUserByFriends (final String taster_name) {
        HashSet<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork<HashSet<User>>) tx -> {
                Result result = tx.run("MATCH (n:User{taster_name: $taster_name})-[:Follow]->(:User)<-[:Follow]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]-(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.taster_name AS taster_name , u.country as country\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("taster_name", taster_name));
                HashSet<User> users = new HashSet<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("taster_name").asString());
                    users.add(u);
                    System.out.print("name: ");
                    System.out.print(r.get("taster_name" ).asString());
                    System.out.print("country: ");
                    System.out.println(r.get("country" ).asString());
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }

}
