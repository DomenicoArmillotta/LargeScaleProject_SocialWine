import beans.User;
import beans.Wine;
import databases.*;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * In the main class is called the scraper that works before all programs.
 * To avoid cuncurrent print between the effective program and the scrpaer
 * it was added a thread the intentionally start the program with delay.
 */
public class Main {
    public static void main(String[] args)throws Exception  {

       // Populating_wine_document pop = new Populating_wine_document();
       // pop.poplulateData();
        //menu 6 admin --> fare una cosa come lo user per eliminare e ceracre gli utenti
        DbOperations graph_operation = new DbOperations();

        //inizialization TEST
        /*graph.registerUser("bob","00","1","@tag","ita","email");
        graph.registerUser("bill","00","0","@tag","ita","email");
        graph.registerUser("adam","00","0","@tag","ita","email");
        graph.addWine("wine1","des1","100","B","A","w1");
        graph.addWine("wine2","des1","100","B","A","w1");
        graph.createRelationFollow("bob","bill");
        graph.createRelationFollow("adam","bill");
        graph.deleteWineByName("wine1");*/
        //test finished

        Menu menu = new Menu();
        menu.MainMenu();

    }
}
