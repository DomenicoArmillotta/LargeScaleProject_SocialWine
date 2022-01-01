package menu;

import beans.User;
import databases.*;
import exception.AlreadyPopulatedException;
import exception.InsertedWrongNumberException;
import login.DistinctUsers;
import login.LoginAdmin;
import login.LoginUser;

import java.util.*;

/**
 * The class contains the core of user/admin choice. From here the user could choice what hw wants to do
 */
public class Menu {


    /**
     * Here there are the two different menus that will appear with respect to if the
     * person that want to login ia a narmal user or an admin.
     * Here, also, are initialized all the other classes like LoginAdmin, LoginUser,
     * DistinctUser, Crud_mongo, Advanced_mongo, Crud_graph, Advanced_graph
     * @throws Exception
     */
    public void MainMenu() throws Exception{

        LoginAdmin logAdm = new LoginAdmin();
        LoginUser logUse = new LoginUser();
        Populating_function_social populate=new Populating_function_social();
        DistinctUsers us = new DistinctUsers();
        us.distinctUser();
        logAdm.addAdmin();
        Crud_mongo mongo = new Crud_mongo();
        Advanced_mongo adv = new Advanced_mongo();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        Advanced_graph advgraph = new Advanced_graph("bolt://localhost:7687", "neo4j", "0000");

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
                        if (logAdm.logIn()) {
                            while (true) {
                                System.out.println("You can do this operations:");
                                System.out.println("A" + " Populate Social based on review collection of MongoDB");
                                System.out.println("B" + " Top 10 countries that have most wineries in descending order");
                                System.out.println("C" + " Display top-20 wines' varieties according to their mean price");
                                System.out.println("D" + " Top-5 users with the highest average of them review scores.");
                                System.out.println("E" + " Create 10 follow relation between selected user and 10 random people");
                                System.out.println("F" + " Create 10 like relation between selected user and 10 random post");
                                System.out.println("G" + " Add an user");
                                System.out.println("H" + " Ban an user");
                                System.out.println("I" + " Add a winery");
                                System.out.println("L" + " Drop a winery");
                                System.out.println("X" + " Terminate program");

                                System.out.println("\nWhat you want to do?");
                                Scanner scan = new Scanner(System.in);
                                String next = scan.nextLine();
                                String choiceStatistics = next;

                                switch (choiceStatistics) {
                                    case "A":
                                        try {
                                            populate.populateSocial();
                                        } catch (AlreadyPopulatedException e){
                                            System.out.println("Graph already populated You can't do this now!"+"\n");
                                        }
                                        break;
                                    case "B":
                                        adv.topTenCountriesWineries();
                                        break;
                                    case "C":
                                        adv.topTwentyVarietiesAvgPrice();
                                        break;
                                    case "D":
                                        try{
                                            adv.topFiveUsersHighestAvgScores();
                                        } catch (NumberFormatException e){
                                            System.out.println("There is some problem with Points field!");
                                        }
                                        break;
                                    case "E":
                                        String selected_user;
                                        Scanner inputName = new Scanner(System.in);
                                        System.out.println("Name of the User that you would select ");
                                        selected_user = inputName.nextLine();
                                        graph.randomFollowByUser(selected_user);
                                        break;
                                    case "F":
                                        String selected_user2;
                                        Scanner inputName2 = new Scanner(System.in);
                                        System.out.println("Name of the User that you would select ");
                                        selected_user2 = inputName2.nextLine();
                                        graph.randomLikeByUser(selected_user2);
                                        break;
                                    case "G":
                                        String taster_name;
                                        String taster_twitter_handle;
                                        Scanner inputTaster = new Scanner(System.in);
                                        System.out.println("Name of the taster that you want to add:");
                                        taster_name = inputTaster.nextLine();
                                        Scanner inputTwitter = new Scanner(System.in);
                                        System.out.println("Twitter name of the taster that you want to add:");
                                        taster_twitter_handle = inputTwitter.nextLine();
                                        mongo.addUser(taster_name, taster_twitter_handle);
                                        graph.addUser(taster_name);
                                        break;
                                    case "H":
                                        String twitter;
                                        Scanner inputTwitterName = new Scanner(System.in);
                                        System.out.println("Twitter name of the taster that you want to ban:");
                                        twitter = inputTwitterName.next();
                                        mongo.deleteUser(twitter);
                                        graph.deleteUser(twitter);
                                        break;
                                    case "I":
                                        String country;
                                        String winery;
                                        Scanner inputWinery = new Scanner(System.in);
                                        System.out.println("Name of the winery that you want to add:");
                                        winery = inputWinery.nextLine();
                                        Scanner inputCountry = new Scanner(System.in);
                                        System.out.println("Country of the winery that you want to add:");
                                        country = inputCountry.nextLine();
                                        mongo.addWinery(winery, country);
                                        graph.addPageWinery(winery, country);
                                        break;
                                    case "L":
                                        String win;
                                        Scanner inputWin = new Scanner(System.in);
                                        System.out.println("Here the list of all wineries of the social:");
                                        System.out.println(graph.returnAllWinery());
                                        System.out.println("Name of the winery that you want to drop:");
                                        win = inputWin.nextLine();
                                        mongo.deleteWinery(win);
                                        graph.deletePage(win);
                                        break;
                                    case "X":
                                        System.out.println("Exiting program...");
                                        System.exit(0);
                                        break;
                                    default:
                                        System.out.println("This is not a valid menu option... Please try again!");
                                        break;
                                }
                            }
                        }
                    }

