package databases;
import beans.Review;
import beans.User;
import org.neo4j.driver.*;

import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class advanced_graph implements AutoCloseable {
    private final Driver driver;


    public advanced_graph( String uri, String user, String password ) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    //suggest 5 user that are friend of friend that are not yet followed
    // user1 <----> user2 ---> user3
    // user3 is suggested to user1
    //WORK
    public ArrayList<User> suggestedUserByFriends(final String taster_name) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH p=(n:User{taster_name: $taster_name})-[:Follow]->(:User)<-[:Follow]-(u:User)\n" +
                                "WHERE NOT EXISTS ((n)-[:Follow]-(u))\n" +
                                "WITH u, rand() AS number\n" +
                                "RETURN u.taster_name AS taster_name\n" +
                                "ORDER BY number\n" +
                                "LIMIT 5",
                        parameters("taster_name", taster_name));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("taster_name").asString());
                    users.add(u);
                }
                Iterator<User> us = users.iterator();
                while (us.hasNext()){
                    System.out.println(us.next().getTaster_name());
                }

                return users;
            });
        } catch (Exception e){
            suggestedUsers = null;
        }
        return suggestedUsers;
    }

    //find the most 5 post with most like on the social
    //i order in descend order and the pick the top 5 from number of like
    //WORK
    public HashMap<String,String> FiveMostLikePost(){
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
                    likeResult.put(r.get("titlePost").asString(), r.get("numLike").toString());
                }
                System.out.println(Arrays.asList(likeResult));
                return likeResult;
            });
        }catch (Exception e){
            likePost = null;
        }
        return likePost;
    }

    // query to extract 10 people for the follow
    //   MATCH (p:Person) RETURN p.name LIMIT 10
    public void randomFollowByUser (final String selected_taster_name){
        try ( Session session = driver.session() )
        {
            // take the 10 random user
            List<String> random = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (p:User) RETURN p.taster_name as taster_name LIMIT 10");

                ArrayList<String> randomUsers = new ArrayList<>();
                while(result.hasNext())
                {
                    Record r = result.next();
                    //i pick the random user
                    randomUsers.add(r.get("taster_name").asString());
                    //i save the String name in the variable
                    String taster_name;
                    taster_name = r.get("taster_name").asString();
                    //i open another session to create the relation "Follow" in bidirectional way
                    try (Session session2 = driver.session()){
                        session2.writeTransaction( tx2 -> {
                            tx2.run("MATCH (u:User{taster_name: $taster_name1}),(u1:User{taster_name: $taster_name2})\n" +
                                            "CREATE (u)<-[:Follow]->(u1)",
                                    parameters("selected_taster_name", selected_taster_name , "taster_name", taster_name));
                            return 1;
                        });
                    } catch (Exception e){
                    }

                }
                return randomUsers;
            });
                System.out.println(random);
        }
    }

    //query to extract 10 post to put like
    //  MATCH (p:Post) RETURN p.titlePost LIMIT 10
    public void randomLikeByUser (final String selected_taster_name){
        try ( Session session = driver.session() )
        {
            // take the 10 random user
            List<String> random = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run( "MATCH (p:Post) RETURN p.titlePost as titlePost LIMIT 10");

                ArrayList<String> randomPost = new ArrayList<>();
                while(result.hasNext())
                {
                    Record r = result.next();
                    //i pick the random title Post
                    randomPost.add(r.get("titlePost").asString());
                    //i save the String titlePost in the variable
                    String titlePost;
                    titlePost = r.get("titlePost").asString();
                    //i open another session to create the relation "Like" in unidirectional way
                    try (Session session2 = driver.session()){
                        session2.writeTransaction( tx2 -> {
                            tx2.run("MATCH (u:User{taster_name: $taster_name1}),(p:Post{titlePost: $titlePost})\n" +
                                            "CREATE (u)-[:Like]->(p)",
                                    parameters("selected_taster_name", selected_taster_name , "titlePost", titlePost));
                            return 1;
                        });
                    } catch (Exception e){
                    }

                }
                return randomPost;
            });
            System.out.println(random);
        }
    }




        @Override
    public void close() throws Exception {
        driver.close();
    }
}
