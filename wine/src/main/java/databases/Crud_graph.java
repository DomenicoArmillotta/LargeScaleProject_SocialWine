package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.neo4j.driver.Values.parameters;

/**
 * Contains basic CRUD operation that are executed on Social Wine
 */
public class Crud_graph implements AutoCloseable {
    private final Driver driver;

    /**
     * Constructor that allows to start the connection with Neo4J.
     *
     * @param uri:      address of Neo4J where the DB is on;
     * @param user:     user's name;
     * @param password: DB's password;
     */
    public Crud_graph(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * Close Neo4J connection
     *
     * @throws Exception if something goes wrong during the closing
     */
    @Override
    public void close() throws Exception {
        driver.close();
    }

    /**
     * Add a new User on the Social Wine
     *
     * @param username:              user's name
     * @param password:              user's password
     * @param adminFlag:             flag to identify if user is an admin or not
     * @param twitter_taster_handle: user's twitter nickname
     * @param country:               user's country
     * @param email:                 user's email
     */
    public void registerUser(final String username, final String password, final String adminFlag, final String twitter_taster_handle, final String country, final String email) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:User {username: $username , password: $password , adminFlag: $adminFlag , twitter_taster_handle: $twitter_taster_handle , country: $country , email: $email})",
                        parameters("username", username, "password", password, "adminFlag", adminFlag, "twitter_taster_handle", twitter_taster_handle, "country", country, "email", email));
                return null;
            });
        }
    }


    /**
     * Given an username will return user's attributes
     *
     * @param username: username
     * @return userToShow: User
     */
    public User showUserByUsername(final String username) {
        User user;
        try (Session session = driver.session()) {
            user = session.readTransaction((TransactionWork<User>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})\n" +
                                "RETURN u.username AS username , u.country AS country , u.email AS email , u.twitter_taster_handle as twitter_taster_handle ",
                        parameters("username", username));
                User userToShow = null;
                while (result.hasNext()) {
                    Record r = result.next();
                    userToShow = new User(r.get("username").asString(), "", r.get("twitter_taster_handle").asString(), r.get("country").asString(), r.get("email").asString(), false);
                }
                return userToShow;
            });
        } catch (Exception e) {
            user = null;
        }
        return user;
    }


    /**
     * Shows all users of Social Wine with Name and Country of membership
     *
     * @return users: List of users with username, country pair
     */
    public ArrayList<User> showAllUser() {
        ArrayList<User> usertoshow;
        try (Session session = driver.session()) {
            usertoshow = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u:User)\n" +
                        "RETURN u.username AS username , u.country AS country ORDER BY u.username");
                ArrayList<User> users = new ArrayList<User>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User user = new User(r.get("username").asString(), "", "", "", "", false);
                    users.add(user);
                }
                return users;
            });
        } catch (Exception e) {
            usertoshow = null;
        }
        return usertoshow;
    }


    /**
     * Show all user's features that are followed by a given user
     *
     * @param username: Username
     * @return usersOutput: List of all followed users
     */
    public ArrayList<User> showFollowedUsers(final String username) {
        ArrayList<User> arrayUser = null;
        try (Session session = driver.session()) {
            arrayUser = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u1:User{username: $username}) , (u2:User)\n" +
                                "WHERE  EXISTS ((u1)-[:Follow]->(u2))\n" +
                                "RETURN u2.username AS username , u2.country AS country , u2.twitter_taster_handle AS twitter_taster_handle  , u2.email AS email",
                        parameters("username", username));
                ArrayList<User> usersOutput = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString(), null, r.get("twitter_taster_handle").asString(), r.get("country").asString(), r.get("email").asString(), false);
                    usersOutput.add(u);
                }
                return usersOutput;
            });
        } catch (Exception e) {

        }
        return arrayUser;
    }


    /**
     * Add a new wine on Social Wine
     *
     * @param wineName:    wine name
     * @param designation: the name of the wine given to the wine by the producer
     * @param price:       wine price
     * @param province:    production province
     * @param variety:     types of grapes used
     * @param winery:      producer
     */
    public void addWine(final String wineName, final String designation, final String price, final String province, final String variety, final String winery) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Wine {wineName: $wineName , designation: $designation , price: $price , province: $province ,  variety: $variety , winery: $winery  })",
                        parameters("wineName", wineName, "designation", designation, "price", price, "province", province, "variety", variety, "winery", winery));
                return null;
            });
        }
    }

    /**
     * Delete a wine from the social
     *
     * @param wineName: wine's name to delete
     */
    public void deleteWineByName(final String wineName) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:Wine{wineName : $wineName})\n" +
                                "DELETE u",
                        parameters("wineName", wineName));
                return null;
            });
        }
    }

    /**
     * List of all wines that are in Social Wine
     *
     * @return wines: List of wines
     */
    public ArrayList<Wine> showAllWine() {
        ArrayList<Wine> winetoshow;
        try (Session session = driver.session()) {
            winetoshow = session.readTransaction((TransactionWork<ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (w:Wine)\n" +
                        "RETURN w.wineName AS wineName ,w.designation AS designation , w.price AS price , w.province AS province , w.variety as variety , w.winery as winery ORDER BY w.wineName");
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int converted = Integer.parseInt(r.get("price").asString());
                    Wine wine = new Wine(r.get("wineName").asString(), r.get("designation").asString(), converted, r.get("province").asString(), r.get("variety").asString(), r.get("winery").asString(), "country");
                    wines.add(wine);
                }
                return wines;
            });
        } catch (Exception e) {
            winetoshow = null;
        }
        return winetoshow;
    }


    /**
     * Add a comment on Social wine
     *
     * @param description: comment text
     * @param rating:      rating to assign
     */
    public void addComment(final String description, final String rating) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Post {description: $description , rating: $rating })",
                        parameters("description", description, "rating", rating));
                return null;
            });
        }
    }

    /**
     * List of user that made a given comment's description
     *
     * @param description: comment description
     * @return users: User's list
     */
    public ArrayList<User> findUserByDescription(final String description) {
        ArrayList<User> arrayUser = null;
        try (Session session = driver.session()) {
            arrayUser = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u1:User) , (p:Post{description: $description})\n" +
                                "WHERE  EXISTS ((u1)-[:Created]->(p))\n" +
                                "RETURN u1.username AS username , u1.country AS country , u1.twitter_taster_handle AS twitter_taster_handle  , u1.email AS email",
                        parameters("description", description));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User user = new User(r.get("username").asString(), null, r.get("twitter_taster_handle").asString(), r.get("country").asString(), r.get("email").asString(), null);
                    users.add(user);
                }
                return users;
            });
        } catch (Exception e) {

        }
        return arrayUser;
    }


    //delete only own review by title and username

    /**
     * Delete only one comment by his description
     *
     * @param description: comment descripition
     */
    public void deleteCommentByDescription(final String description) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post{description: $description}) \n" +
                                "DELETE p",
                        parameters("description", description));
                return null;
            });
        }
    }


    /**
     * List of all comment related to given wine
     *
     * @param wineName: wine's name
     * @return commentOutput: List of wine's comments
     */
    public ArrayList<Review> showAllCommentRelatedWineName(final String wineName) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (p:Post) , (w:Wine{wineName: $wineName}) \n" +
                                "WHERE  EXISTS ((p)-[:Related]->(w))\n" +
                                "RETURN p.description as description , p.rating as rating",
                        parameters("wineName", wineName));
                ArrayList<Review> commentOutput = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(), convertedRating);
                    commentOutput.add(review);

                }
                return commentOutput;
            });
        } catch (Exception e) {
            commenttoshow = null;
        }
        return commenttoshow;
    }

    /**
     * List of all comments made by a given user's friend on Social Wine
     *
     * @param myUsername:     Username
     * @param usernameFriend: Friend username
     * @return reviews: List of comments
     */
    public ArrayList<Review> showCommentsFriends(final String myUsername, final String usernameFriend) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $myUsername}),(u1:User{username: $usernameFriend}) , (p:Post),(w:Wine)  \n" +
                                "WHERE  EXISTS ((u)-[:Follow]-(u1))\n" +
                                "AND  EXISTS ((u1)-[:Created]->(p))\n" +
                                "AND  EXISTS ((p)-[:Related]-(w))\n" +
                                "RETURN  p.description AS description , p.rating AS rating , w.wineName AS wineName\n",
                        parameters("myUsername", myUsername, "usernameFriend", usernameFriend));
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(), convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e) {
            commenttoshow = null;
        }
        return commenttoshow;
    }

    /**
     * List of all comment made by a given username
     *
     * @param myUsername: username
     * @return reviews: List of review
     */
    public ArrayList<Review> showMyComment(final String myUsername) {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $myUsername}), (p:Post),(w:Wine)  \n" +
                                "WHERE  EXISTS ((u)-[:Created]->(p))\n" +
                                "AND  EXISTS ((p)-[:Related]->(w))\n" +
                                "RETURN  p.description AS description , p.rating AS rating\n",
                        parameters("myUsername", myUsername));
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(), convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e) {
            commenttoshow = null;
        }
        return commenttoshow;
    }

    /**
     * List of all comments that are in Social Wine
     *
     * @return reviews: List of reviews
     */
    public ArrayList<Review> showAllComments() {
        ArrayList<Review> commenttoshow;
        try (Session session = driver.session()) {
            commenttoshow = session.readTransaction((TransactionWork<ArrayList<Review>>) tx -> {
                Result result = tx.run("MATCH (p:Post),(w:Wine)  \n" +
                        "WHERE  EXISTS ((p)-[:Related]->(w))\n" +
                        "RETURN  p.description AS description , p.rating AS rating ORDER BY p.rating\n");
                ArrayList<Review> reviews = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int convertedRating = Integer.parseInt(r.get("rating").asString());
                    Review review = new Review(r.get("description").asString(), convertedRating);
                    reviews.add(review);
                }
                return reviews;
            });
        } catch (Exception e) {
            commenttoshow = null;
        }
        return commenttoshow;
    }

    //create relation follow for the use, and check if not exist

    /**
     * Create "follow" relaltion between two users of Social Wine, checking if
     * not exists
     *
     * @param username1: First username
     * @param username2: Second username
     */
    public void createRelationFollow(final String username1, final String username2) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username1}),(u1:User{username: $username2})\n" +
                                "WHERE NOT EXISTS ((u)-[:Follow]->(u1))\n" +
                                "CREATE (u)-[:Follow]->(u1)",
                        parameters("username1", username1, "username2", username2));
                return null;
            });
        }
    }

    /**
     * Delete "follow" relation between two users of Social Wine
     *
     * @param username1
     * @param username2
     */
    public void deleteRelationFollow(final String username1, final String username2) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username1})-[f:Follow]-(u1:User{username : $username2})\n" +
                                "DELETE f",
                        parameters("username1", username1, "username2", username2));
                return null;
            });

        }
    }


    /**
     * Check if there is "created" relation between an user and a comment by its description
     *
     * @param description: comment's description
     * @param username:    username
     * @return result: true if the relation exists, false otherwise
     */
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

    /**
     * Delete "related" relation between a comment and an user
     *
     * @param description: comment description
     * @param username:    username
     */
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


    /**
     * Check if user is allow to enter or not and check if user that want to log-in is an user or an admin
     *
     * @param username:      username
     * @param passwordLogin: user's password
     * @param adminFlag:     user's flag that identifies him normal user or admin
     * @return check: true is the user could have access to Social Wine, false otherwise
     */
    public boolean checkLoginByUsername(final String username, final String passwordLogin, final String adminFlag) {
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
                    if ((r.get("password").asString()).equals(passwordLogin)) {
                        if ((r.get("adminFlag").asString()).equals(adminFlag)) {
                            check.set(true);
                        }
                    }
                }
                return String.valueOf(check);
            });

        }

        return check.get();
    }

    /**
     * Create "like" relation between an user and a comment, if its doesn't exists
     *
     * @param description: comment's description
     * @param username:    username
     */
    public void putLikeByDescription(final String description, final String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username}),(p:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((u)-[:Like]->(p))\n" +
                                "CREATE (u)-[:Like]->(p)",
                        parameters("description", description, "username", username));
                return null;
            });
        }
    }

    /**
     * Delete relation "like" between a comment and an user
     *
     * @param description: comment's description
     * @param username:    username
     */
    public void deleteLikeByDescription(final String description, final String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})-[f:Like]-(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description, "username", username));
                return null;
            });
        }
    }

    /**
     * Create relation "related" between a wine and a comment, if its doesn't exists
     *
     * @param wineName:    wine's name
     * @param description: comment description
     */
    public void createRelationRelated(final String wineName, final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (w:Wine{wineName: $wineName}),(p:Post{description: $description})\n" +
                                "WHERE NOT EXISTS ((p)-[:Related]->(w))\n" +
                                "CREATE (p)-[:Related]->(w)",
                        parameters("wineName", wineName, "description", description));
                return null;
            });
        }
    }



    /**
     * Delete all relation "like" of a given comment
     *
     * @param description: comment description
     */
    public void deleteAllRelationLikeByDescription(final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User)-[f:Like]->(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description));
                return null;
            });
        }

    }

    /**
     * Delete all relation "related" for a given comment
     *
     * @param description: comment description
     */
    public void deleteAllRelationRelatedByDescription(final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post{description : $description})-[f:Related]->(w:Wine)\n" +
                                "DELETE f",
                        parameters("description", description));
                return null;
            });
        }

    }

    /**
     * Delete all "created by" relations for a given comment
     *
     * @param description: comment description
     */
    public void deleteAllRelationCreatedByDescription(final String description) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User)-[f:Created]->(p:Post{description : $description})\n" +
                                "DELETE f",
                        parameters("description", description));
                return null;
            });
        }
    }

    /**
     * Delete all relation "follow" for a given username
     *
     * @param username: username
     */
    public void deleteAllRelationFollow(final String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})<-[f:Follow]->(u:User)\n" +
                                "DELETE f",
                        parameters("username", username));
                return null;
            });
        }
    }

    /**
     * Delete all relations "like" for a given username
     *
     * @param username: username
     */
    public void deleteAllRelationLike(final String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})-[f:Like]->(p:Post)\n" +
                                "DELETE f",
                        parameters("username", username));
                return null;
            });
        }
    }

    /**
     * Delete all relations "created" for a given username
     *
     * @param username: username
     */
    public void deleteAllRelationCreated(final String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})-[f:Created]->(p:Post)\n" +
                                "DELETE f",
                        parameters("username", username));
                return null;
            });
        }
    }

    /**
     * Delete an user from Social Wine
     *
     * @param username: username
     */
    public void deleteUserByUsername(final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username})\n" +
                                "DELETE u",
                        parameters("username", username));
                return null;
            });
        }
    }

    /**
     * Delete all relation "related by" for a given wine
     *
     * @param wineName: wine's name
     */
    public void deleteAllRelatedBynameWine(final String wineName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Post)-[f:Related]->(w:Wine{wineName: $wineName})\n" +
                                "DELETE f",
                        parameters("wineName", wineName));
                return null;
            });
        }
    }

    /**
     * Count all likes that a comment received
     *
     * @param description: comment description
     * @return nLike: like's number
     */
    public Number countLikeByDescription(final String description) {
        Number nLike = null;

        try (Session session = driver.session()) {
            nLike = session.readTransaction((TransactionWork<Number>) tx -> {
                Result result = tx.run("MATCH (u:User)-[r:Like]->(p:Post{description: $description})\n" +
                                "RETURN  COUNT(r) AS numLike",
                        parameters("description", description));
                Number likeNumber = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    likeNumber = r.get("numLike").asNumber();
                }
                return likeNumber;
            });
        } catch (Exception e) {
            nLike = null;
        }
        return nLike;
    }

    /**
     * Count all follows that a user has
     *
     * @param username: username
     * @return nFollowers: follow's number
     */
    public Number countFollowersByUsername(final String username) {
        Number nFollowers = null;

        try (Session session = driver.session()) {
            nFollowers = session.readTransaction((TransactionWork<Number>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})<-[r:Follow]-(u2:User)\n" +
                                "RETURN  COUNT(r) AS nfollowers",
                        parameters("username", username));
                Number followersNumber = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    followersNumber = r.get("nfollowers").asNumber();
                }
                return followersNumber;
            });
        } catch (Exception e) {
            nFollowers = null;
        }
        return nFollowers;
    }

    /**
     * Check if a comment was liked by an user
     *
     * @param description: comment description
     * @param username:    username
     * @return resultLike: 0 if user didn't put like, 1 otherwise
     */
    public int checkIfLikedByDescription(final String description, final String username) {
        Integer resultLike = 0;
        try (Session session = driver.session()) {
            resultLike = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username})-[:Like]->(p:Post{description: $description})\n" +
                                "RETURN u.username as username",
                        parameters("description", description, "username", username));
                Integer ifLike = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    if ((r.get("username").asString()).equals(username)) {
                        ifLike = 1;
                    }

                }
                return ifLike;
            });
        } catch (Exception e) {
            resultLike = 0;
        }
        return resultLike;
    }

    /**
     * Check if an user have commented a wine
     *
     * @param wineName: wine's name
     * @param username: username
     * @return resultCheck: 0 if the given user didn't comment the given wine, 1 otherwise
     */
    public int checkIfCommentedWine(final String wineName, final String username) {
        Integer resultcheck = 0;
        try (Session session = driver.session()) {
            resultcheck = session.readTransaction((TransactionWork<Integer>) tx -> {
                Result result = tx.run("MATCH (u:User{username: $username}),(p:Post),(w:Wine{wineName:$wineName})\n" +
                                "WHERE  EXISTS ((p)-[:Related]->(w))\n" +
                                "AND  EXISTS ((u)-[:Created]->(p))\n" +
                                "RETURN u.username as username",
                        parameters("wineName", wineName, "username", username));
                Integer ifresult = 0;
                while (result.hasNext()) {
                    Record r = result.next();
                    if ((r.get("username").asString()).equals(username)) {
                        ifresult = 1;
                    }

                }
                return ifresult;
            });
        } catch (Exception e) {
            resultcheck = null;
        }
        return resultcheck;
    }


    /**
     * Return a wine taking in consideration a comment description
     *
     * @param description: comment description
     * @return winetoshow: list of wine
     */
    public ArrayList<Wine> findWineByDescription(final String description) {
        ArrayList<Wine> winetoshow;
        try (Session session = driver.session()) {
            winetoshow = session.readTransaction((TransactionWork<ArrayList<Wine>>) tx -> {
                Result result = tx.run("MATCH (p:Post{description: $description})-[r:Related]->(w:Wine)\n" +
                                "RETURN w.wineName AS wineName ,w.designation AS designation , w.price AS price , w.province AS province , w.variety as variety , w.winery as winery",
                        parameters("description", description));
                ArrayList<Wine> wines = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    int converted = Integer.parseInt(r.get("price").asString());
                    Wine wine = new Wine(r.get("wineName").asString(), r.get("designation").asString(), converted, r.get("province").asString(), r.get("variety").asString(), r.get("winery").asString(), "null");
                    wines.add(wine);
                }
                return wines;
            });
        } catch (Exception e) {
            winetoshow = null;
        }
        return winetoshow;
    }

    /**
     * Used by the scraper to populate Social Wine with new informations
     *
     * @param wineName:    wine name
     * @param designation: the name of the wine given to the wine by the producer
     * @param price:       wine price
     * @param province:    production province
     * @param variety:     types of grapes used
     * @param winery:      producer
     * @param country:     production country
     * @param country:     user's country
     * @param email:       user's email
     * @param description: comment body
     * @param rating:      wine's rating
     */
    public void addPostComplete(final String wineName, final String variety, final String country, final String province, final String price, final String winery, final String designation, final Integer rating, final String description, final String taster_twitter_handle, final String taster_name, final String user_country, final String email) {
        addWine(wineName, designation, price, province, variety, winery);
        addComment(description, rating.toString());
        registerUser(taster_name, "0000", "false", taster_twitter_handle, "None", "None");
        createRelationRelated(wineName, description);
        createRelationCreated(description, taster_name);
    }

    /**
     * Make a normal user, admin. May only be used by administrators
     * @param username: user to be made admin
     */
    public void switchToAdmin(String username){
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username : $username})\n" +
                                "SET u.adminFlag = 'true' ",
                        parameters("username", username));
                return null;
            });
        }
    }


    /**
     * List of 10 randomic users that follow a given user
     *
     * @param username: username
     * @return suggestedUsers: randomic user's list
     */
    public ArrayList<User> show10RandomUsers(final String username) {
        ArrayList<User> suggestedUsers;
        try (Session session = driver.session()) {
            suggestedUsers = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH (u1:User{username: $username}),(u2:User)\n" +
                                "WHERE NOT EXISTS ((u1)-[:Follow]->(u2))\n" +
                                "RETURN u2.username AS username , u2.country as country , u2.twitter_taster_handle as twitter_taster_handle\n" +
                                "LIMIT 10",
                        parameters("username", username));
                ArrayList<User> users = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    User u = new User(r.get("username").asString(), "", r.get("twitter_taster_handle").asString(), r.get("country").asString(), "", false);
                    users.add(u);
                }
                return users;
            });
        } catch (Exception e) {
            suggestedUsers = null;
        }
        return suggestedUsers;
    }
}

