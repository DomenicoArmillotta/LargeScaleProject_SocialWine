package databases;
import beans.Review;
import beans.User;
import beans.Wine;
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
                                "WHERE NOT EXISTS ((n)-[:Follow]->(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.username AS username , u.country as country , u.twitter_taster_handle as twitter_taster_handle\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("username", username));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }

    public ArrayList<User> showSuggestedUserByLike (final String username) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork< ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (n:User{username: $username})-[:Like]->(:Post)<-[:Created]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]->(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.username AS username , u.country as country , u.twitter_taster_handle as twitter_taster_handle\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("username", username));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }






    public ArrayList<Wine> showSuggestedWineByLike (final String username) {
        ArrayList<Wine> suggestedWines;
        try (Session session = driver.session()) {
            suggestedWines = session.readTransaction((TransactionWork< ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (n:User{username: $username})-[:Like]->(:Post)-[:Related]->(u:Wine)\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.wineName AS wineName , u.price as price , u.designation as designation , u.winery as winery , u.variety as variety , u.province as province\n" +
                                "ORDER BY number\n" +
                                "LIMIT 3",
                        parameters("username", username));
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    //User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);
                    Wine u = new Wine(r.get("wineName").asString(),r.get("designation").asString(),Integer.parseInt(r.get("price").asString()),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString(),null);
                    wines.add(u);
                }
                return wines;
            });
        } catch (Exception e){
            suggestedWines = null;
        }
        return suggestedWines;
    }

    public ArrayList<Wine> showSuggestedWineByComment (final String username) {
        ArrayList<Wine> suggestedWines;
        try (Session session = driver.session()) {
            suggestedWines = session.readTransaction((TransactionWork< ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (n:User{username: $username})-[:Created]->(:Post)-[:Related]->(u:Wine)\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.wineName AS wineName , u.price as price , u.designation as designation , u.winery as winery , u.variety as variety , u.province as province\n" +
                                "ORDER BY number\n" +
                                "LIMIT 3",
                        parameters("username", username));
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    //User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);

                    Wine u = new Wine(r.get("wineName").asString(),r.get("designation").asString(),Integer.parseInt(r.get("price").asString()),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString(),null);
                    wines.add(u);
                }
                return wines;
            });
        } catch (Exception e){
            suggestedWines = null;
        }
        return suggestedWines;
    }




    public ArrayList<User> showSuggestedUserByLikeAndFriends (final String username) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork< ArrayList<User>>) tx -> {
                Result result = tx.run("CALL{ \n"+
                                "MATCH (n:User{username: $username})-[:Like]->(:Post)<-[:Created]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]->(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u ORDER BY number \n" +
                                "UNION \n"+
                                "MATCH (n:User{username: $username})-[:Follow]->(:User)<-[:Follow]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]->(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u ORDER BY number \n" +
                                "}\n"+
                                "RETURN u.username AS username , u.country as country , u.twitter_taster_handle as twitter_taster_handle LIMIT 10 ",
                        parameters("username", username));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }



    public ArrayList<Wine> showSuggestedWineByCommentAndLike (final String username) {
        ArrayList<Wine> suggestedWines;
        try (Session session = driver.session()) {
            suggestedWines = session.readTransaction((TransactionWork< ArrayList<Wine>>) tx -> {
                Result result = tx.run("CALL{\n"+
                              "MATCH (n:User{username: $username})-[:Created]->(:Post)-[:Related]->(u:Wine)\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u ORDER BY number\n" +
                                "UNION\n" +
                                "MATCH (n:User{username: $username})-[:Like]->(:Post)-[:Related]->(u:Wine)\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u ORDER BY number\n" +
                                "}\n" +
                                "RETURN u.wineName AS wineName , u.price as price , u.designation as designation , u.winery as winery , u.variety as variety , u.province as province LIMIT 6 \n",
                        parameters("username", username));
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    //User u = new User(r.get("username").asString() , "",r.get("twitter_taster_handle").asString() ,r.get("country").asString()  ,"", false);

                    Wine u = new Wine(r.get("wineName").asString(),r.get("designation").asString(),Integer.parseInt(r.get("price").asString()),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString(),null);
                    wines.add(u);
                }
                return wines;
            });
        } catch (Exception e){
            suggestedWines = null;
        }
        return suggestedWines;
    }
}
