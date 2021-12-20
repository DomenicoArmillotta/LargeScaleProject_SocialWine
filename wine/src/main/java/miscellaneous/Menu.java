package miscellaneous;


import com.mongodb.client.*;
import databases.advanced_graph;
import databases.advanced_mongo;
import databases.crud_graph;
import databases.crud_mongo;
import scraping.InitTh;
import scraping.scraperThread;
import static com.mongodb.client.model.Filters.eq;

import java.util.Scanner;

public class Menu {


    public void MainMenu() throws Exception {
        loginAdmin login = new loginAdmin();
        InitTh thread = new InitTh();
        thread.initThread();
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        scraperThread scraper = new scraperThread();
        crud_mongo mongo = new crud_mongo();
        advanced_mongo adv = new advanced_mongo();
        crud_graph graph = new crud_graph("bolt://localhost:7687", "neo4j", "root");
        advanced_graph advgraph = new advanced_graph("bolt://localhost:7687", "neo4j", "root");

        System.out.println("\n***SOCIAL WINE APPLICATION***\n");

        while (true) {
            System.out.println("\nAre you user or admin?");
            System.out.println("1" + " Admin");
            System.out.println("2" + " User");
            System.out.println("0" + " Terminate program");
            Scanner scanA = new Scanner(System.in);
            String nextIntString = scanA.nextLine();
            int choice = Integer.parseInt(nextIntString);

            switch (choice) {
                case 0:
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("This is not a valid menu option... Please try again!");
                    break;
                    //admin login
                case 1:
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (login.logIn()) {
                            System.out.println("You can do this statistics:");
                            System.out.println("A" + " Top 10 countries that have most wineries in descending order");
                            System.out.println("B" + " Display top-20 wines' varieties according to their mean price");
                            System.out.println("C" + " Top-5 users with the highest average of them review scores.");
                            System.out.println("D" + " Create 10 follow relation between selected user and 10 random people");
                            System.out.println("E" + " Create 10 like relation between selected user and 10 random post");
                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;

                            switch (choiceStatistics) {
                                case "A":
                                    adv.topTenCountriesWineries();
                                    break;
                                case "B":
                                    adv.topTwentyVarietiesAvgPrice();
                                    break;
                                case "C":
                                    adv.topFiveUsersHighestAvgScores();
                                    break;
                                case "D":
                                    String selected_user;
                                    Scanner inputName = new Scanner(System.in);
                                    System.out.println("Name of the User that you would select ");
                                    selected_user = inputName.next();
                                    advgraph.randomFollowByUser(selected_user);
                                    break;
                                case "E":
                                    String selected_user2;
                                    Scanner inputName2 = new Scanner(System.in);
                                    System.out.println("Name of the User that you would select ");
                                    selected_user2 = inputName2.next();
                                    advgraph.randomLikeByUser(selected_user2);
                                    break;
                            }

                        }
                    }

                //user login
                case 2:
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (login.logIn()) {
                            System.out.println("You can do these things :");
                            System.out.println("A" + " Follow your friend");
                            System.out.println("B" + " Unfollow a  friend");
                            System.out.println("C" + " Put like on a Post");
                            System.out.println("D" + " Delete like on a Post");
                            System.out.println("E" + " Add a Post on the social");
                            System.out.println("F" + " have tje list of suggested friends");
                            System.out.println("G" + " Discover the top 5 trending Post on the social");
                            System.out.println("H" + " See all user followed");
                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;
                            String myName = null; //--> in this variable there is my name to do the query

                            switch (choiceStatistics) {
                                case "A":
                                    String userToFollow;
                                    Scanner inputA = new Scanner(System.in);
                                    System.out.println("who do you want to follow? Name: ");
                                    userToFollow = inputA.next();
                                    graph.createRelationFollow(myName,userToFollow);
                                    System.out.println("Done");
                                    break;
                                case "B":
                                    String userToUnfollow;
                                    Scanner inputB = new Scanner(System.in);
                                    System.out.println("who do you want to Unfollow? Name: ");
                                    userToUnfollow = inputB.next();
                                    graph.deleteRelationFollow(myName,userToUnfollow);
                                    System.out.println("Done");
                                    break;
                                case "C":
                                    String titlePostToPutLike;
                                    Scanner inputC = new Scanner(System.in);
                                    System.out.println("which post you want to like? TitlePost: ");
                                    titlePostToPutLike = inputC.next();
                                    graph.createRelationLike(titlePostToPutLike,myName);
                                    System.out.println("Done");
                                    break;
                                case "D":
                                    String titlePostToDeleteLike;
                                    Scanner inputD = new Scanner(System.in);
                                    System.out.println("which post you want to delete like? TitlePost: ");
                                    titlePostToDeleteLike = inputD.next();
                                    graph.deleteRelationLike(titlePostToDeleteLike,myName);
                                    System.out.println("Done");
                                    break;
                                case "E":
                                    System.out.println("Add post in the social network");
                                    System.out.println("insert the title of the post");
                                    String titleofthepost;
                                    Scanner inputF = new Scanner(System.in);
                                    titleofthepost = inputF.next();
                                    System.out.println("insert the review of the wine");
                                    String descriptionofthepost;
                                    Scanner inputG = new Scanner(System.in);
                                    descriptionofthepost = inputG.next();
                                    System.out.println("insert the winery name of the wine");
                                    String wineryName;
                                    Scanner inputE = new Scanner(System.in);
                                    wineryName = inputG.next();
                                    System.out.println("insert the winery country of the wine");
                                    String wineryCountry;
                                    Scanner inputH = new Scanner(System.in);
                                    wineryCountry = inputG.next();
                                    graph.addPostComplete(myName,titleofthepost,descriptionofthepost,wineryName,wineryCountry);
                                    System.out.println("Done");
                                    break;
                                case "F":
                                    System.out.println("this is the list of suggested user for you: ");
                                    advgraph.suggestedUserByFriends(myName);
                                    break;
                                case "G":
                                    System.out.println("this is the list of the tranding post on SocialWine ");
                                    advgraph.FiveMostLikePost();
                                    break;
                                case "H":
                                    System.out.println("this is the list of all followed user by this account ");
                                    graph.allFollowedUserByTaster_name(myName);
                                    break;

                            }

                        }
                    }
            }
        }
    }
}

