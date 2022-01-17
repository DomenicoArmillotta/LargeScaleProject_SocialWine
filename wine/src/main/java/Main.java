import beans.User;
import beans.Wine;
import databases.*;
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

       // Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
      //  DbOperations graph_operation = new DbOperations();
        //Populating_wine_document populate=new Populating_wine_document();
       // populate.poplulateData();
        // Populating_wine_document populate=new Populating_wine_document();
       //populate.poplulateData();

        /*Advanced_mongo adv = new Advanced_mongo();
        adv.topFiveWines();*/
        /*Crud_mongo crud = new Crud_mongo();
        Advanced_mongo adv = new Advanced_mongo();
        adv.topFiveCountryAccordingScore();
        adv.topFiveMostExpensiveVarieties();
        adv.topFiveUsersHighestAvgScores();*/

        //crud.addComment("Domaines Vinsmoselle 2014 Bech-Kleinmacher Naumberg Grand Premier Cru Auxerrois (Moselle Luxembourgeoise)","Leonardo",80,"Vino buono","@leo","ita","cazzo",false);
        //crud.deleteAllCommentForGivenUser("Leonardo");
        //crud.addComment("Zanetti NV Case Bianche Extra Dry  (Prosecco di Conegliano e Valdobbiadene)","Leonardo",80,"Vino buono","@leo","ita","cazzo",false);
        //crud.deleteComment("A great cocktail of fruit flavors propel this wine out of the glass. It is ripe and crisp at the same time, the fruits running from peach to grapefruit and back again. There is plenty of acidity, crisp green apple structure, but at the end it is all about richness, a mouthful of delicious Chardonnay.","Roger Voss","Olivier Leflaive 2006 Les Folatières Premier Cru  (Puligny-Montrachet)");
        //crud.createWine("Taverello","Brut","Italy","Ba",40,"Leo",80,"buono","Enoteca","@le","it","leo@ii",false);
        //crud.createWine("Taverello","Brut","Ger","Ba",40,"Leo",82,"bad","Enoteca","@le","it","leo@ii",false);
        //crud.createWine("Taverello","Stt","Ger","Ba",40,"tryr",82,"bad","Enoteca","@le","it","leo@ii",false);


        //Crud_mongo crud = new Crud_mongo();
        //Populating_function_social pop = new Populating_function_social();
        //pop.populateSocial();
        //crud.deleteWine("Tavernello");
        //Advanced_mongo adv = new Advanced_mongo();
        //adv.topFiveMostExpensiveVarieties();
        //adv.topFiveCountryAccordingRating();
        //adv.topFiveWinesAccordinglyRatingsInsertedByUser();
        //crud.createWine("Tavernello","Rosso","Italia","Bari",56,"Giuseppe",77,"Vino delizioso","@Leonardo","Italia","cazzo",false);
        //crud.createWine("Tavernello","Rosso","Italia","Firenze",54,"Giovanni",80,"Vino delizioso","@Leonardo","Italia","cazzo",false);
        //crud.createWine("Tavernello","Rosso","Italia","Pisa",30,"Antonio",75,"Vino delizioso","@Leonardo","Italia","cazzo",false);
        //crud.createWine("Tavernello","Rosso","It","Mi","Alba",70,"Leonardo",90,"Vino buono","Enoteca","@leo","It","cazzo@gmail.com");
        //crud.createWine("Tavernello","Rosso","It","Ba","Deld",90,"Luca",90,"Vino buono","Enoteca","@leo","It","cazzo@gmail.com");
        /*Advanced_mongo adv = new Advanced_mongo();
        adv.topFiveWines();
        adv.moreExpensiveVariety();*/
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
       // graph.createRelationRelated("wine2","vino cattivo");
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");

        //graph.createRelationFollow("bob","adam");
        //graph.createRelationCreated("vino delicato","adam");
        //graph.registerUser("bob","00","1","@tagAdmin","ita","email");
        //graph.addWine("wine1","des1","100","B","A","w1");
        Menu menu = new Menu();
        menu.MainMenu();

    }
}
