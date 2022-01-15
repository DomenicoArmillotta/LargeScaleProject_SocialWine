package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import org.neo4j.driver.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.neo4j.driver.Values.parameters;


public class Crud_graph implements AutoCloseable {

    //operation must be closed at the end of each operation?
    //quando visualizzo lo faccio interamente da neo4j



    //IDEA=
    //graphdb = abbiamo user , post (? = si per mettere i like) , vini per creare le relazioni (? --> potrebbero non servire dato che non abbiamo queri che usano i vini in graph, ma le usano in mongo dato che abbiamo un nested li)
    //quindi potrebbe essere corretto avere in graph solo user + post

    private final Driver driver;


    public Crud_graph(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    @Override
    public void close() throws Exception {
        driver.close();
    }

    //all operation

    //registered user
    public void registerUser(final String username, final String password , final String adminFlag , final String twitter_taster_handle , final String country , final String email ) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:User {username: $username , password: $password , adminFlag: $adminFlag , twitter_taster_handle: $twitter_taster_handle , country: $country , email: $email   })",
                        parameters("username", username , "password" , password , "adminFlag" , adminFlag , "twitter_taster_handle" , twitter_taster_handle , "country" , country , "email" , email ));
                System.out.println("User added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    //ban user by username, this function can be done by admin only
    public void banUserByUsername(final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})\n" +
                                "DELETE u",
                        parameters("username", username));
                System.out.println("User drop successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }
    //work
    public User showUserByUsername(final String username){
        User user;
        try (Session session = driver.session()) {
            user = session.readTransaction((TransactionWork<User>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})\n" +
                                "RETURN u.username AS username , u.country AS country , u.email AS email , u.twitter_taster_handle as twitter_taster_handle ",
                        parameters("username", username));
                User userToShow = null;
                while (result.hasNext()) {
                    Record r = result.next();
                    userToShow = new User(r.get("username").asString(),"",r.get("country").asString() , r.get("email").asString() , r.get("twitter_taster_handle").asString());
                }
                return userToShow;
            });
        }catch (Exception e){
            user = null;
        }
        return user;
    }

    //work
    public ArrayList<User> showAllUser() {
        ArrayList<User> usertoshow;
        try (Session session = driver.session()) {
            usertoshow = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u:User)\n" +
                                "RETURN u.username AS username , u.country AS country");
                ArrayList<User> users = new ArrayList<User>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString() , "","" ,"" ,"");
                    users.add(u);
                }
                return users;
            });
        }catch (Exception e){
            usertoshow = null;
        }
        return usertoshow;
    }

    //show a list of followed user ---->  work
    public ArrayList<User> showFollowedUsers(final String username) {
        ArrayList<User> arrayUser = null;
        try (Session session = driver.session()) {
            arrayUser= session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u1:User{username: $username}) , (u2:User)\n" +
                                "WHERE  EXISTS ((u1)-[:Follow]->(u2))\n" +
                                "RETURN u2.username AS username , u2.country AS country , u2.twitter_taster_handle AS twitter_taster_handle  , u2.email AS email",
                                parameters("username", username));
                ArrayList<User> usersOutput = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString(),null,r.get("twitter_taster_handle").asString(),r.get("country").asString() ,r.get("email").asString() );
                    usersOutput.add(u);
                }
                return usersOutput;
            });
        } catch (Exception e){
            
        }
        return arrayUser;
    }


    public void searchUserByPrefix(final String prefixUsername) {
        HashSet<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork<HashSet<User>>) tx -> {
                Result result = tx.run("MATCH (u:User)\n" +
                                "WHERE  u.username STARTS WITH '$prefixUsername'\n" +
                                "RETURN u.username AS username , u.country AS country \n",
                        parameters("prefixUsername", prefixUsername ));
                HashSet<User> users = new HashSet<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.println("Nome ");
                    System.out.println(r.get("username").asString());
                    System.out.println("Country : ");
                    System.out.println(r.get("country").asString());
                }
                return null;
            });

        }

    }

    public void addWine(final String wineName, final String designation , final String price, final String province , final String variety , final String winery ){
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Wine {wineName: $wineName , designation: $designation , price: $price , province: $province ,  variety: $variety , winery: $winery  })",
                        parameters("wineName",wineName,"designation",designation,"price",price , "province" ,province,"variety",variety , "winery",winery));
                System.out.println("Wine added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    public void deleteWineByName(final String wineName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:Wine{wineName : $wineName})\n" +
                                "DELETE u",
                        parameters("wineName", wineName));
                System.out.println("Wine drop successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    public ArrayList<Wine> showWineByName(final String wineName ){
        ArrayList<Wine> winetoshow;
        try (Session session = driver.session()) {
            winetoshow= session.readTransaction((TransactionWork<ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (w:Wine{wineName: $wineName})\n" +
                        "RETURN w.wineName AS wineName ,w.designation AS designation , w.price AS price , w.province AS province , w.variety as variety , w.winery as winery");
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    Wine wine = new Wine(r.get("wineName").asString(),r.get("designation").asString(),r.get("price").asString(),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString());
                    wines.add(wine);
                }
                return wines;
            });
        }catch (Exception e){
            winetoshow = null;
        }
        return winetoshow;
    }


    public ArrayList<Wine> showAllWine (){
        ArrayList<Wine> winetoshow;
        try (Session session = driver.session()) {
            winetoshow= session.readTransaction((TransactionWork<ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (w:Wine)\n" +
                        "RETURN w.wineName AS wineName ,w.designation AS designation , w.price AS price , w.province AS province , w.variety as variety , w.winery as winery");
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    Wine wine = new Wine(r.get("wineName").asString(),r.get("designation").asString(),r.get("price").asString(),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString());
                    wines.add(wine);
                }
                return wines;
            });
        }catch (Exception e){
            winetoshow = null;
        }
        return winetoshow;
    }



    public void addComment(final String description , final String rating) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Post {description: $description , rating: $rating })",
                        parameters( "description", description , "rating" , rating));
                System.out.println("comment added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    public ArrayList<User> findUserByDescription(final String description){
        ArrayList<User> arrayUser = null;
        try (Session session = driver.session()) {
            arrayUser= session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u1:User) , (p:Post{description: $description})\n" +
                                "WHERE  EXISTS ((u1)-[:Created]->(p))\n" +
                                "RETURN u1.username AS username , u1.country AS country , u1.twitter_taster_handle AS twitter_taster_handle  , u1.email AS email",
                        parameters("description", description));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User user = new User(r.get("username").asString(),null,r.get("twitter_taster_handle").asString(),r.get("country").asString() ,r.get("email").asString() );
                    users.add(user);
                }
                return users;
            });
        } catch (Exception e){

        }
        return arrayUser;
    }




    //delete only own review by title and username
    public void deleteCommentByDescription(final String description ) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post{description: $description}) \n" +
                                "DELETE p",
                        parameters("description", description ));
                System.out.println("Review drop successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }


    //broswe all review of social network --> work
    public ArrayList<Review> showAllCommentRelatedWineName(final String wineName) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow=session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (p:Post) , (w:Wine{wineName: $wineName}) \n" +
                                "WHERE  EXISTS ((p)-[:Related]->(w))\n" +
                                "RETURN p.description as description , p.rating as rating",
                                 parameters("wineName",wineName));
                ArrayList<Review> commentOutput = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(),convertedRating);
                    commentOutput.add(review);

                }
                return commentOutput;
            });
        } catch (Exception e){
            commenttoshow = null;
        }
        return commenttoshow;
    }

    public ArrayList<Review> showCommentsFriends (final String myUsername , final String usernameFriend ) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $myUsername}),(u1:User{username: $usernameFriend}) , (p:Post),(w:Wine)  \n" +
                                "WHERE  EXISTS ((u)-[:Follow]-(u1))\n" +
                                "AND  EXISTS ((u1)-[:Created]->(p))\n" +
                                "AND  EXISTS ((p)-[:Related]-(w))\n" +
                                "RETURN  p.description AS description , p.rating AS rating , w.wineName AS wineName \n",
                        parameters("myUsername", myUsername ,"usernameFriend" , usernameFriend ));
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(),convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e){
            commenttoshow = null;
        }
        return commenttoshow;
}

    public ArrayList<Review> showMyComment (final String myUsername ) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $myUsername}), (p:Post),(w:Wine)  \n" +
                                "WHERE  EXISTS ((u)-[:Created]->(p))\n" +
                                "RETURN  p.description AS description , p.rating AS rating\n",
                        parameters("myUsername", myUsername  ));
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(),convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e){
            commenttoshow = null;
        }
        return commenttoshow;
    }

    public ArrayList<Review> showAllComments () {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (p:Post),(w:Wine)  \n" +
                                "WHERE  EXISTS ((p)-[:Related]->(w))\n" +
                                "RETURN  p.description AS description , p.rating AS rating\n");
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(),convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e){
            commenttoshow = null;
        }
        return commenttoshow;
    }






    //create relation follow for the use, and check if not exist
    public void createRelationFollow(final String username1, final String username2) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username1}),(u1:User{username: $username2})\n" +
                                "WHERE NOT EXISTS ((u)-[:Follow]->(u1))\n" +
                                "CREATE (u)-[:Follow]->(u1)",
                        parameters("username1", username1, "username2", username2));
                System.out.println("Follow added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    //delete relation followed
    public void deleteRelationFollow(final String username1, final String username2) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username1})-[f:Follow]-(u1:User{username : $username2})\n" +
                                "DELETE f",
                        parameters("username1", username1, "username2", username2));
                System.out.println("Follow drop successfully (Neo4J)." + "\n");
                return null;
            });

        }
    }


    //relation between user that create a new review and the review
    public boolean createRelationCreated(final String description, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username}),(u1:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((u)-[:Created]->(u1))\n" +
                                "CREATE (u)-[:Created]->(u1)",
                        parameters("description", description, "username", username));
                return 1;

            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    public void deleteRelationCreated(final String description, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})-[f:Created]-(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description, "username", username));
                return null;
            });
        }

    }

    //return true if access granted, false if is not allow to enter , check also if you are admin or user
    public boolean checkLoginByUsername (final String username , final String passwordLogin , final String adminFlag) {
        AtomicBoolean check = new AtomicBoolean(false);
        String password = null;
        AtomicReference<String> prova = null;
        try (Session session = driver.session()) {

            password = session.readTransaction((TransactionWork<String>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})\n" +
                                "RETURN u.password as password , u.adminFlag as adminFlag",
                        parameters("username", username));
                while (result.hasNext()) {
                    Record r = result.next();
                    if(( r.get("password").asString()).equals(passwordLogin) ){
                        if((r.get("adminFlag").asString()).equals(adminFlag))
                        {
                            check.set(true);
                        }
                    }
                }
                return String.valueOf(check);
            });

        }

        return check.get();
    }

    public void putLikeByDescription(final String description, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username}),(p:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((u)-[:Like]->(p))\n" +
                                "CREATE (u)-[:Like]->(p)",
                        parameters("description", description, "username", username));
                System.out.println("Like reaction successfully inserted (Neo4J)." + "\n");
                return null;
            });
        }
    }


    public void deleteLikeByDescription(final String description, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})-[f:Like]-(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description, "username", username));
                System.out.println("Like drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }

    //relation with wine and comment
    public void createRelationRelated(final String wineName, final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (w:Wine{wineName: $wineName}),(p:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((p)-[:Related]->(w))\n" +
                                "CREATE (p)-[:Related]->(w)",
                        parameters("wineName", wineName, "description", description));
                System.out.println("Related added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }


    public void deleteRelationRelated(final String description, final String wineName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(p:Post{description : $description})-[f:Related]-(w:Wine{wineName : $wineName})\n" +
                                "DELETE f",
                        parameters("description", description, "wineName", wineName));
                System.out.println("Relation Related drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }

    public void deleteAllRelationLikeByDescription(final String description){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User)-[f:Like]->(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description));
                System.out.println("Relation Related drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }

    }

    public void deleteAllRelationRelatedByDescription(final String description){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post{description : $description})-[f:Related]->(w:Wine)\n" +
                                "DELETE f",
                        parameters("description", description));
                System.out.println("Relation Related drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }

    }
    public void deleteAllRelationCreatedByDescription(final String description){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User)-[f:Created]->(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description));
                System.out.println("Relation Related drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }

    }

    public void deleteAllRelationFollow(final String username){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})<-[f:Follow]->(u:User)\n" +
                                "DELETE f",
                        parameters("username", username));
                System.out.println("Relation follow -> drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }
    public void deleteAllRelationFollowed(final String username){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})<-[f:Follow]-(u:User)\n" +
                                "DELETE f",
                        parameters("username", username));
                System.out.println("Relation follow <- drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }
    public void deleteAllRelationLike(final String username){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})-[f:Like]->(p:Post)\n" +
                                "DELETE f",
                        parameters("username", username));
                System.out.println("Relation like  drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }

    }
    public void deleteAllRelationCreated(final String username){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})-[f:Created]->(p:Post)\n" +
                                "DELETE f",
                        parameters("username", username));
                System.out.println("Relation created  drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }
    public void deleteUserByUsername(final String username){
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})\n" +
                                "DELETE u",
                        parameters("username", username));
                System.out.println("Node user  drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }
    }

    public  void deleteAllRelatedBynameWine (final String wineName){
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post)-[f:Related]->(w:Wine{wineName: $wineName})\n" +
                                "DELETE f",
                        parameters("wineName", wineName));
                System.out.println("Relation related  drop successfully made it (Neo4J)." + "\n");
                return null;
            });
        }

    }



    public Number countLikeByDescription(final String description){
        Number nLike = null;

        try (Session session = driver.session()) {
            nLike = session.readTransaction((TransactionWork<Number>) tx -> {
                Result result = tx.run("MATCH (u:User)-[r:Like]->(p:Post{description: $description})\n" +
                                "RETURN  COUNT(r) AS numLike",
                        parameters("description",description));
                Number likeNumber = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    likeNumber = r.get("numLike").asNumber();
                }
                return likeNumber;
            });
        }catch (Exception e){
            nLike = null;
        }
        return nLike;
    }

    public Number countFollowersByUsername(final String username){
        Number nFollowers = null;

        try (Session session = driver.session()) {
            nFollowers = session.readTransaction((TransactionWork<Number>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})<-[r:Follow]-(u2:User)\n" +
                                "RETURN  COUNT(r) AS nfollowers",
                        parameters("username",username));
                Number followersNumber = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    followersNumber = r.get("nfollowers").asNumber();
                }
                return followersNumber;
            });
        }catch (Exception e){
            nFollowers = null;
        }
        return nFollowers;
    }


    public int checkIfLikedByDescription(final String description , final String username){
        Integer resultLike = 0;
        try (Session session = driver.session()) {
            resultLike= session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})-[Like]->(p:Post{description: $description})\n" +
                                "RETURN u.username as username",
                        parameters("description",description,"username",username));
                Integer ifLike = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    if((r.get("username").asString()).equals(username)){
                        ifLike=1;
                    }

                }
                return ifLike;
            });
        }catch (Exception e){
            resultLike = null;
        }
        return resultLike;
    }






    public ArrayList<Wine> findWineByDescription(final String description){
        ArrayList<Wine> winetoshow;
        try (Session session = driver.session()) {
            winetoshow= session.readTransaction((TransactionWork<ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (p:Post{description: $description})-[r:Related]->(w:Wine)\n" +
                        "RETURN w.wineName AS wineName ,w.designation AS designation , w.price AS price , w.province AS province , w.variety as variety , w.winery as winery",
                        parameters("description",description));
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    Wine wine = new Wine(r.get("wineName").asString(),r.get("designation").asString(),r.get("price").asString(),r.get("province").asString(),r.get("variety").asString(),r.get("winery").asString());
                    wines.add(wine);
                    String name = r.get("wineName").asString();
                }
                return wines;
            });
        }catch (Exception e){
            winetoshow = null;
        }
        return winetoshow;
    }












}



    /**
     * Create a new user.
     *
     * @param taster_name: user'name.
     *//*
    public void addUser(final String taster_name) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:User {taster_name: $taster_name})",
                        parameters("taster_name", taster_name));
                System.out.println("User added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    *//**
     * Add a post on graph adding the relations "created by" (user-post) and "belong" (post-winery).
     *
     * @param taster_name: user's name;
     * @param titlePost:   review'title;
     * @param description: review's body;
     * @param wineryName:  winery's name;
     * @param country:     winery's country.
     *//*
    public void addPostComplete(final String taster_name, final String titlePost, final String description, final String wineryName, final String country) {
        addPost(titlePost, description);
        createRelationBelong(titlePost, wineryName);
        createRelationCreated(titlePost, taster_name);
        addPageWinery(wineryName, country);
        createRelationBelong(titlePost, wineryName);
        System.out.println("Complete post added successfully (Neo4J)." + "\n");

    }

    *//**
     * Add a post.
     *
     * @param titlePost:   review's title;
     * @param description: review's body.
     *//*
    public void addPost(final String titlePost, final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Post {titlePost: $titlePost, description: $description})",
                        parameters("titlePost", titlePost, "description", description));
                System.out.println("Post added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    *//**
     * Add a winery.
     *
     * @param wineryName: winery's name;
     * @param country:    winery's country.
     *//*
    public void addPageWinery(final String wineryName, final String country) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Page {wineryName: $wineryName, country: $country})",
                        parameters("wineryName", wineryName, "country", country));
                System.out.println("Page winery added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    *//**
     * Create the relation "follow" between two user of the social, if that relation doesn't exist.
     *
     * @param taster_name1: user's name that want to follow someone;
     * @param taster_name2: user's name that will be followed from taster_name1.
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean createRelationFollow(final String taster_name1, final String taster_name2) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name1}),(u1:User{taster_name: $taster_name2})\n" +
                                "WHERE NOT EXISTS ((u)-[:Follow]->(u1))\n" +
                                "CREATE (u)-[:Follow]->(u1)",
                        parameters("taster_name1", taster_name1, "taster_name2", taster_name2));
                System.out.println("Follow added successfully (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Drop the relation "follow" between two user.
     *
     * @param taster_name1: user's name that want to drop relation "follow" w.r.t another user;
     * @param taster_name2: user's name that will not have anymore taster_name1's follow.
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deleteRelationFollow(final String taster_name1, final String taster_name2) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name1})-[f:Follow]-(u1:User{taster_name : $taster_name2})\n" +
                                "DELETE f",
                        parameters("taster_name1", taster_name1, "taster_name2", taster_name2));
                System.out.println("Follow drop successfully (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    *//**
     * Create the relation like between a user and a post taking in consideration title post.
     *
     * @param titlePost:   review's title;
     * @param taster_name: user's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean createRelationLikeByTitle(final String titlePost, final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name}),(u1:Post{titlePost: $titlePost})\n" +
                                "WHERE NOT EXISTS ((u)-[:Like]->(u1))\n" +
                                "CREATE (u)-[:Like]->(u1)",
                        parameters("titlePost", titlePost, "taster_name", taster_name));
                System.out.println("Like reaction successfully inserted (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Create the relation like between a user and a post taking in consideration description post.
     *
     * @param descriptionPost: body of the post
     * @param taster_name: user's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean createRelationLikeByDescription(final String descriptionPost, final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name}),(u1:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((u)-[:Like]->(u1))\n" +
                                "CREATE (u)-[:Like]->(u1)",
                        parameters("description", descriptionPost, "taster_name", taster_name));
                System.out.println("Like reaction successfully inserted (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    *//**
     * Delete the relation like between a review and a user.
     *
     * @param titlePost:   review's title;
     * @param taster_name: user's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deleteRelationLike(final String titlePost, final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Like]-(p:Post{titlePost : $titlePost})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost, "taster_name", taster_name));
                System.out.println("Like drop successfully made it (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Create the relation created between a review and a user. (Who create the review).
     *
     * @param titlePost:   review's title;
     * @param taster_name: user's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean createRelationCreated(final String titlePost, final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name}),(u1:Post{titlePost: $titlePost})\n" +
                                "WHERE NOT EXISTS ((u)-[:Created]->(u1))\n" +
                                "CREATE (u)-[:Created]->(u1)",
                        parameters("titlePost", titlePost, "taster_name", taster_name));
                return 1;

            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Delete the relation created between a review and a user.
     *
     * @param titlePost:   review's title;
     * @param taster_name: user's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deleteRelationCreated(final String titlePost, final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Created]-(p:Post{titlePost : $titlePost})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost, "taster_name", taster_name));
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Create the relation belong between a review and a winery, to indicate to which winery
     * that review refers to.
     *
     * @param titlePost:  review's title;
     * @param wineryName: winery's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean createRelationBelong(final String titlePost, final String wineryName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:Post{titlePost: $titlePost}),(u1:Page{wineryName: $wineryName})\n" +
                                "WHERE NOT EXISTS ((u)-[:Belong]->(u1))\n" +
                                "CREATE (u)-[:Belong]->(u1)",
                        parameters("titlePost", titlePost, "wineryName", wineryName));
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    *//**
     * Delete the relation belong between a review and a winery, to indicate to which winery
     * that review refers to.
     *
     * @param titlePost:  review's title;
     * @param wineryName: winery's name;
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deleteRelationBelong(final String titlePost, final String wineryName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Belong]-(p:Post{wineryName : $wineryName})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost, "wineryName", wineryName));
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Delete an user from the social.
     *
     * @param taster_name: user's name to drop.
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deleteUser(final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})\n" +
                                "DELETE u",
                        parameters("taster_name", taster_name));
                System.out.println("User drop successfully (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    *//**
     * Delete a review from the social.
     *
     * @param titlePost: review to drop.
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deletePost(final String titlePost) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(p:User{titlePost : $titlePost})\n" +
                                "DELETE p",
                        parameters("titlePost", titlePost));
                System.out.println("Post drop successfully (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    *//**
     * Delete a winary from the social.
     *
     * @param wineryName: winery to drop.
     * @return result: indicates if the operation has been done successfully.
     *//*
    public boolean deletePage(final String wineryName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(p:User{wineryName : $wineryName})\n" +
                                "DELETE p",
                        parameters("wineryName", wineryName));
                System.out.println("Winery drop successfully (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    *//**
     * Show all users that are followed by a given user of the social.
     *
     * @param taster_name: user's name.
     * @return followeUsers: list of users followed by taster_name.
     *//*
    public HashSet<User> allFollowedUserByTaster_name(final String taster_name) {
        HashSet<User> followedUsers;
        try (Session session = driver.session()) {
            followedUsers = session.readTransaction((TransactionWork<HashSet<User>>) tx -> {
                Result result = tx.run("MATCH p=(n:User{taster_name: $taster_name})-[:Follow]->(u:User)\n" +
                                "RETURN u.taster_name AS taster_name",
                        parameters("taster_name", taster_name));
                HashSet<User> users = new HashSet<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("taster_name").asString());
                    users.add(u);
                }

                return users;
            });
        } catch (Exception e) {
            followedUsers = null;
        }
        return followedUsers;
    }


    *//**
     * Extract ten users randomly from the social network and add the follow relation between the users and the selected user
     *
     *
     * @param selected_taster_name: user's name.
     *//*
    public void randomFollowByUser(final String selected_taster_name) {
        try (Session session = driver.session()) {
            List<String> random = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (p:User) RETURN p.taster_name as taster_name LIMIT 10");

                ArrayList<String> randomUsers = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    if (!(r.get("taster_name").asString().equals(selected_taster_name))) {
                        String taster_name;
                        randomUsers.add(r.get("taster_name").asString());
                        taster_name = r.get("taster_name").asString();
                        createRelationFollow(selected_taster_name, taster_name);
                        createRelationFollow(taster_name, selected_taster_name);
                    }
                }
                return randomUsers;
            });
            System.out.println(random);
        }
    }


    *//**
     * Extract ten post from the social network to put like given a
     * tester name.
     *
     * @param selected_taster_name: user's name.
     *//*
    public void randomLikeByUser(final String selected_taster_name) {
        try (Session session = driver.session()) {
            List<String> random = session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (p:Post) RETURN p.titlePost as titlePost LIMIT 10");

                ArrayList<String> randomPost = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    randomPost.add(r.get("titlePost").asString());
                    String titlePost;
                    titlePost = r.get("titlePost").asString();
                    createRelationLikeByTitle(titlePost, selected_taster_name);

                }
                return randomPost;
            });
            System.out.println(random);
        }
    }


    *//**
     * Method that count the nodes in the Social
     *//*
    public ArrayList<String> countGraphNodes() {
        ArrayList<String> random;
        try (Session session = driver.session()) {
            random = (ArrayList<String>) session.readTransaction((TransactionWork<List<String>>) tx -> {
                Result result = tx.run("MATCH (n) RETURN count(n) as count");

                ArrayList<String> count = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    count.add(String.valueOf(r.get("count").asInt()));
                }
                return count;
            });
        }
        return random;
    }

    *//**
     * Return list of reviews that are in Social Network but only the description (body)
     * @return
     *//*
    public ArrayList<String> returnAllReviews() {
        ArrayList<String> random;
        try (Session session = driver.session()) {
            random = session.readTransaction((TransactionWork<ArrayList<String>>) tx -> {
                Result result = tx.run("MATCH (p:Post) RETURN p.description as description");

                ArrayList<String> rev = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    rev.add(r.get("description").asString() + "\n");
                }
                return rev;
            });
        }
        return random;
    }

    *//**
     * Return the list of wineries (name) that are inside Social Network
     * @return rev: list of wineris
     *//*
    public ArrayList<String> returnAllWinery() {
        ArrayList<String> random;
        try (Session session = driver.session()) {
            random = session.readTransaction((TransactionWork<ArrayList<String>>) tx -> {
                Result result = tx.run("MATCH (p:Page) RETURN p.country, p.wineryName as winery");

                ArrayList<String> rev = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    rev.add(r.get("winery").asString() + "\n");
                }
                return rev;
            });
        }
        return random;
    }*/
