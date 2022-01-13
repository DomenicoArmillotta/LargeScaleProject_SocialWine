import beans.User;
import databases.Crud_graph;
import databases.Graph_operation;
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

        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        Graph_operation graph_operation = new Graph_operation();

        //graph.showCommentsFriends("bill","adam");
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph_operation.showUserByUsernameAndFollow("bob","adam");
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph.showAllUser();
        //graph_operation.showAllUser();
        //graph_operation.showFollowedUserAndUnfollow("bill");
        //graph_operation.showUserByUsername("bill");
        graph_operation.showFollowedUserAndUnfollow("bob");
        //graph_operation.showCommentAndPutLike("wine1");











    }
}