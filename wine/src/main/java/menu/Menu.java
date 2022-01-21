package menu;

import databases.Crud_graph;
import databases.DbOperations;

import java.util.Scanner;


/**
 * Contains the two mains menus (Admin/User/Unregistered user).
 */
public class Menu {

    /**
     * Menu for User, Admin, Unregistered user
     */
    public void MainMenu() {
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        DbOperations graph_operation = new DbOperations();

        while (true) {
            int user = 0;
            String userLoggedName = null;
            while (user == 0) {
                System.out.println("╔==================MENU====================╗");
                System.out.println("║ 1" + " User Login                             ║");
                System.out.println("║ 2" + " Admin Login                            ║");
                System.out.println("║ 3" + " Register new User                      ║");
                System.out.println("╚==========================================╝");
                System.out.println(" What do you want do?                     ");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) {
                    userLoggedName = null;
                    System.out.println("==============LOGIN ADMIN ===========");
                    System.out.println("Please enter your username: ");
                    Scanner scanLoginUsername = new Scanner(System.in);
                    String loginName = scanLoginUsername.nextLine();
                    System.out.println("Please enter your password: ");
                    Scanner scanLoginPsw = new Scanner(System.in);
                    String loginPsw = scanLoginPsw.nextLine();
                    if (graph_operation.userLogin2(loginName, loginPsw) == true) {
                        user = 1;
                        userLoggedName = loginName;
                    } else {
                        System.out.println("Password or username not correct");
                        user = 0;
                    }
                    //user = 1;
                } else if (selection.equals("2")) {
                    user = 2;
                    userLoggedName = null;
                    System.out.println("==============LOGIN ADMIN ===========");
                    System.out.println("Please enter your username: ");
                    Scanner scanLoginAdminUsername = new Scanner(System.in);
                    String loginAdminName = scanLoginAdminUsername.nextLine();
                    System.out.println("Please enter your password: ");
                    Scanner scanLoginAdminPsw = new Scanner(System.in);
                    String loginAdminPsw = scanLoginAdminPsw.nextLine();
                    if (graph_operation.adminLogin2(loginAdminName, loginAdminPsw) == true) {
                        user = 2;
                        userLoggedName = loginAdminName;
                    } else {
                        System.out.println("Password or username not correct");
                        user = 0;
                    }

                } else if (selection.equals("3")) {
                    System.out.println("===================Registration phase====================");
                    graph_operation.registerNewUser();
                    user = 0;
                }
            }
            while (user == 1) {
                System.out.println("╔===================USER==================╗");
                System.out.println("║ " + "1" + " See wine menu                         ║");
                System.out.println("║ 2" + " Homepage                              ║");
                System.out.println("║ 3" + " My profile                            ║");
                System.out.println("║ 4" + " Search a friend and know new people   ║");
                System.out.println("║ 5" + " Logout                                ║");
                System.out.println("╚=========================================╝");

                System.out.println(" Select operation : ");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) { //mongo ok
                    graph_operation.showAllWineMenu(userLoggedName);
                } else if (selection.equals("2")) { //mongo ok
                    graph_operation.homepageUser(userLoggedName);
                } else if (selection.equals("3")) {//mongo ok
                    graph_operation.showMyAccount(userLoggedName);
                    try {
                        graph.showUserByUsername(userLoggedName).getUsername();

                    } catch (NullPointerException e) {
                        System.out.println("Account deleted");
                        userLoggedName = null;
                        user = 0;
                    }
                } else if (selection.equals("4")) { //mongo ok
                    graph_operation.searchUserfromUser(userLoggedName);

                } else if (selection.equals("5")) {
                    user = 0;
                }

            }
            while (user == 2) {
                System.out.println("╔==============ADMIN==================╗");
                System.out.println("║ 1" + " See wine menu and Delete          ║");
                System.out.println("║ 2" + " Homepage                          ║");
                System.out.println("║ 3" + " My profile                        ║");
                System.out.println("║ 4" + " Find friends or new people        ║");
                System.out.println("║ 5" + " Browse all comments and Delete    ║");
                System.out.println("║ 6" + " Browse  user and Ban/delete       ║");
                System.out.println("║ 7" + " Social Network statistics         ║");
                System.out.println("║ 8" + " Logout                            ║");
                System.out.println("╚=====================================╝");

                System.out.println(" What do you want do?");
                Scanner scanSelection = new Scanner(System.in);
                String selection = scanSelection.nextLine();
                if (selection.equals("1")) { //mongo ok
                    //see wine menu put like and delete wine
                    graph_operation.showAllWineMenuAdmin(userLoggedName);
                } else if (selection.equals("2")) { //mongo ok
                    graph_operation.homepageAdmin(userLoggedName);
                } else if (selection.equals("3")) { //mongo ok
                    graph_operation.showMyAccount(userLoggedName);
                } else if (selection.equals("4")) {//mongo ok
                    graph_operation.searchUserfromUser(userLoggedName);
                } else if (selection.equals("5")) { //mongo ok
                    graph_operation.showCommentAdminMenu(userLoggedName);
                } else if (selection.equals("6")) { //mongo ok
                    graph_operation.usersMenuBanAdmin(userLoggedName);
                } else if (selection.equals("7")) {
                    graph_operation.statsMenuAdmin();
                } else if (selection.equals("8")) {
                    user = 0;
                }
            }
        }
    }
}


