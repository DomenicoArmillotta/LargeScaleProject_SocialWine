package databases;
import beans.Review;
import beans.User;
import beans.Wine;
import databases.Crud_graph;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.*;

public class Graph_operation {
    Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
    Advanced_graph adv_graph = new Advanced_graph("bolt://localhost:7687", "neo4j", "0000");

    public boolean adminLogin(){
        boolean result =false;
        System.out.println("Please enter your username: ");
        Scanner scanLoginAdminUsername = new Scanner(System.in);
        String loginAdminName = scanLoginAdminUsername.nextLine();
        System.out.println("Please enter your password: ");
        Scanner scanLoginAdminPsw = new Scanner(System.in);
        String loginAdminPsw = scanLoginAdminPsw.nextLine();
        if(graph.checkLoginByUsername(loginAdminName,loginAdminPsw,"1") == true){
            System.out.println("Caro Admin sei Entrato");
            result=true;
        }else{
            result=false;
        }
        return result;
    }

    public boolean userLogin(){
        boolean result =false;
        System.out.println("Please enter your username: ");
        Scanner scanLoginUserUsername = new Scanner(System.in);
        String loginUserName = scanLoginUserUsername.nextLine();
        System.out.println("Please enter your password: ");
        Scanner scanLoginUserPsw = new Scanner(System.in);
        String loginUserPsw = scanLoginUserPsw.nextLine();
        if(graph.checkLoginByUsername(loginUserName,loginUserPsw,"0") == true){
            System.out.println("Caro User sei Entrato");
            result=true;
        }else{
            result=false;
        }
        return result;
    }


    //tested
    public void registerNewUser(){
        System.out.println("Please enter your username: ");
        Scanner scanLoginName = new Scanner(System.in);
        String loginName = scanLoginName.nextLine();
        System.out.println("Please set your password: ");
        Scanner scanLoginPassword = new Scanner(System.in);
        String loginPassword = scanLoginPassword.nextLine();
        System.out.println("Please set your twitter tag: ");
        Scanner scanLoginTwitter = new Scanner(System.in);
        String loginTwitter = scanLoginTwitter.nextLine();
        System.out.println("Please set your country: ");
        Scanner scanLoginCountry = new Scanner(System.in);
        String loginCountry = scanLoginCountry.nextLine();
        System.out.println("Please set your email: ");
        Scanner scanLoginEmail = new Scanner(System.in);
        String loginEmail = scanLoginEmail.nextLine();
        graph.registerUser(loginName,loginPassword,"0",loginTwitter,loginCountry,loginEmail);
    }
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

    public  void showAllUserAndFollow()
    {
        ArrayList<User> users = new ArrayList<>(graph.showAllUser());
        int i = 0;
        for (i=0;i<users.size();i++){
            System.out.println("nome = " + users.get(i).getUsername() + "  country" + users.get(i).getCountry());
        }
    }

    public void showUserByUsernameAndFollow(String username){
        User user = graph.showUserByUsername(username);
        System.out.println("name  = " + user.getUsername() +"  email = " + user.getEmail() + "  country" + user.getCountry());
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
    public void showAllWineAndWriteComment(){
        ArrayList<Wine> wines = new ArrayList<>(graph.showAllWine());
        int i =0;
        System.out.println("==============List of All Wine on Social============= ");
        for (i=0;i<wines.size();i++){
            System.out.println( "wine name = " + wines.get(i).getWineName());
            System.out.println( "designation = " + wines.get(i).getDesignation());
            System.out.println( "price = " + wines.get(i).getPrice());
            System.out.println( "province = " + wines.get(i).getProvince());
            System.out.println( "variety = " + wines.get(i).getVariety());
            System.out.println( "winery = " + wines.get(i).getWinery());
            System.out.println("---------------------------------------------------");

        }
        System.out.println("===================================================== ");
    }

    //testare
    public void showSuggestedUserAndUnfollow(String myUsername){
        ArrayList<User> users = new ArrayList<>(adv_graph.showSuggestedUserByFriends(myUsername));
        int i = 0;
        System.out.println("==============All Followed User By Me============= ");
        for (i=0;i<users.size();i++){
            System.out.println("name = " + users.get(i).getUsername() + "   country = " + users.get(i).getCountry());
        }
        System.out.println("===================================================== ");

    }


    //testare , mettere count like con altra query
    public void showTreendingCommentWithLikeAndPuttingLike(){
        ArrayList<Review> reviews = new ArrayList<>(adv_graph.showTrendingComment());
        int i =0;
        System.out.println("=================Trending Comment=======================" );
        for (i=0;i<reviews.size();i++){
            System.out.println( reviews.get(i).getDescription());
            System.out.println("rating = " + reviews.get(i).getRating());
            System.out.println("-------------------------------------------------------" );
        }
        System.out.println("=======================================================" );
    }









}
