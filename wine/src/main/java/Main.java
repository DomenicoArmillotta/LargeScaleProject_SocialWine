import databases.Crud_graph;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

/**
 * In the main class is called the scraper that works before all programs.
 * To avoid cuncurrent print between the effective program and the scrpaer
 * it was added a thread the intentionally start the program with delay.
 */
public class Main {
    public static void main(String[] args)throws Exception  {
        System.out.println("Program will start in 3 minutes from now");
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
        }
    }
}