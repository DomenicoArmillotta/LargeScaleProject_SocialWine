package databases;
import beans.Review;
import beans.User;
import databases.Crud_graph;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.*;

public class Graph_operation {
    Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");

    //work tested
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
        ArrayList<User> users = new ArrayList<>(graph.showFollowedUsers(myUsername));
        int i = 0;
        System.out.println("==============All Followed User By Me============= ");
        for (i=0;i<users.size();i++){
            System.out.println("name = " + users.get(i).getUsername() + "   country = " + users.get(i).getCountry());
        }
        System.out.println("===================================================== ");


    }

    public  void showAllUser()
    {
        ArrayList<User> users = new ArrayList<>(graph.showAllUser());
        int i = 0;
        for (i=0;i<users.size();i++){
            System.out.println("nome = " + users.get(i).getUsername());
        }
    }

    public void showUserByUsername(String username){
        User user = graph.showUserByUsername("username");
        System.out.println("name  = " + user.getUsername() +"email = " + user.getEmail() + "country" + user.getCountry());
    }

    public void showCommentRelatedWineAndPutLike(String wineName){
        ArrayList<Review> reviews = new ArrayList<>(graph.showAllCommentRelatedWineName(wineName));
        int i =0;
        System.out.println("=================Comment of "+wineName+"=======================" );
        for (i=0;i<reviews.size();i++){
            System.out.println( reviews.get(i).getDescription());
            System.out.println("rating = " + reviews.get(i).getRating());
            System.out.println("=======================================================" );
        }
    }



}
