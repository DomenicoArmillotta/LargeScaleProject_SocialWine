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
        Menu menu = new Menu();
        menu.MainMenu();














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