                    //user login
                case 2:
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        String myName = logUse.getName();
                        while (true) {
                            System.out.println("You can do the following things:");
                            System.out.println("A" + " Follow your friend");
                            System.out.println("B" + " Unfollow a  friend");
                            System.out.println("C" + " Put like on a Post by Title");
                            System.out.println("D" + " Put like on a Post by its Description (body)");
                            System.out.println("E" + " Delete like on a Post");
                            System.out.println("F" + " Add a Post on the social");
                            System.out.println("G" + " Suggested friends' list and follow one of them");
                            System.out.println("H" + " Discover the top 5 trending Post on the social and put like on one of them");
                            System.out.println("I" + " See all user followed and unfollow one of them");
                            System.out.println("L" + " See all review from taster_name");
                            System.out.println("X" + " Terminate program");

                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;

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
                                    graph.createRelationLikeByTitle(titlePostToPutLike, myName);
                                    System.out.println("Done");
                                    break;
                                case "D":
                                    String descriptionPostToPutLike;
                                    Scanner inputZX = new Scanner(System.in);
                                    System.out.println("Which post you want to like? Description: ");
                                    descriptionPostToPutLike = inputZX.next();
                                    graph.createRelationLikeByDescription(descriptionPostToPutLike, myName);
                                    System.out.println("Done");
                                    break;
                                case "E":
                                    String titlePostToDeleteLike;
                                    Scanner inputD = new Scanner(System.in);
                                    System.out.println("Which post you want to delete like? TitlePost: ");
                                    titlePostToDeleteLike = inputD.next();
                                    graph.deleteRelationLike(titlePostToDeleteLike, myName);
                                    System.out.println("Done");
                                    break;
                                case "F":
                                    System.out.println("Add post in the social network");
                                    System.out.println("Insert the title of the post");
                                    String titleofthepost;
                                    Scanner inputTitle = new Scanner(System.in);
                                    titleofthepost = inputTitle.next();
                                    System.out.println("Insert the review of the wine");
                                    String descriptionofthepost;
                                    Scanner inputDescription = new Scanner(System.in);
                                    descriptionofthepost = inputDescription.next();
                                    System.out.println("Insert the winery name of the wine");
                                    String wineryName;
                                    Scanner inputWineryName = new Scanner(System.in);
                                    wineryName = inputWineryName.next();
                                    System.out.println("Insert the winery country of the wine");
                                    String wineryCountry;
                                    Scanner inputWineryCountry = new Scanner(System.in);
                                    wineryCountry = inputWineryCountry.next();
                                    System.out.println("Insert the points of the wine ");
                                    String winePoints;
                                    Scanner inputPoints = new Scanner(System.in);
                                    winePoints = inputPoints.next();
                                    System.out.println("Insert the price of the wine ");
                                    String winePrice;
                                    Scanner inputPrice = new Scanner(System.in);
                                    winePrice = inputPrice.next();
                                    mongo.createReview(winePoints, titleofthepost, descriptionofthepost, myName, "", 100, "", "", "", "", "", wineryCountry, wineryName);
                                    graph.addPostComplete(myName, titleofthepost, descriptionofthepost, wineryName, wineryCountry);
                                    System.out.println("Done");
                                    break;
                                case "G":
                                    System.out.println("This is the list of suggested user for you: ");
                                    advgraph.suggestedUserByFriends(myName);
                                    User[] users = advgraph.suggestedUserByFriends(myName).toArray(new User[advgraph.suggestedUserByFriends(myName).size()]);
                                    ArrayList<User> arrusr = new ArrayList<>(Arrays.asList(users));
                                    int j = 1;
                                    HashMap<Integer, String> friendCounter = new HashMap<>();
                                    for (int i = 0; i < arrusr.size(); i++) {
                                        friendCounter.put(j++, arrusr.get(i).getTaster_name() );
                                    }
                                    System.out.println("For each suggested user of the list is assigned a value! Write the number corresponding to the user" +
                                            " that you want as friend...");
                                    System.out.println("This is the list with the numbers:");
                                    System.out.println(friendCounter);
                                    Scanner scannerF = new Scanner(System.in);
                                    System.out.print("Enter the number (0 if the list is empty): ");
                                    int numb = scannerF.nextInt();
                                    if (numb <= friendCounter.size()) {
                                        if (friendCounter.containsKey(numb)) {
                                            String friend = friendCounter.get(numb);
                                            graph.createRelationFollow(myName, friend);
                                        }
                                    } else throw new InsertedWrongNumberException("Inserted number doesn't exists!");

                                    break;
                                case "H":
                                    System.out.println("This is the list of the tranding post on SocialWine (title = num like)");
                                    advgraph.FiveMostLikePost();
                                    int y = 1;
                                    HashMap<Integer, String> likeCounter = new HashMap<>();
                                    List keysrev = new ArrayList(advgraph.FiveMostLikePost().keySet());
                                    for (int i=0; i<keysrev.size();i++){
                                        likeCounter.put(y++, (String) keysrev.get(i));
                                    }
                                    System.out.println("For each tranding post is assigned a value! Write the number corresponding to the post" +
                                            " to put like on...");
                                    System.out.println("This is the list with the numbers:");
                                    System.out.println(likeCounter);
                                    Scanner scanner = new Scanner(System.in);
                                    System.out.print("Enter the number (0 if the list is empty): ");
                                    int num = scanner.nextInt();
                                    try{
                                        if (num <= likeCounter.size()){
                                            if (likeCounter.containsKey(num)){
                                                String post = likeCounter.get(num);
                                                graph.createRelationLikeByTitle(String.valueOf(post),myName);
                                            }
                                        } else throw new InsertedWrongNumberException("Inserted number doesn't exists!");
                                    } catch (InsertedWrongNumberException exc){
                                        exc.getMessage();
                                    }


                            break;
                                case "I":
                                    graph.allFollowedUserByTaster_name(myName);
                                    User[] allus = graph.allFollowedUserByTaster_name(myName).toArray(new User[graph.allFollowedUserByTaster_name(myName).size()]);
                                    ArrayList<User> allusarr = new ArrayList<>(Arrays.asList(allus));
                                    int w = 1;
                                    HashMap<Integer, String> frCounter = new HashMap<>();
                                    for (int i = 0; i < allusarr.size(); i++) {
                                        frCounter.put(w++, allusarr.get(i).getTaster_name() );
                                    }
                                    System.out.println("For each of your friend  is assigned a value! Write the number corresponding to the friend" +
                                            " that you want to unfollow...");
                                    System.out.println("This is the list with the numbers:");
                                    System.out.println(frCounter);
                                    Scanner scannerFr = new Scanner(System.in);
                                    System.out.print("Enter the number (0 if the list is empty): ");
                                    int number = scannerFr.nextInt();
                                    if (number <= frCounter.size()) {
                                        if (frCounter.containsKey(number)) {
                                            String friend = frCounter.get(number);
                                            graph.deleteRelationFollow(myName, friend);
                                        }
                                    } else throw new InsertedWrongNumberException("Inserted number doesn't exists!");
                                    break;
                                case "L":
                                    System.out.println("This is the list of all post that are in Social Wine (");
                                    graph.returnAllReviews();
                                    System.out.println(graph.returnAllReviews());
                                    break;
                                case "M":
                                    System.out.println("Insert the name of the taster that you would see reviews");
                                    String tasterName2;
                                    Scanner inputTasterName2 = new Scanner(System.in);
                                    tasterName2 = inputTasterName2.nextLine();
                                    mongo.showReviewFromTaster_name(tasterName2);
                                    break;
                                case "X":
                                    System.out.println("Exiting program...");
                                    System.exit(0);
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

