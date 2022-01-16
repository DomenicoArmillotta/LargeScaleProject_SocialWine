package databases;
import beans.Review;
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
    public ArrayList<Review> showTrendingComment(){
        ArrayList<Review>  likePost;
        try (Session session = driver.session()) {
            likePost = session.readTransaction((TransactionWork<ArrayList<Review> >) tx -> {
                Result result = tx.run("MATCH (p:Post)-[r:Like]-(u:User)\n" +
                        "RETURN p.description AS description, p.rating AS rating , COUNT(r) AS numLike \n" +
                        "ORDER BY numLike DESC\n" +
                        "LIMIT 5");
                ArrayList<Review>  likeResult = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int conversionRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString() ,conversionRating );
                    likeResult.add(review);
                }
                System.out.println(likeResult);
                return likeResult;
            });
        }catch (Exception e){
            likePost = null;
        }
        return likePost;
    }


    public ArrayList<User> showSuggestedUserByFriends (final String username) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork< ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (n:User{username: $username})-[:Follow]->(:User)<-[:Follow]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]-(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.username AS username , u.country as country , u.twitter_taster_handle as twitter_taster_handle\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("username", username));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", r.get("admin").asBoolean());
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }

}
