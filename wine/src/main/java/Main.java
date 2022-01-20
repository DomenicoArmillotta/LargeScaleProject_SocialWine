import menu.Menu;


/**
 * In the main class is called the scraper and the menu. Sraper will work concurrently to menu
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Menu menu = new Menu();
        menu.MainMenu();
    }
}
