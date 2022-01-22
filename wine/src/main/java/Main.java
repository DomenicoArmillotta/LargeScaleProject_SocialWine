import menu.Menu;
import scraping.ScraperThread;


/**
 * In the main class is called the scraper and the menu. Scraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args){
        ScraperThread sc = new ScraperThread();
        sc.run();
        Menu menu = new Menu();
        menu.MainMenu();
    }
}
