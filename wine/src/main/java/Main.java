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
    public static void main(String[] args)throws Exception {

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
        //graph.registerUser("bob","00","1","@tagAdmin","ita","email");
        //graph.addWine("wine2","prova","100","ita","abc","winery2");
        //graph.createRelationRelated("wine2","vino cattivo");


        while(true) {
            int user = 0;
            while (user == 0) {
                System.out.println("==========MENU===============");
                System.out.println("1" + " User Login");
                System.out.println("2" + " Admin Login");
                System.out.println("3" + " Register new User");
                System.out.println(" What do you want do?");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) {
                    user = 1;
                } else if (selection.equals("2")) {
                    user = 2;
                } else if (selection.equals("3")) {
                    System.out.println("===================Registration phase====================");
                    graph_operation.registerNewUser();
                    user = 0;
                }
            }
            while (user == 1) {
                //ADAM
                System.out.println("==========USER===============");
                System.out.println("1" + " See wine menu");
                System.out.println("2" + " Homepage");
                System.out.println("3" + " MY PROFILE");
                System.out.println("4" + " Suggested user section");
                System.out.println("5" + " Search a friend");
                System.out.println("6" + " Logout");
                System.out.println(" Select operation : ");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) {
                    graph_operation.showAllWineMenu("adam");
                } else if (selection.equals("2")) {
                    graph_operation.homepageUser("adam");
                } else if (selection.equals("3")) {
                    graph_operation.showMyAccount("adam");
                } else if (selection.equals("4")) {
                    graph_operation.showSuggestedUserAndFollow("adam");
                } else if (selection.equals("5")) {
                    graph_operation.searchUserfromUser("adam");

                } else if (selection.equals("6")) {
                    user = 0;
                }

            }
            while (user == 2) {
                //BOB
                System.out.println("==========ADMIN===============");
                System.out.println("1" + " See wine menu");
                System.out.println("2" + " Homepage");
                System.out.println("3" + " MY PROFILE");
                System.out.println("4" + " Broswe all comments"); //--> DA FARE (broswe and delete)
                System.out.println("5" + " Broswe all user");  //--> inserire il search
                System.out.println("6" + " Logout");
                System.out.println(" What do you want do?");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) {
                    //see wine menu put like and delete wine
                    graph_operation.showAllWineMenuAdmin("bob");
                } else if (selection.equals("2")) {
                    graph_operation.homepageAdmin("bob");
                } else if (selection.equals("3")) {
                    //non funziona quando elimino un post
                    graph_operation.showMyAccount("bob");
                }else if (selection.equals("4")) {
                    //broswe all comments and delete
                }else if (selection.equals("5")) {
                    //broswe or search user --> DONE
                    graph_operation.usersMenuBanAdmin("bob");
                } else if (selection.equals("6")) {
                    user = 0;
                }

            }
        }
    }
}