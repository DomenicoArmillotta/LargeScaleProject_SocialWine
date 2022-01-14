import beans.User;
import beans.Wine;
import databases.Crud_graph;
import databases.DbOperations;
import exception.WrongInsertionException;
import menu.Menu;
import scraping.InitTh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

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
        //graph_operation.showFollowedUserAndUnfollow("bob");
        //graph_operation.showAllUserAndFollow("bob");
        //graph_operation.showCommentRelatedWineAndPutLike("bob","wine1");
        //graph_operation.showAllWineMenu("bob");

        //graph_operation.shoALlCommentMadebyFriendsAndPutLike("bob");
        System.out.println("==========BOB===============");
        System.out.println("1" + " See wine menu");
        System.out.println("2" + " Homepage");
        System.out.println("3" + " MY PROFILE");
        System.out.println("4" + " Suggested user section");
        Scanner scanSelection = new Scanner(System.in);
        String selection = scanSelection.nextLine();
        if(selection.equals("1")){
            graph_operation.showAllWineMenu("bob");
        }else if (selection.equals("2")){
            graph_operation.shoALlCommentMadebyFriendsAndPutLike("bob");
        }else if(selection.equals("3")){
            //non funziona quando elimino un post
            graph_operation.showMyAccount("bob");
        }else if(selection.equals("4")){
            graph_operation.showSuggestedUserAndFollow("bob");
        }

    }
}