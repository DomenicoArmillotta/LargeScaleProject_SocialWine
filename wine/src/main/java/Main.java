import beans.User;
import databases.Crud_graph;
import databases.DbOperations;
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
        DbOperations graph_operation = new DbOperations();

        /*graph.registerUser("bob","00","0","@tag","ita","email");
        graph.registerUser("bill","00","0","@tag","ita","email");
        graph.registerUser("adam","00","0","@tag","ita","email");
        graph.addWine("wine1","desc1","150","ita","A","A");
        graph.addWine("wine2","desc2","100","ita","A","A");
        graph.addComment("vino buono","78");
        graph.addComment("vino medio","70");
        graph.createRelationRelated("wine1","vino buono");
        graph.createRelationRelated("wine1","vino medio");
        graph.createRelationCreated("vino buono","bob");
        graph.createRelationCreated("vino medio","bill");
        graph.createRelationFollow("bob","bill");
        graph.createRelationFollow("bob","adam");
        graph.putLikeByDescription("vino buono","bill");
        graph.putLikeByDescription("vino medio","adam");*/
        //graph_operation.showAllUserAndFollow("bill");
        //graph_operation.showFollowedUserAndUnfollow("bill");
        //graph_operation.showCommentRelatedWineAndPutLike("bill","wine1");
        //graph_operation.showAllWineAndWriteComment("adam");
        //graph_operation.showSuggestedUserAndFollow("bill");
        graph_operation.showTrendingCommentWithLikeAndPuttingLike("bob");








    }
}