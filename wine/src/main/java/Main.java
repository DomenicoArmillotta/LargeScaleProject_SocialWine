import beans.User;
import databases.Crud_graph;
import databases.DbOperations;
import databases.Populating_User_Review_Collection;
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

        //Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
       // DbOperations graph_operation = new DbOperations();

        //graph.showCommentsFriends("bill","adam");
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph_operation.showUserByUsernameAndFollow("bob","adam");
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph.showAllUser();
        //graph_operation.showAllUser();
        //graph_operation.showFollowedUserAndUnfollow("bill");
        //graph_operation.showUserByUsername("bill");
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph_operation.showCommentAndPutLike("wine1");
        //graph_operation.showUserByUsernameAndFollow("bob");
        //graph_operation.showAllUserAndFollow();
        //graph.showAllWine();
        //graph_operation.showAllWineAndWriteComment();
        //graph.showCommentsFriends("bob" , "adam");
        Populating_User_Review_Collection pop=new Populating_User_Review_Collection();
        pop.poplulateData();










    }
}