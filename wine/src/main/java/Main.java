import beans.User;
import beans.Wine;
import com.mongodb.client.MongoCursor;
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

        //fare menu searc wine





       // Populating_wine_document pop = new Populating_wine_document();
       // pop.poplulateData();
        DbOperations graph_operation = new DbOperations();
        Crud_mongo mongo = new Crud_mongo();
        //inizialization TEST
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");

        //TEST
        graph.registerUser("adam","00","0","@user1","it","rr");
        graph.registerUser("bob","00","1","@user1","it","rr");
        graph.createRelationFollow("adam","bob");
        graph.createRelationFollow("bob","adam");
        //graph.deleteWineByName("wine1");

        Menu menu = new Menu();
        menu.MainMenu();

    }
}
