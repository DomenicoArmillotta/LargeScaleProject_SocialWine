package menu;


import com.mongodb.client.*;
import databases.Advanced_graph;
import databases.Advanced_mongo;
import databases.Crud_graph;
import databases.Crud_mongo;
import login.DistinctUsers;
import login.LoginAdmin;
import login.LoginUser;
import login.SaveLogin;
import scraping.InitTh;
import scraping.ScraperThread;
import static com.mongodb.client.model.Filters.eq;

import java.util.List;
import java.util.Scanner;

public class Menu {

    //this class is used to create and show the menu for the admin and the user
    //in the variable myName is stored the name of the user that is logged in this session
    public void MainMenu() throws Exception {
        LoginAdmin logAdm = new LoginAdmin();
        LoginUser logUse = new LoginUser();
        SaveLogin level = new SaveLogin();
        DistinctUsers us = new DistinctUsers();
        logAdm.addAdmin();
        us.distinctUser();
     /*   InitTh thread = new InitTh();
        thread.initThread();*/
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        ScraperThread scraper = new ScraperThread();
        Crud_mongo mongo = new Crud_mongo();
        Advanced_mongo adv = new Advanced_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "root");
        Advanced_graph advgraph = new Advanced_graph("bolt://localhost:7687", "neo4j", "root");

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
                    level.openDB();
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (logAdm.checkLogIn()) {
                            System.out.println("You can do this statistics:");
                            System.out.println("A" + " Top 10 countries that have most wineries in descending order");
                            System.out.println("B" + " Display top-20 wines' varieties according to their mean price");
                            System.out.println("C" + " Top-5 users with the highest average of them review scores.");
                            System.out.println("D" + " Create 10 follow relation between selected user and 10 random people");
                            System.out.println("E" + " Create 10 like relation between selected user and 10 random post");
                            System.out.println("F" + " Add an user");
                            System.out.println("G" + " Ban an user");
                            System.out.println("H" + " Add a winery");
                            System.out.println("I" + " Drop a winery");
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
                                    graph.randomFollowByUser(selected_user);
                                    break;
                                case "E":
                                    String selected_user2;
                                    Scanner inputName2 = new Scanner(System.in);
                                    System.out.println("Name of the User that you would select ");
                                    selected_user2 = inputName2.next();
                                    graph.randomLikeByUser(selected_user2);
                                    break;
                                case "F":
                                    String taster_name;
                                    String taster_twitter_handle;
                                    Scanner inputTaster = new Scanner(System.in);
                                    System.out.println("Name of the taster that you want to add:");
                                    taster_name = inputTaster.next();
                                    Scanner inputTwitter = new Scanner(System.in);
                                    System.out.println("Twitter name of the taster that you want to add:");
                                    taster_twitter_handle = inputTwitter.next();
                                    mongo.addUser(taster_name, taster_twitter_handle);
                                    break;
                                case "G":
                                    String twitter;
                                    Scanner inputTwitterName = new Scanner(System.in);
                                    System.out.println("Twitter name of the taster that you want to ban:");
                                    twitter = inputTwitterName.next();
                                    mongo.deleteUser(twitter);
                                    break;
                                case "H":
                                    String country;
                                    String winery;
                                    Scanner inputWinery = new Scanner(System.in);
                                    System.out.println("Name of the winery that you want to add:");
                                    winery = inputWinery.next();
                                    Scanner inputCountry = new Scanner(System.in);
                                    System.out.println("Country of the winery that you want to add:");
                                    country = inputCountry.next();
                                    mongo.addWinery(winery, country);
                                    break;
                                case "I":
                                    String win;
                                    Scanner inputWin = new Scanner(System.in);
                                    System.out.println("Name of the winery that you want to drop:");
                                    win = inputWin.next();
                                    mongo.deleteWinery(win);
                                    break;
                                default:
                                    System.out.println("This is not a valid menu option... Please try again!");
                                    break;
                            }

                        }
                    }

                    //user login
                case 2:
                    level.openDB();
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (logUse.checkLogIn()) {
                            System.out.println("You can do the following things:");
                            System.out.println("A" + " Follow your friend");
                            System.out.println("B" + " Unfollow a  friend");
                            System.out.println("C" + " Put like on a Post");
                            System.out.println("D" + " Delete like on a Post");
                            System.out.println("E" + " Add a Post on the social");
                            System.out.println("F" + " Suggested friends' list");
                            System.out.println("G" + " Discover the top 5 trending Post on the social");
                            System.out.println("H" + " See all user followed");
                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;
                            String myName = logUse.logIn();//--> ATTENTION = in this variable there is my name to do the query, and here you must put the name taken from the keyValue db

                            switch (choiceStatistics) {
                                case "A":
                                    String userToFollow;
                                    Scanner inputA = new Scanner(System.in);
                                    System.out.println("Who do you want to follow? Name: ");
                                    userToFollow = inputA.next();
                                    graph.createRelationFollow(myName, userToFollow);
                                    System.out.println("Done");
                                    break;
                                case "B":
                                    String userToUnfollow;
                                    Scanner inputB = new Scanner(System.in);
                                    System.out.println("Who do you want to Unfollow? Name: ");
                                    userToUnfollow = inputB.next();
                                    graph.deleteRelationFollow(myName, userToUnfollow);
                                    System.out.println("Done");
                                    break;
                                case "C":
                                    String titlePostToPutLike;
                                    Scanner inputC = new Scanner(System.in);
                                    System.out.println("Which post you want to like? TitlePost: ");
                                    titlePostToPutLike = inputC.next();
                                    graph.createRelationLike(titlePostToPutLike, myName);
                                    System.out.println("Done");
                                    break;
                                case "D":
                                    String titlePostToDeleteLike;
                                    Scanner inputD = new Scanner(System.in);
                                    System.out.println("Which post you want to delete like? TitlePost: ");
                                    titlePostToDeleteLike = inputD.next();
                                    graph.deleteRelationLike(titlePostToDeleteLike, myName);
                                    System.out.println("Done");
                                    break;
                                case "E":
                                    System.out.println("Add post in the social network");
                                    System.out.println("Insert the title of the post");
                                    String titleofthepost;
                                    Scanner inputF = new Scanner(System.in);
                                    titleofthepost = inputF.next();
                                    System.out.println("Insert the review of the wine");
                                    String descriptionofthepost;
                                    Scanner inputG = new Scanner(System.in);
                                    descriptionofthepost = inputG.next();
                                    System.out.println("Insert the winery name of the wine");
                                    String wineryName;
                                    Scanner inputE = new Scanner(System.in);
                                    wineryName = inputE.next();
                                    System.out.println("Insert the winery country of the wine");
                                    String wineryCountry;
                                    Scanner inputH = new Scanner(System.in);
                                    wineryCountry = inputH.next();
                                    graph.addPostComplete(myName, titleofthepost, descriptionofthepost, wineryName, wineryCountry);
                                    System.out.println("Done");
                                    break;
                                case "F":
                                    System.out.println("This is the list of suggested user for you: ");
                                    advgraph.suggestedUserByFriends(myName);
                                    break;
                                case "G":
                                    System.out.println("This is the list of the tranding post on SocialWine ");
                                    advgraph.FiveMostLikePost();
                                    break;
                                case "H":
                                    System.out.println("This is the list of all followed user by this account ");
                                    graph.allFollowedUserByTaster_name(myName);
                                    break;
                                default:
                                    System.out.println("This is not a valid menu option... Please try again!");
                                    break;
                            }

                        }
                    }

            }
        }
    }
}

