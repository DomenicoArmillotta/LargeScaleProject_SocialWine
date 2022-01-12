package databases;

import beans.Review;
import beans.User;
import org.neo4j.driver.*;

import java.util.*;

import static org.neo4j.driver.Values.parameters;


public class Crud_graph implements AutoCloseable {

    //registerUser -- ok
    //banUserByUsername -- ok
    //searchUserWithPrefix -- FARE



    //FOLLOW---------------
    //showAllUser  -- ok
    //createRelationFollow -- ok
    //deleteRelationFollow -- ok
    //showFollowedUsers -- ok

    //LIKE-----------------
    //putLikeByTitle -- ok
    //deleteLikeByTitle -- ok
    //showAllReviews -- ok

    //showReviewFriends -- FARE

    //CREATED----------------
    //createRelationCreated -- ok
    //deleteRelationCreated -- ok
    //addReview -- ok
    //deleteReviewByTitle -- ok
    //deleteOWNReview -- ok


    //LOGIN----------------------
    //checkLoginByUsername -- FARE




    //operation must be closed at the end of each operation?
    //quando dobbiamo visualizzare le review degli utenti da dove li prendiamo? mongo db o neo4j? o il titolo da qui e poi mongo?




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

    public void showAllUser() {
        try (Session session = driver.session()) {
            session.readTransaction((TransactionWork<HashSet<User>>) tx -> {
                Result result = tx.run("MATCH (u:User)\n" +
                                "RETURN u.username AS username , u.country as country");
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.print("Nome utente: ");
                    System.out.print(r.get("username").asString());
                    System.out.print("  nazione: ");
                    System.out.println(r.get("country").asString());
                }
                return null;
            });
        }
    }

    public void searchUserByPrefix() {
        //MATCH (n:Person)
        //WHERE n.name STARTS WITH 'Pet'
        //RETURN n.name, n.age

    }

    public void addReview(final String title, final String description , final String rating) {
        try (Session session = driver.session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (p:Post {title: $title, description: $description , rating: $rating })",
                        parameters("title", title, "description", description , "rating" , rating));
                System.out.println("Review added successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }


    public void deleteReviewByTitle(final String title) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(p:User{title : $title})\n" +
                                "DELETE p",
                        parameters("title", title));
                System.out.println("Post drop successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }

    //delete only own review by title and username
    public void deleteOwnReviewByTitle(final String title , final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username : $username}), (p:Post{title: $title}) \n" +
                                "WHERE  EXISTS ((u)-[:Created]->(p))\n" +
                                "DELETE p",
                        parameters("title", title , "username" , username));
                System.out.println("Review drop successfully (Neo4J)." + "\n");
                return null;
            });
        }
    }


    //broswe all review of social network
    public void showAllReviews() {
        try (Session session = driver.session()) {
            session.readTransaction((TransactionWork<HashSet<Review>>) tx -> {
                Result result = tx.run("MATCH (p:Post)\n" +
                                "RETURN p.title as title , p.description as description");
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.println("Titolo della Review : ");
                    System.out.println(r.get("title").asString());
                    System.out.println("Descrizione della Review : ");
                    System.out.println(r.get("description").asString());
                    System.out.println("Rating della Review : ");
                    System.out.println(r.get("rating").asString());
                }
                return null;
            });
        }
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

    //show a list of followed user
    public void showFollowedUsers(final String username) {
        try (Session session = driver.session()) {
            session.readTransaction((TransactionWork<HashSet<User>>) tx -> {
                Result result = tx.run("MATCH p=(n:User{username: $username})-[:Follow]->(u:User)\n" +
                                "RETURN u.username AS username , u.password as password",
                        parameters("username", username));
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.println(r.get("username").asString());
                    System.out.println(r.get("password").asString());
                }
                return null;
            });
        }
    }

    //relation between user that create a new review and the review
    public boolean createRelationCreated(final String title, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username}),(u1:Post{title: $title})\n" +
                                "WHERE NOT EXISTS ((u)-[:Created]->(u1))\n" +
                                "CREATE (u)-[:Created]->(u1)",
                        parameters("title", title, "username", username));
                return 1;

            });
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    public void deleteRelationCreated(final String title, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})-[f:Created]-(p:Post{title : $title})\n" +
                                "DELETE f",
                        parameters("title", title, "username", username));
                return null;
            });
        }

    }

    //return 1 if access granted, 0 if is not allow to enter ---> AGGIUSTARE
    public int checkLoginByUsername (final String username , final String passwordLogin) {
        int result = 0;

        return result;
    }

    public void putLikeByTitle(final String title, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User{username: $username}),(p:Post{title: $title})\n" +
                                "WHERE NOT EXISTS ((u)-[:Like]->(p))\n" +
                                "CREATE (u)-[:Like]->(p)",
                        parameters("title", title, "username", username));
                System.out.println("Like reaction successfully inserted (Neo4J)." + "\n");
                return null;
            });
        }
    }


    public boolean deleteLikeByTitle(final String title, final String username) {
        boolean result = true;
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH path=(u:User{username : $username})-[f:Like]-(p:Post{title : $title})\n" +
                                "DELETE f",
                        parameters("title", title, "username", username));
                System.out.println("Like drop successfully made it (Neo4J)." + "\n");
                return 1;
            });
        } catch (Exception e) {
            result = false;
        }
        return result;
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
