package databases;
import beans.User;
import databases.Crud_graph;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.*;

public class Graph_operation {
    Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");

    public void createCommentOnWine(final String username){
        System.out.println("Enter the name of the wine to comment");
        Scanner scanWine = new Scanner(System.in);
        String wineName = scanWine.nextLine();
        System.out.println("Insert the comment");
        Scanner scanComment = new Scanner(System.in);
        String description = scanComment.nextLine();
        System.out.println("Insert the rating");
        Scanner scanRating = new Scanner(System.in);
        String rating = scanRating.nextLine();
        graph.addComment(description,rating);
        graph.createRelationCreated(description,username);
        graph.createRelationRelated(wineName,description);
    }

    public void showFollowedUserAndUnfollow(String myUsername){
        ArrayList<User> users = new ArrayList<>();
        users = graph.showFollowedUsers(myUsername);
        int i = 0;
        for (i=0;i<users.size();i++){
            System.out.println("nome = " + users.get(i).getUsername());
        }


    }

    public  void showAllUser()
    {
        ArrayList<User> users = new ArrayList<>();
        users = graph.showAllUser();
        int i = 0;
        for (i=0;i<users.size();i++){
            System.out.println("nome = " + users.get(i).getUsername());
        }
    }

    public void showUserByUsername(String username){
        User user = graph.showUserByUsername("username");
        System.out.println("name  = " + user.getUsername() +"email = " + user.getEmail() + "country" + user.getCountry());
    }



}
