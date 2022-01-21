import databases.Crud_graph;
import databases.Crud_mongo;
import databases.Populating_function_social;
import menu.Menu;


/**
 * In the main class is called the scraper and the menu. Scraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Populating_function_social pop = new Populating_function_social();
        pop.populateSocial();
        /*Menu menu = new Menu();
        menu.MainMenu();*/
    }
}
