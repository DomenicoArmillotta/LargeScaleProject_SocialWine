import menu.Menu;
import scraping.InitTh;


/**
 * In the main class is called the scraper and the menu. Scraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args){
        //MonngoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));;
        InitTh init = new InitTh();
        init.initThread();
        Menu menu = new Menu();
        menu.MainMenu();
    }
}
