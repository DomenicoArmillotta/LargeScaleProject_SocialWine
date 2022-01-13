import beans.User;
import databases.Crud_graph;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.HashSet;

/**
 * In the main class is called the scraper that works before all programs.
 * To avoid cuncurrent print between the effective program and the scrpaer
 * it was added a thread the intentionally start the program with delay.
 */
public class Main {
    public static void main(String[] args)throws Exception  {

        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        //graph.registerUser("dom","00","0","@bho","it","prova@gmail.com");
        //graph.registerUser("giovanni","00","0","@bho","it","prova@gmail.com");
        //graph.createRelationFollow("dom" , "giovanni");
        //graph.registerUser("bob","00","0","@bho","it","prova@gmail.com");
        //graph.createRelationFollow("dom" , "bob");
        //graph.showFollowedUsers("dom");
        //graph.addReview("titolo","descrizione","rating");
        //graph.showAllReviews();
        //graph.showAllUser();
        //boolean result = false;
        //result = graph.checkLoginByUsername("dom" , "0045" , "0");
        //Menu menu = new Menu();
        //menu.MainMenu();
        graph.registerUser("bob","00","0","@tag","ita","email1");
        graph.registerUser("bill","00","0","@tag","ita","email1");
        graph.registerUser("adam","00","0","@tag","ita","email1");
        graph.createRelationFollow("bob","bill");
        graph.createRelationFollow("bob","adam");
        graph.createRelationFollow("bill","adam");
        graph.addWine("wine1","Belsito" ,"100" , "ita" , "rosso" , "winery1");
        graph.addWine("wine2","Car" ,"100" , "ita" , "rosso" , "winery1");
        //commento 1 vino 1
        graph.addComment("vino molto buono" , "75");
        graph.createRelationRelated("wine1" ,"vino molto buono" );
        graph.createRelationCreated("vino molto buono","bill");
        //commento 2 vino 1
        graph.addComment("vino abbastanza buono" , "80");
        graph.createRelationRelated("wine1" ,"vino abbastanza buono" );
        graph.createRelationCreated("vino abbastanza buono","adam");

        graph.putLikeByDescription("vino molto buono" , "bob");
        graph.showFollowedUsers("bob");
        graph.showAllCommentRelatedWineName("wine1");
        graph.showAllWine();
        graph.showAllUser();













        /*System.out.println("Program will start in 3 minutes from now");
        InitTh thread = new InitTh();
        System.out.println("Scraper in action:");
        thread.initThread();

        try {
            Thread.sleep(180000);
            Menu cd = new Menu();
            try {
                cd.MainMenu();
            } catch (WrongInsertionException wex){
                wex.printStackTrace();
            }
        } catch (InterruptedException e) {
            System.out.println("Thread is interrupted due to some error!");
        }*/
    }
}