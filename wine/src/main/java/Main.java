import databases.*;

public class Main {
    public static void main(String[] args){
        //crud_mongo mongo = new crud_mongo();

        /*mongo.createReview("85","Prova","Wine was good","Giovanni","@giovanni 98",
                20,"pdor","kmer","Puglia","","Bari","Italy","Ressa");*/

        //mongo.deleteReviewsByTaster_Name("Giovanni ");

        //mongo.findReviewByWinery("Ressa ");

        //mongo.updateAllPrice(20,23);

        advanced_mongo ad_mongo = new advanced_mongo();
        ad_mongo.topTenCountriesWineries();
        //ad_mongo.topTwentyVarietiesAvgPrice();
        //ad_mongo.topFiveUsersHighestAvgScores();
    }
}