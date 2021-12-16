import databases.*;
import miscellaneous.*;

public class Main {
    public static void main(String[] args){
        //crud_mongo mongo = new crud_mongo();

        /*mongo.createReview("85","Prova","Wine was good","Giovanni","@giovanni 98",
                20,"pdor","kmer","Puglia","","Bari","Italy","Ressa");*/

        //mongo.deleteReviewsByTaster_Name("Giovanni ");

        //mongo.findReviewByWinery("Ressa ");

        //mongo.updateAllPrice(20,23);

        //advanced_mongo ad_mongo = new advanced_mongo();
        //ad_mongo.topTenCountriesWineries();
        //ad_mongo.topTwentyVarietiesAvgPrice();
        //ad_mongo.topFiveUsersHighestAvgScores();

        crud_graph crud = new crud_graph("bolt://localhost:7687","neo4j","root");
        advanced_graph adv = new advanced_graph("bolt://localhost:7687","neo4j","root");
/*        //connection.addUser("Ba cicc");
        crud.addUser("Aldo");
        crud.addUser("Giovanni");
        crud.addUser("Giacomo");
        crud.addUser("Giuseppe");
        crud.addPageWinery("Enoteca","Italy");
        crud.addPageWinery("Eno","Germany");
        crud.addPost("ottimo vino","il vino era molto buono");
        crud.createRelationBelong("ottimo vino","Enoteca");
        crud.createRelationCreated("ottimo vino","Aldo");
        crud.createRelationFollow("Aldo","Giovanni");
        crud.createRelationLike("ottimo vino","Giuseppe");
        crud.deletePage("Eno");
        crud.deletePost("ottimo vino");
        crud.deleteRelationBelong("ottimo vino","Enoteca");
        crud.deleteUser("Ba cicc");
        crud.deleteRelationCreated("ottimo vino","Aldo");
        crud.deleteRelationFollow("Aldo","Giovanni");
        crud.deleteRelationLike("ottimo vino","Giuseppe");
        crud.createRelationFollow("Aldo","Giovanni");
        crud.createRelationFollow("Aldo","Giacomo");
        crud.createRelationFollow("Aldo","Giuseppe");
        crud.createRelationFollow("Giovanni","Giacomo");
        crud.createRelationFollow("Giovanni","Aldo");
        crud.createRelationFollow("Giovanni","Giuseppe");
        crud.createRelationFollow("Giuseppe","Giovanni");
        crud.createRelationFollow("Giuseppe","Giacomo");*/

        distinctUsers user = new distinctUsers();
        user.distinctUser();

    }
}