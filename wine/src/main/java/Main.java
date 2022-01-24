import databases.Crud_graph;
import databases.Populating_function_social;
import menu.Menu;
import scraping.InitTh;


/**
 * In the main class is called the scraper and the menu. Scraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args){
        //MonngoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));;
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        //graph.registerUser("admin","admin","true","admin","ita","adm@gmail.com");
        //graph.deleteUserByUsername("admin");
        graph.createRelationFollow("Domenico","Leonardo");
        graph.createRelationFollow("Domenico","Roger Voss");
        graph.createRelationFollow("Roger Voss","Domenico");
        graph.createRelationFollow("Bashar","Leonardo");
        graph.createRelationFollow("Domenico","Paul Gregutt");
        graph.createRelationFollow("Domenico","Matt Kettmann");
        graph.createRelationFollow("Domenico","Joe Czerwinski");
        graph.createRelationFollow("Joe Czerwinski","Paul Gregutt");
        graph.createRelationFollow("Joe Czerwinski","Jeff Jenssen");
        graph.createRelationFollow("Jeff Jenssen","Leonardo");
        graph.createRelationFollow("Anne Krebiehl MW","Paul Gregutt");
        graph.createRelationFollow("Sean P. Sullivan","Domenico");




        InitTh init = new InitTh();
        init.initThread();
        Menu menu = new Menu();
        menu.MainMenu();
    }
}
