package databases;

import beans.User;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.neo4j.driver.Values.parameters;
// query to do :

// add user  ------ ok
// add review Post ------- ok
// add pageWinery ------- ok

// delete user -----ok
// delete review  -----ok
// delete PageWinery  ------ok

// add relation Like ------ok
// add relation FOLLOW ------- ok
// add relation belong of ------ok
// add relation post created by ----ok

// remove relation follow ------ok
//remove relation like -----ok
// remove relation belong of  -----ok
//remove relation post created by -----ok

//search user
//search post
//search page



public class crud_graph implements AutoCloseable  {
    private final Driver driver;


    public crud_graph( String uri, String user, String password ) {
            driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    //add people
    public void addUser( final String taster_name ) {
        try ( Session session = driver.session() ) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:User {taster_name: $taster_name})",
                        parameters( "taster_name" , taster_name) );
                return null;
            });
        }
    }

    //this query is used to add a post and make the connection "created by" and "belong"
    public void addPostComplete(final String taster_name, final String titlePost, final String description , final String wineryName, final String country) {
        addPost(titlePost ,description );
        createRelationBelong(titlePost,wineryName);
        createRelationCreated(titlePost , taster_name);
        addPageWinery(wineryName,country);
    }


        //add post
    public void addPost( final String titlePost, final String description ) {
        try ( Session session = driver.session() ) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:Post {titlePost: $titlePost, description: $description})",
                        parameters( "titlePost", titlePost, "description", description) );
                return null;
            });
        }
    }

    //add Page winery
    public void addPageWinery( final String wineryName, final String country ) {
        try ( Session session = driver.session() ) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MERGE (p:Page {wineryName: $wineryName, country: $country})",
                        parameters( "wineryName", wineryName, "country", country) );
                return null;
            });
        }
    }

    //create follow between user and the edge have direction
    public boolean createRelationFollow(final String taster_name1, final String taster_name2){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name1}),(u1:User{taster_name: $taster_name2})\n" +
                                "CREATE (u)-[:Follow]->(u1)",
                        parameters("taster_name1", taster_name1 , "taster_name2", taster_name2));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //delete the relation follow between two user
    public boolean deleteRelationFollow(final String taster_name1, final String taster_name2){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name1})-[f:Follow]-(u1:User{taster_name : $taster_name2})\n" +
                                "DELETE f",
                        parameters("taster_name1", taster_name1 , "taster_name2", taster_name2));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }


    //crete the relation like between one post and one user
    public boolean createRelationLike(final String titlePost, final String taster_name){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name}),(u1:Post{titlePost: $titlePost})\n" +
                                "CREATE (u)-[:Like]->(u1)",
                        parameters("titlePost", titlePost , "taster_name", taster_name));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //delete relation like between post and user
    public boolean deleteRelationLike(final String titlePost, final String taster_name){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Like]-(p:Post{titlePost : $titlePost})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost , "taster_name", taster_name));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //create the relation created by between post and User
    public boolean createRelationCreated(final String titlePost, final String taster_name){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH (u:User{taster_name: $taster_name}),(u1:Post{titlePost: $titlePost})\n" +
                                "CREATE (u)-[:Created]->(u1)",
                        parameters("titlePost", titlePost , "taster_name", taster_name));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //delete relation created between title and taster name
    public boolean deleteRelationCreated(final String titlePost, final String taster_name){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Created]-(p:Post{titlePost : $titlePost})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost , "taster_name", taster_name));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //relation between page of winery and the post
    public boolean createRelationBelong(final String titlePost, final String wineryName){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH (u:Post{titlePost: $titlePost}),(u1:Page{wineryName: $wineryName})\n" +
                                "CREATE (u)-[:Belong]->(u1)",
                        parameters("titlePost", titlePost , "wineryName", wineryName));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    //delete the relation belong between the post and the page
    public boolean deleteRelationBelong(final String titlePost, final String wineryName){
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})-[f:Belong]-(p:Post{wineryName : $wineryName})\n" +
                                "DELETE f",
                        parameters("titlePost", titlePost , "wineryName", wineryName));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    public boolean deleteUser( final String taster_name) {
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(u:User{taster_name : $taster_name})\n" +
                                "DELETE u",
                        parameters("taster_name", taster_name ));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    public boolean deletePost( final String titlePost) {
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(p:User{titlePost : $titlePost})\n" +
                                "DELETE p",
                        parameters("titlePost", titlePost ));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }

    public boolean deletePage( final String wineryName) {
        boolean result = true;
        try (Session session = driver.session()){
            session.writeTransaction( tx -> {
                tx.run("MATCH path=(p:User{wineryName : $wineryName})\n" +
                                "DELETE p",
                        parameters("wineryName", wineryName ));
                return 1;
            });
        } catch (Exception e){
            result = false;
        }
        return result;
    }




    public ArrayList<User> allFollowedUserByTaster_name(final String taster_name) {
        ArrayList<User> followedUsers;
        try (Session session = driver.session()) {
            followedUsers = session.readTransaction((TransactionWork<ArrayList<User>>) tx -> {
                Result result = tx.run("MATCH p=(n:User{taster_name: $taster_name})-[:Follow]->(u:User)\n" +
                                "RETURN u.taster_name AS taster_name\n" +
                                "ORDER BY number\n" ,
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
            followedUsers = null;
        }
        return followedUsers;
    }

}
