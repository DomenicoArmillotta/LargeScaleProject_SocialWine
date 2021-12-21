import databases.*;

public class Main {
    public static void main(String[] args){

       /* InitTh thread = new InitTh();
        thread.initThread();*/

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

        Crud_graph crud = new Crud_graph("bolt://localhost:7687","neo4j","0000");
        Advanced_graph adv = new Advanced_graph("bolt://localhost:7687","neo4j","0000");

       crud.addUser("Aldo");
        crud.addUser("Giovanni");
        crud.addUser("Giacomo");
        crud.addUser("Giuseppe");
        //crud.randomFollowByUser("Aldo");
        crud.addPostComplete("Aldo","titleProva" , "test" , "winery1" , "italy");
        crud.addPostComplete("Giacomo","titleProva2" , "prova" , "winery2" , "italy");
        crud.randomLikeByUser("Giovanni");

        /*
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
        crud.deleteRelationLike("ottimo vino","Giuseppe");*/
        //crud.createRelationFollow("Aldo","Giovanni");
        //crud.createRelationFollow("Giovanni","Giacomo");
        //crud.createRelationFollow("Giacomo","Giovanni");
        //crud.createRelationFollow("Giacomo","Giuseppe");
       /* crud.createRelationFollow("Giovanni","Giacomo");
        crud.createRelationFollow("Giovanni","Aldo");
        crud.createRelationFollow("Giovanni","Giuseppe");
        crud.createRelationFollow("Giuseppe","Giovanni");
        crud.createRelationFollow("Giuseppe","Giacomo");*/
    //adv.suggestedUserByFriends("Aldo");
      /*  crud.addPostComplete("Aldo","Il vino fa schifo","Il vino dava di tappo","Pizz");
        crud.createRelationLike("Il vino fa schifo","Giovanni");
        crud.createRelationLike("Il vino fa schifo","Giacomo");
        crud.addPostComplete("Aldo","Il vino era buono","l'ho pagato assai soldi","Cola");
        crud.createRelationLike("Il vino era buono","Giovanni");
        crud.createRelationLike("Il vino era buono","Giacomo");
        crud.createRelationLike("Il vino era buono","Giuseppe");*/
        //adv.FiveMostLikePost();


 /*       distinctUsers user = new distinctUsers();
        user.distinctUser();*/


    }
}