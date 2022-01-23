import menu.Menu;
import scraping.InitTh;
import scraping.ScraperThread;


/**
 * In the main class is called the scraper and the menu. Scraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args){
        InitTh sc = new InitTh();
        sc.initThread();
        Menu menu = new Menu();
        menu.MainMenu();
    }
}
