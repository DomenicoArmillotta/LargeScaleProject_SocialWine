package databases;

import beans.Review;
import beans.User;
import beans.Wine;
import exception.ReviewAlreadyInserted;
import exception.UserNotPresentException;
import exception.WineNotExistsException;
import exception.WrongInsertionException;


import java.util.*;

public class DbOperations {
    Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
    Advanced_graph adv_graph = new Advanced_graph("bolt://localhost:7687", "neo4j", "0000");
    Advanced_mongo adv_mongo = new Advanced_mongo();
    Crud_mongo mongo = new Crud_mongo();

    //DONE
    public boolean adminLogin() {
        boolean result = false;
        System.out.println("==============LOGIN ADMIN ===========");
        System.out.println("Please enter your username: ");
        Scanner scanLoginAdminUsername = new Scanner(System.in);
        String loginAdminName = scanLoginAdminUsername.nextLine();
        System.out.println("Please enter your password: ");
        Scanner scanLoginAdminPsw = new Scanner(System.in);
        String loginAdminPsw = scanLoginAdminPsw.nextLine();
        if (graph.checkLoginByUsername(loginAdminName, loginAdminPsw, "1") == true) {
            System.out.println("Caro Admin sei Entrato");
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean adminLogin2(String loginAdminName, String loginAdminPsw) {
        boolean result = false;
        if (graph.checkLoginByUsername(loginAdminName, loginAdminPsw, "1") == true) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }


    //DONE
    public boolean userLogin() {
        boolean result = false;
        System.out.println("==============LOGIN USER ===========");
        System.out.println("Please enter your username: ");
        Scanner scanLoginUserUsername = new Scanner(System.in);
        String loginUserName = scanLoginUserUsername.nextLine();
        System.out.println("Please enter your password: ");
        Scanner scanLoginUserPsw = new Scanner(System.in);
        String loginUserPsw = scanLoginUserPsw.nextLine();
        if (graph.checkLoginByUsername(loginUserName, loginUserPsw, "0") == true) {
            System.out.println("Caro User sei Entrato");
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean userLogin2(String loginUserName, String loginUserPsw) {
        boolean result = false;

        if (graph.checkLoginByUsername(loginUserName, loginUserPsw, "0") == true) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }


    //DONE
    public void registerNewUser() {
        System.out.println("==============Register new User ===========");
        System.out.println("Please enter your username: ");
        Scanner scanLoginName = new Scanner(System.in);
        String loginName = scanLoginName.nextLine();
        System.out.println("Please set your password: ");
        try{
            Scanner scanLoginPassword = new Scanner(System.in);
            String loginPassword = scanLoginPassword.nextLine().trim();
            if (loginPassword.length() != 4 || loginPassword.trim().isEmpty() )
                throw new WrongInsertionException("The password must contains 4 digits or no spaces");
            System.out.println("Please set your twitter tag: ");
            Scanner scanLoginTwitter = new Scanner(System.in);
            String loginTwitter = scanLoginTwitter.nextLine();
            System.out.println("Please set your country: ");
            Scanner scanLoginCountry = new Scanner(System.in);
            String loginCountry = scanLoginCountry.nextLine();
            System.out.println("Please set your email: ");
            Scanner scanLoginEmail = new Scanner(System.in);
            String loginEmail = scanLoginEmail.nextLine();
            graph.registerUser(loginName, loginPassword, "0", loginTwitter, loginCountry, loginEmail);

        }catch (WrongInsertionException wx){
            System.out.println(wx.getMessage());
        }
    }

    //work tested DONE
    public void createCommentOnWine(final String username) {
        System.out.println("Enter the name of the wine to comment");
        Scanner scanWine = new Scanner(System.in);
        String wineName = scanWine.nextLine();
        System.out.println("Insert the comment");
        Scanner scanComment = new Scanner(System.in);
        String description = scanComment.nextLine();
        System.out.println("Insert the rating");
        Scanner scanRating = new Scanner(System.in);
        String rating = scanRating.nextLine();
        graph.addComment(description, rating);
        graph.createRelationCreated(description, username);
        graph.createRelationRelated(wineName, description);
    }

    //DONE tested
    public void showFollowedUserAndUnfollow(String myUsername) {
        ArrayList<User> users = new ArrayList<>(graph.showFollowedUsers(myUsername));
        if (users.size() != 0) {
            int i = 0;
            System.out.println("==============All Followed User By Me=============== ");
            for (i = 0; i < users.size(); i++) {


                System.out.println(i + " : name = " + users.get(i).getUsername() + "   country = " + users.get(i).getCountry());
                if (i != (users.size() - 1)) {
                    System.out.println("--------------------------------------------------");
                }
            }
            System.out.println("===================================================== ");
            System.out.println("Select a user to unfollow :");
            Scanner scanSelect = new Scanner(System.in);
            String selected = scanSelect.nextLine();
            if (selected.equals("X")) {

            } else {
                int selectedInt = Integer.parseInt(selected);
                graph.deleteRelationFollow(myUsername, users.get(selectedInt).getUsername());
            }
        } else {
            System.out.println("You dont have friends");
        }
    }

    //DONE tested --->made check followed
    public void showAllUserAndFollow(String myUsername) {
        ArrayList<User> users = new ArrayList<>(graph.showAllUser());
        int i = 0;
        System.out.println("==============Broswer All Users=============== ");
        for (i = 0; i < users.size(); i++) {
            System.out.println(i + " : nome = " + users.get(i).getUsername() + "  country" + users.get(i).getCountry());
            if (i != (users.size() - 1)) {
                System.out.println("------------------------------------------------");
            }
        }
        System.out.println("============================================== ");
        System.out.println("Select a user to follow :");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            int selectedInt = Integer.parseInt(selected);
            graph.createRelationFollow(myUsername, users.get(selectedInt).getUsername());
        }

    }

    //done
    public void showUserByUsernameAndFollow(String myUsername, String username) {
        User user = graph.showUserByUsername(username);
        if (user != null) {
            System.out.println("name: " + user.getUsername() + "\nemail: " + user.getEmail() + "\ncountry" + user.getCountry() + "\nFollowers:" + graph.countFollowersByUsername(user.getUsername()));
            System.out.println("Do you want to follow? y/n");
            Scanner scanSelect = new Scanner(System.in);
            String selection = scanSelect.nextLine();
            if (selection.equals("y")) {
                graph.createRelationFollow(myUsername, username);
            } else {

            }
        } else {
            System.out.println("User dont found");
        }

    }

    //DONE tested
    public void showCommentRelatedWineAndPutLike(String myUsername, String wineName) {
        ArrayList<Review> reviews = new ArrayList<>(graph.showAllCommentRelatedWineName(wineName));
        int i = 0;
        System.out.println("=================Comment of " + wineName + "=======================");
        for (i = 0; i < reviews.size(); i++) {
            System.out.println(i + " : Comment  ");
            System.out.println(reviews.get(i).getDescription());
            System.out.println("rating = " + reviews.get(i).getRating());
            System.out.println("like = " + graph.countLikeByDescription(reviews.get(i).getDescription()));
            if (i != (reviews.size() - 1)) {
                System.out.println("------------------------------------------------");
            }
        }
        System.out.println("=======================================================");
        System.out.println("Select a comment to put like :");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            int selectedInt = Integer.parseInt(selected);
            graph.putLikeByDescription(reviews.get(selectedInt).getDescription(), myUsername);
        }
    }


    //DONE tested
    public void showAllWineAndWriteComment(String myUsername) {
        ArrayList<Wine> wines = new ArrayList<>(graph.showAllWine());
        int i = 0;
        System.out.println("==============List of All Wine on Social============= ");
        for (i = 0; i < wines.size(); i++) {
            System.out.println("Wine to select " + i + " :");
            System.out.println("wine name = " + wines.get(i).getWineName());
            System.out.println("designation = " + wines.get(i).getDesignation());
            System.out.println("price = " + wines.get(i).getPrice());
            System.out.println("province = " + wines.get(i).getProvince());
            System.out.println("variety = " + wines.get(i).getVariety());
            System.out.println("winery = " + wines.get(i).getWinery());
            System.out.println("---------------------------------------------------");

        }
        System.out.println("===================================================== ");
        System.out.println("Select wine to write comment:");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            int selectedInt = Integer.parseInt(selected);
            System.out.println("Insert the comment");
            Scanner scanComment = new Scanner(System.in);
            String description = scanComment.nextLine();
            System.out.println("Insert the rating");
            Scanner scanRating = new Scanner(System.in);
            String rating = scanRating.nextLine();
            graph.addComment(description, rating);
            graph.createRelationCreated(description, myUsername);
            graph.createRelationRelated(wines.get(selectedInt).getWineName(), description);
        }


    }

    public void showCommentFriendAndPutLike(String myUsername, String friendUsername) {
        ArrayList<Review> reviews = new ArrayList<>(graph.showCommentsFriends(myUsername, friendUsername));
        int i = 0;
        System.out.println("=================Comment made by " + friendUsername + "=======================");
        for (i = 0; i < reviews.size(); i++) {
            System.out.println("Comment to select " + i + " :");
            System.out.println(reviews.get(i).getDescription());
            System.out.println("rating = " + reviews.get(i).getRating());
            System.out.println("=======================================================");
        }
        System.out.println("Select a comment to put like :");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            int selectedInt = Integer.parseInt(selected);
            graph.putLikeByDescription(reviews.get(selectedInt).getDescription(), myUsername);
        }
    }


    //puo essere parte della home
    public void homepageUser(String myUsername) {
        ArrayList<Review> allReview = new ArrayList<>();
        int k = 0;
        ArrayList<User> users = new ArrayList<>(graph.showFollowedUsers(myUsername));
        if (users.size() != 0) {
            int i = 0;
            for (i = 0; i < users.size(); i++) {
                ArrayList<Review> reviews = new ArrayList<>(graph.showCommentsFriends(myUsername, users.get(i).getUsername()));
                if (reviews.size() != 0) {
                    int j;
                    for (j = 0; j < reviews.size(); j++) {
                        System.out.println("============ " + k + " : Comment made by " + users.get(i).getUsername() + "===================");
                        k++;
                        allReview.add(reviews.get(j));
                        System.out.println("comment made to the wine:  " + graph.findWineByDescription(reviews.get(j).getDescription()).get(0).getWineName());
                        System.out.println(reviews.get(j).getDescription());
                        System.out.println("rating = " + reviews.get(j).getRating());
                        System.out.println("like = " + graph.countLikeByDescription(reviews.get(j).getDescription()));
                        if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 1) {
                            System.out.println("Like = V");
                        } else if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 0) {
                            System.out.println("Like = X");
                        }

                    }
                }
            }
            if(k!=0){
                System.out.println("=======================================================");
                System.out.println("what do you want to do?");
                System.out.println("1 : Put like on a Post");
                System.out.println("2 : Delete Like on a Post");
                Scanner scanSelectlike = new Scanner(System.in);
                String selectedLike = scanSelectlike.nextLine();
                if (selectedLike.equals("1")) {
                    System.out.println("Select a comment to put like :");
                    Scanner scanSelect = new Scanner(System.in);
                    String selected = scanSelect.nextLine();
                    if (selected.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selected);
                            if(selectedInt>=0 && selectedInt<=(allReview.size()-1)){
                                graph.putLikeByDescription(allReview.get(selectedInt).getDescription(), myUsername);
                            }else{
                                System.out.println("selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                } else if (selectedLike.equals("2")) {
                    System.out.println("Select a comment to delete like :");
                    Scanner scanSelectDeleteLike = new Scanner(System.in);
                    String selectedDeleteLike = scanSelectDeleteLike.nextLine();
                    if (selectedDeleteLike.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selectedDeleteLike);
                            if(selectedInt>=0 && selectedInt<=(allReview.size()-1)){
                                graph.deleteLikeByDescription(allReview.get(selectedInt).getDescription(), myUsername);
                            }else{
                                System.out.println("selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                }
            }else{
                System.out.println("No comment to show");
            }



        } else {
            System.out.println("You dont have friends");
        }

        System.out.println("Do you want see the trending post? y/n");
        Scanner scanSelectShow = new Scanner(System.in);
        String selectionShow = scanSelectShow.nextLine();
        if (selectionShow.equals("y")) {
            ArrayList<Review> trendingReviews = new ArrayList<>(adv_graph.showTrendingComment());
            if (trendingReviews.size() != 0) {


                int s = 0;
                System.out.println("=================Trending Comment=======================");
                for (s = 0; s < trendingReviews.size(); s++) {
                    System.out.println("Comment to select " + s + " :");
                    System.out.println(trendingReviews.get(s).getDescription());
                    System.out.println("rating = " + trendingReviews.get(s).getRating());
                    System.out.println("Like = " + graph.countLikeByDescription(trendingReviews.get(s).getDescription()));
                    System.out.println("made by:  = " + graph.findUserByDescription(trendingReviews.get(s).getDescription()).get(0).getUsername());
                    System.out.println("wine = " + graph.findWineByDescription(trendingReviews.get(s).getDescription()).get(0).getWineName());
                    if (graph.checkIfLikedByDescription(trendingReviews.get(s).getDescription(), myUsername) == 1) {
                        System.out.println("Like = V");
                    } else if (graph.checkIfLikedByDescription(trendingReviews.get(s).getDescription(), myUsername) == 0) {
                        System.out.println("Like = X");
                    }


                    if (s != (trendingReviews.size() - 1)) {
                        System.out.println("------------------------------------------------");
                    }
                }
                System.out.println("=======================================================");
                System.out.println("what do you want to do?");
                System.out.println("1 : Put like on a Post");
                System.out.println("2 : Delete Like on a Post");
                Scanner scanSelectlike = new Scanner(System.in);
                String selectedLike = scanSelectlike.nextLine();
                if (selectedLike.equals("1")) {
                    System.out.println("Select a comment to put like :");
                    Scanner scanSelect = new Scanner(System.in);
                    String selected = scanSelect.nextLine();
                    if (selected.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selected);
                            if (selectedInt >= 0 && selectedInt <= (trendingReviews.size() - 1)) {
                                graph.putLikeByDescription(trendingReviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection Wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                } else if (selectedLike.equals("2")) {
                    System.out.println("Select a comment to delete like :");
                    Scanner scanSelectDeleteLike = new Scanner(System.in);
                    String selectedDeleteLike = scanSelectDeleteLike.nextLine();
                    if (selectedDeleteLike.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selectedDeleteLike);
                            if (selectedInt >= 0 && selectedInt <= (trendingReviews.size() - 1)) {
                                graph.deleteLikeByDescription(trendingReviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection Wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                }



            /*System.out.println("Select a comment to put like :");
            Scanner scanSelect = new Scanner(System.in);
            String selected = scanSelect.nextLine();
            if(selected.equals("X")){

            }else {
                int selectedInt = Integer.parseInt(selected);
                graph.putLikeByDescription(trendingReviews.get(selectedInt).getDescription(), myUsername);
            }*/
            } else {
                System.out.println("There aren't trending comment");
            }
        } else {

        }


    }


    //puo essere parte della home
    public void homepageAdmin(String myUsername) {
        ArrayList<Review> allReview = new ArrayList<>();
        int k = 0;
        ArrayList<User> users = new ArrayList<>(graph.showFollowedUsers(myUsername));
        if (users.size() != 0) {
            int i = 0;
            for (i = 0; i < users.size(); i++) {
                ArrayList<Review> reviews = new ArrayList<>(graph.showCommentsFriends(myUsername, users.get(i).getUsername()));
                if (reviews.size() != 0) {


                System.out.println("============ " + k + " : Comment made by " + users.get(i).getUsername() + "===================");
                int j;
                for (j = 0; j < reviews.size(); j++) {
                    //System.out.println("Comment to select "+k+" :");
                    k++;
                    allReview.add(reviews.get(j));
                    System.out.println("comment made to the wine:  " + graph.findWineByDescription(reviews.get(j).getDescription()).get(0).getWineName());
                    System.out.println(reviews.get(j).getDescription());
                    System.out.println("rating = " + reviews.get(j).getRating());
                    System.out.println("like = " + graph.countLikeByDescription(reviews.get(j).getDescription()));

                    if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 1) {
                        System.out.println("Like = V");
                    } else if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 0) {
                        System.out.println("Like = X");
                    }
                }

                }else{
                    System.out.println("no comment to show");
                }
            }
            if(k!=0){
                System.out.println("=======================================================");
                System.out.println("what do you want to do?");
                System.out.println("1 : Put like on a Post");
                System.out.println("2 : Delete Like on a Post");
                System.out.println("3 : Delete comment"); //-->mongo ok
                Scanner scanSelectlike = new Scanner(System.in);
                String selectedLike = scanSelectlike.nextLine();
                if (selectedLike.equals("1")) {
                    System.out.println("Select a comment to put like :");
                    Scanner scanSelect = new Scanner(System.in);
                    String selected = scanSelect.nextLine();
                    if (selected.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selected);
                            if (selectedInt >= 0 && selectedInt <= (allReview.size() - 1)) {
                                graph.putLikeByDescription(allReview.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("Insert a number not a string");
                        }

                    }
                } else if (selectedLike.equals("2")) {
                    System.out.println("Select a comment to delete like :");
                    Scanner scanSelectDeleteLike = new Scanner(System.in);
                    String selectedDeleteLike = scanSelectDeleteLike.nextLine();
                    if (selectedDeleteLike.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selectedDeleteLike);
                            if (selectedInt >= 0 && selectedInt <= (allReview.size() - 1)) {
                                graph.deleteLikeByDescription(allReview.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }
                    }
                } else if (selectedLike.equals("3")) {
                    System.out.println("Select Comment to delete :");
                    Scanner scanSelect2 = new Scanner(System.in);
                    String selectedReview = scanSelect2.nextLine();
                    if (selectedReview.equals("X")) {

                    } else {
                        try {
                            int selectedReviewInt = Integer.parseInt(selectedReview);
                            if (selectedReviewInt >= 0 && selectedReviewInt <= (allReview.size() - 1)) {
                                graph.deleteAllRelationLikeByDescription(allReview.get(selectedReviewInt).getDescription());
                                graph.deleteAllRelationRelatedByDescription(allReview.get(selectedReviewInt).getDescription());
                                graph.deleteAllRelationCreatedByDescription(allReview.get(selectedReviewInt).getDescription());
                                graph.deleteCommentByDescription(allReview.get(selectedReviewInt).getDescription());
                                mongo.deleteComment(allReview.get(selectedReviewInt).getDescription(),graph.findUserByDescription(allReview.get(selectedReviewInt).getDescription()).get(0).getUsername(),graph.findWineByDescription(allReview.get(selectedReviewInt).getDescription()).get(0).getWineName());
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }
                    }
                }
            }
        } else {
            System.out.println("You dont have friends");
        }


        //TRENDING
        System.out.println("Do you want see the trending post? y/n");
        Scanner scanSelectShow = new Scanner(System.in);
        String selectionShow = scanSelectShow.nextLine();
        if (selectionShow.equals("y")) {
            ArrayList<Review> trendingReviews = new ArrayList<>(adv_graph.showTrendingComment());
            if (trendingReviews.size() != 0) {
                int s = 0;
                System.out.println("=================Trending Comment=======================");
                for (s = 0; s < trendingReviews.size(); s++) {
                    System.out.println("Comment to select " + s + " :");
                    System.out.println(trendingReviews.get(s).getDescription());
                    System.out.println("rating = " + trendingReviews.get(s).getRating());
                    System.out.println("Like = " + graph.countLikeByDescription(trendingReviews.get(s).getDescription()));
                    System.out.println("made by:  = " + graph.findUserByDescription(trendingReviews.get(s).getDescription()).get(0).getUsername());
                    System.out.println("wine = " + graph.findWineByDescription(trendingReviews.get(s).getDescription()).get(0).getWineName());
                    if (graph.checkIfLikedByDescription(trendingReviews.get(s).getDescription(), myUsername) == 1) {
                        System.out.println("Like = V");
                    } else if (graph.checkIfLikedByDescription(trendingReviews.get(s).getDescription(), myUsername) == 0) {
                        System.out.println("Like = X");
                    }


                    if (s != (trendingReviews.size() - 1)) {
                        System.out.println("------------------------------------------------");
                    }
                }
                System.out.println("=======================================================");
                System.out.println("what do you want to do?");
                System.out.println("1 : Put like on a Post");
                System.out.println("2 : Delete Like on a Post");
                Scanner scanSelectlike = new Scanner(System.in);
                String selectedLike = scanSelectlike.nextLine();
                if (selectedLike.equals("1")) {
                    System.out.println("Select a comment to put like :");
                    Scanner scanSelect = new Scanner(System.in);
                    String selected = scanSelect.nextLine();
                    if (selected.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selected);
                            if (selectedInt >= 0 && selectedInt <= (trendingReviews.size() - 1)) {
                                graph.putLikeByDescription(trendingReviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                } else if (selectedLike.equals("2")) {
                    System.out.println("Select a comment to delete like :");
                    Scanner scanSelectDeleteLike = new Scanner(System.in);
                    String selectedDeleteLike = scanSelectDeleteLike.nextLine();
                    if (selectedDeleteLike.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selectedDeleteLike);
                            if (selectedInt >= 0 && selectedInt <= (trendingReviews.size() - 1)) {
                                graph.deleteLikeByDescription(trendingReviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                }
            } else {
                System.out.println("There aren't trending comment");
            }
        } else {

        }


    }


    //tested DONE
    public void showSuggestedUserAndFollow(String myUsername) {
        ArrayList<User> usersPost = new ArrayList<>(adv_graph.showSuggestedUserByFriends(myUsername));
        ArrayList<User> usersLike = new ArrayList<>(adv_graph.showSuggestedUserByLike(myUsername));
        ArrayList<User> users = new ArrayList<>();
        users.addAll(usersPost);
        users.addAll(usersLike);

        int i = 0;
        if (users.size() != 0) {
            System.out.println("==============All Suggested User By Me============= ");
            for (i = 0; i < users.size(); i++) {
                System.out.println(i + " : name: " + users.get(i).getUsername() + "   country: " + users.get(i).getCountry() + "  Followers: " + graph.countFollowersByUsername(users.get(i).getUsername()));
            }
            System.out.println("===================================================== ");
            System.out.println("Select a user to follow :");
            Scanner scanSelect = new Scanner(System.in);
            String selected = scanSelect.nextLine();
            if (selected.equals("X")) {

            } else {
                try {
                    int selectedInt = Integer.parseInt(selected);
                    if (selectedInt >= 0 && selectedInt <= (users.size() - 1)) {
                        graph.createRelationFollow(myUsername, users.get(selectedInt).getUsername());
                    } else {
                        System.out.println("Selection wrong");
                    }
                } catch (NumberFormatException nex) {
                    System.out.println("You have to insert a string not a number");
                }
            }
        } else {
            System.out.println("No suggested friends");
        }


    }


    //tested DONE -------> mettere count like con altra query
    public void showTrendingCommentWithLikeAndPuttingLike(String myUsername) {
        ArrayList<Review> reviews = new ArrayList<>(adv_graph.showTrendingComment());
        int i = 0;
        System.out.println("=================Trending Comment=======================");
        for (i = 0; i < reviews.size(); i++) {
            System.out.println("Comment to select " + i + " :");
            System.out.println(reviews.get(i).getDescription());
            System.out.println("rating = " + reviews.get(i).getRating());
            System.out.println("-------------------------------------------------------");
        }
        System.out.println("=======================================================");
        System.out.println("Select a comment to put like :");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            int selectedInt = Integer.parseInt(selected);
            graph.putLikeByDescription(reviews.get(selectedInt).getDescription(), myUsername);
        }
    }

    public void showMyAccount(String myUsername){
        User myUser = graph.showUserByUsername(myUsername);
        System.out.println("====================MY PROFILE==============");
        System.out.println("Name : " + myUser.getUsername());
        System.out.println("Country : " + myUser.getCountry());
        System.out.println("Email : " + myUser.getEmail());
        System.out.println("Twitter Tag : " + myUser.getTwitter_taster_handle());
        System.out.println("Followed Friends : " + graph.showFollowedUsers(myUsername).size());
        System.out.println("Followers : " + graph.countFollowersByUsername(myUsername));
        System.out.println("==============LIST OF MY FRIENDS============= ");
        ArrayList<User> users = new ArrayList<>(graph.showFollowedUsers(myUsername));
        if (users.size() != 0) {
            int i = 0;
            for (i = 0; i < users.size(); i++) {


                System.out.println(i + " : name = " + users.get(i).getUsername() + "   country = " + users.get(i).getCountry());
                if (i != (users.size() - 1)) {
                    System.out.println("--------------------------------------------------");
                }
            }

        } else {
            System.out.println("You dont have friends");
        }
        System.out.println("==============LIST OF MY COMMENTS============= ");
        ArrayList<Review> myReviews = new ArrayList<>(graph.showMyComment(myUsername));
        if (myReviews.size() != 0) {
            int x = 0;
            for (x = 0; x < myReviews.size(); x++) {
                System.out.println(x + " : Comment  ");
                System.out.println(myReviews.get(x).getDescription());
                System.out.println("rating = " + myReviews.get(x).getRating());
                System.out.println("like = " + graph.countLikeByDescription(myReviews.get(x).getDescription()));
                System.out.println("wine = " + graph.findWineByDescription(myReviews.get(x).getDescription()).get(0).getWineName());
                if (graph.checkIfLikedByDescription(myReviews.get(x).getDescription(), myUsername) == 1) {
                    System.out.println("Like = V");
                } else if (graph.checkIfLikedByDescription(myReviews.get(x).getDescription(), myUsername) == 0) {
                    System.out.println("Like = X");
                }

                if (x != (myReviews.size() - 1)) {
                    System.out.println("------------------------------------------------");
                }
            }
            System.out.println("==================================================" + "\n");
        } else {
            System.out.println("You dont have review");
            System.out.println("===============================================" + "\n");
        }

        System.out.println("What do you want do?");
        System.out.println("1.  Unfollow a friends");
        System.out.println("2.  Delete one review"); //-->mongo ok
        System.out.println("3.  Delete account"); //--->mongo ok
        System.out.println("4.  See profilo of a friends");
        Scanner scanSelection = new Scanner(System.in);
        String selection = scanSelection.nextLine();
        if (selection.equals("1")) {
            if (users.size() != 0) {
                System.out.println("Select a user to unfollow :");
                Scanner scanSelect = new Scanner(System.in);
                String selected = scanSelect.nextLine();
                if (selected.equals("X")) {

                } else {
                    try {
                        int selectedInt = Integer.parseInt(selected);
                        if (selectedInt >= 0 && selectedInt <= (users.size() - 1)) {
                            graph.deleteRelationFollow(myUsername, users.get(selectedInt).getUsername());
                        }
                    } catch (NumberFormatException nex) {
                        System.out.println("You have to insert a number not a string");
                    }
                }
            } else {
                System.out.println("You don't have friend");
            }
        } else if (selection.equals("2")) {
            if (myReviews.size() != 0) {
                System.out.println("Select Comment to delete :");
                Scanner scanSelect2 = new Scanner(System.in);
                String selectedReview = scanSelect2.nextLine();
                if (selectedReview.equals("X")) {

                } else {
                    try {
                        int selectedReviewInt = Integer.parseInt(selectedReview);
                        if (selectedReviewInt >= 0 && selectedReviewInt <= (myReviews.size() - 1)) {
                            graph.deleteAllRelationLikeByDescription(myReviews.get(selectedReviewInt).getDescription());
                            graph.deleteAllRelationRelatedByDescription(myReviews.get(selectedReviewInt).getDescription());
                            graph.deleteAllRelationCreatedByDescription(myReviews.get(selectedReviewInt).getDescription());
                            graph.deleteCommentByDescription(myReviews.get(selectedReviewInt).getDescription());
                            mongo.deleteComment(myReviews.get(selectedReviewInt).getDescription(),myUsername,graph.findWineByDescription(myReviews.get(selectedReviewInt).getDescription()).get(0).getWineName());
                        }
                    } catch (NumberFormatException nex) {
                        System.out.println("You have to insert a number not a string");
                    }

                }
            } else {
                System.out.println("You dont have comment");
            }
        } else if (selection.equals("3")) {
            graph.deleteAllRelationFollow(myUsername);
            //graph.deleteAllRelationFollowed(myUsername);
            graph.deleteAllRelationLike(myUsername);
            graph.deleteAllRelationCreated(myUsername);
            graph.deleteUserByUsername(myUsername);
            try {
                mongo.deleteAllCommentForGivenUser(myUsername);
            }catch (UserNotPresentException e){
                System.out.println(e.getMessage());
            }
        } else if (selection.equals("4")) {
            if (users.size() != 0) {
                System.out.println("Select a user to see profile :");
                Scanner scanSelectProfile = new Scanner(System.in);
                String selectedProfile = scanSelectProfile.nextLine();
                if (selectedProfile.equals("X")) {

                } else {
                    try {
                        int selectedIntProfile = Integer.parseInt(selectedProfile);
                        if (selectedIntProfile >= 0 && selectedIntProfile <= (users.size() - 1)) {
                            System.out.println("==============PROFILE OF YOUR FRIEND============= ");
                            System.out.println("Name : " + users.get(selectedIntProfile).getUsername());
                            System.out.println("Country : " + users.get(selectedIntProfile).getCountry());
                            System.out.println("Email : " + users.get(selectedIntProfile).getEmail());
                            System.out.println("Twitter Tag : " + users.get(selectedIntProfile).getTwitter_taster_handle());
                            System.out.println("Followed Friends : " + graph.showFollowedUsers(users.get(selectedIntProfile).getUsername()).size());
                            System.out.println("Followers : " + graph.countFollowersByUsername(users.get(selectedIntProfile).getUsername()));
                            System.out.println("==============LIST OF FRIENDS============= ");
                            ArrayList<User> usersFollowed = new ArrayList<>(graph.showFollowedUsers(users.get(selectedIntProfile).getUsername()));
                            if (usersFollowed.size() != 0) {
                                int z = 0;
                                for (z = 0; z < usersFollowed.size(); z++) {


                                    System.out.println(z + " : name = " + usersFollowed.get(z).getUsername() + "   country = " + usersFollowed.get(z).getCountry());
                                    if (z != (usersFollowed.size() - 1)) {
                                        System.out.println("--------------------------------------------------");
                                    }
                                }

                            } else {
                                System.out.println("He dont have friends");
                            }
                            System.out.println("==============LIST OF COMMENTS MADE============= ");
                            ArrayList<Review> friendReviews = new ArrayList<>(graph.showMyComment(users.get(selectedIntProfile).getUsername()));
                            if (friendReviews.size() != 0) {
                                int k = 0;
                                friendReviews.size();
                                for (k = 0; k < friendReviews.size(); k++) {
                                    System.out.println(k + " : Comment  ");
                                    System.out.println(friendReviews.get(k).getDescription());
                                    System.out.println("rating = " + friendReviews.get(k).getRating());
                                    System.out.println("like = " + graph.countLikeByDescription(friendReviews.get(k).getDescription()));
                                    System.out.println("wine = " + graph.findWineByDescription(friendReviews.get(k).getDescription()).get(0).getWineName());
                                    if (graph.checkIfLikedByDescription(friendReviews.get(k).getDescription(), myUsername) == 1) {
                                        System.out.println("Like = V");
                                    } else if (graph.checkIfLikedByDescription(friendReviews.get(k).getDescription(), myUsername) == 0) {
                                        System.out.println("Like = X");
                                    }

                                    if (k != (friendReviews.size() - 1)) {
                                        System.out.println("------------------------------------------------");
                                    }
                                }
                                System.out.println("==================================================" + "\n");
                            } else {
                                System.out.println("He dont have review");
                            }


                            System.out.println("Select operation");
                            System.out.println("1. put like on a post");
                            System.out.println("2. delete like on a post");
                            Scanner scanChoise = new Scanner(System.in);
                            String selectChoise = scanChoise.nextLine();
                            if (selectChoise.equals("1")) {
                                System.out.println("select a post: ");
                                Scanner scanSelectlike = new Scanner(System.in);
                                String selectedReviewLike = scanSelectlike.nextLine();
                                if (selectedReviewLike.equals("X")) {

                                } else {
                                    try {
                                        int selectedReviewInt = Integer.parseInt(selectedReviewLike);
                                        if (selectedReviewInt >= 0 && selectedReviewInt <= (friendReviews.size() - 1)) {
                                            graph.putLikeByDescription(friendReviews.get(selectedReviewInt).getDescription(), myUsername);
                                        } else {
                                            System.out.println("selection wrong");
                                        }
                                    } catch (NumberFormatException nex) {
                                        System.out.println("You have to insert a number not a string");
                                    }

                                }
                            } else if (selectChoise.equals("2")) {
                                System.out.println("select a post: ");
                                Scanner scanSelectlike = new Scanner(System.in);
                                String selectedReviewLike = scanSelectlike.nextLine();
                                if (selectedReviewLike.equals("X")) {

                                } else {
                                    try {
                                        int selectedReviewInt = Integer.parseInt(selectedReviewLike);
                                        if (selectedReviewInt >= 0 && selectedReviewInt <= (friendReviews.size() - 1)) {
                                            graph.deleteLikeByDescription(friendReviews.get(selectedReviewInt).getDescription(), myUsername);
                                        } else {
                                            System.out.println("Selection wrong");
                                        }
                                    } catch (NumberFormatException nex) {
                                        System.out.println("You have to insert a number not a string");
                                    }

                                }
                            }


                        } else {
                            System.out.println("Selection wrong");
                        }

                    } catch (NumberFormatException nex) {
                        System.out.println("You have to insert a number not a string");
                    }
                }
            } else {
                System.out.println("You dont have friends");
            }
        }

    }

    public void searchUserfromUser(String myUsername) {
        System.out.println("Select an option");
        System.out.println("1. Search a user");
        System.out.println("2. See 10 random people that don't follow");
        System.out.println("3. See 10 suggested friends");
        Scanner scanselectOption = new Scanner(System.in);
        String selectOption = scanselectOption.nextLine();

        if (selectOption.equals("1")) {
            System.out.println("Digit the name: ");
            Scanner scanName = new Scanner(System.in);
            String nameToSearch = scanName.nextLine();
            User user = graph.showUserByUsername(nameToSearch);
            if (user != null) {
                System.out.println("name: " + user.getUsername() + "\nemail: " + user.getEmail() + "\ncountry" + user.getCountry() + "\nFollowers:" + graph.countFollowersByUsername(user.getUsername()));
                System.out.println("Select operation: ");
                System.out.println("1. Follow");
                System.out.println("2. See the profile");
                Scanner scanSelect = new Scanner(System.in);
                String selection = scanSelect.nextLine();
                if (selection.equals("1")) {
                    graph.createRelationFollow(myUsername, user.getUsername());
                } else if (selection.equals("2")) {
                    System.out.println("==============PROFILE============= ");
                    System.out.println("Name : " + user.getUsername());
                    System.out.println("Country : " + user.getCountry());
                    System.out.println("Email : " + user.getEmail());
                    System.out.println("Twitter Tag : " + user.getTwitter_taster_handle());
                    System.out.println("Followed Friends : " + graph.showFollowedUsers(user.getUsername()).size());
                    System.out.println("Followers : " + graph.countFollowersByUsername(user.getUsername()));
                    System.out.println("==============LIST OF MY FRIENDS============= ");
                    ArrayList<User> usersFollowed = new ArrayList<>(graph.showFollowedUsers(user.getUsername()));
                    if (usersFollowed.size() != 0) {
                        int z = 0;
                        for (z = 0; z < usersFollowed.size(); z++) {


                            System.out.println(z + " : name = " + usersFollowed.get(z).getUsername() + "   country = " + usersFollowed.get(z).getCountry());
                            if (z != (usersFollowed.size() - 1)) {
                                System.out.println("--------------------------------------------------");
                            }
                        }

                    } else {
                        System.out.println("he dont have friends");
                    }
                    System.out.println("==============LIST OF COMMENTS MADE============= ");
                    ArrayList<Review> friendsReviews = new ArrayList<>(graph.showMyComment(user.getUsername()));
                    if (friendsReviews.size() != 0) {
                        int k = 0;
                        for (k = 0; k < friendsReviews.size(); k++) {
                            System.out.println(k + " : Comment  ");
                            System.out.println(friendsReviews.get(k).getDescription());
                            System.out.println("rating = " + friendsReviews.get(k).getRating());
                            System.out.println("like = " + graph.countLikeByDescription(friendsReviews.get(k).getDescription()));
                            System.out.println("wine = " + graph.findWineByDescription(friendsReviews.get(k).getDescription()).get(0).getWineName());
                            if (graph.checkIfLikedByDescription(friendsReviews.get(k).getDescription(), myUsername) == 1) {
                                System.out.println("Like = V");
                            } else if (graph.checkIfLikedByDescription(friendsReviews.get(k).getDescription(), myUsername) == 0) {
                                System.out.println("Like = X");
                            }

                            if (k != (friendsReviews.size() - 1)) {
                                System.out.println("------------------------------------------------");
                            }
                        }
                        System.out.println("==================================================" + "\n");
                    } else {
                        System.out.println("He dont have review");
                    }
                    System.out.println("Do you want to Follow? y/n");
                    Scanner scanSelectFollow = new Scanner(System.in);
                    String selectionFollow = scanSelectFollow.nextLine();
                    if (selectionFollow.equals("y")) {
                        graph.createRelationFollow(myUsername, user.getUsername());
                    }

                }
            } else {
                System.out.println("User dont found");
            }
        } else if (selectOption.equals("2")) {
            ArrayList<User> randomUsers = new ArrayList<>(graph.show10RandomUsers(myUsername));
            int i = 0;
            if (randomUsers.size() != 0) {
                System.out.println("==============Random User for you============= ");
                for (i = 0; i < randomUsers.size(); i++) {
                    System.out.println(i + " : name: " + randomUsers.get(i).getUsername() + "   country: " + randomUsers.get(i).getCountry() + "  Followers: " + graph.countFollowersByUsername(randomUsers.get(i).getUsername()));
                }
                System.out.println("===================================================== ");
                System.out.println("Select a user to follow :");
                Scanner scanSelect = new Scanner(System.in);
                String selected = scanSelect.nextLine();
                if (selected.equals("X")) {

                } else {
                    try {
                        int selectedInt = Integer.parseInt(selected);
                        if (selectedInt >= 0 && selectedInt <= (randomUsers.size() - 1)) {
                            graph.createRelationFollow(myUsername, randomUsers.get(selectedInt).getUsername());
                        } else {
                            System.out.println("Selection wrong");
                        }
                    } catch (NumberFormatException nex) {
                        System.out.println("You have to insert a number not a string");
                    }

                }
            } else {
                System.out.println("No random users");
            }

        } else if (selectOption.equals("3")) {
            showSuggestedUserAndFollow(myUsername);
        }


    }

    public void searchUserfromAdmin(String myUsername) {
        System.out.println("Digit the name: ");
        Scanner scanName = new Scanner(System.in);
        String nameToSearch = scanName.nextLine();
        User user = graph.showUserByUsername(nameToSearch);
        if (user != null) {
            System.out.println("name: " + user.getUsername() + "\nemail: " + user.getEmail() + "\ncountry" + user.getCountry() + "\nFollowers:" + graph.countFollowersByUsername(user.getUsername()));
            System.out.println("Select operation: ");
            System.out.println("1. Follow");
            System.out.println("2. See the profile");//--> mongo ok
            System.out.println("3. Ban user"); //-->mongo ok
            Scanner scanSelect = new Scanner(System.in);
            String selection = scanSelect.nextLine();
            if (selection.equals("1")) {
                graph.createRelationFollow(myUsername, user.getUsername());
            } else if (selection.equals("2")) {
                System.out.println("==============PROFILE============= ");
                System.out.println("Name : " + user.getUsername());
                System.out.println("Country : " + user.getCountry());
                System.out.println("Email : " + user.getEmail());
                System.out.println("Twitter Tag : " + user.getTwitter_taster_handle());
                System.out.println("Followed Friends : " + graph.showFollowedUsers(user.getUsername()).size());
                System.out.println("Followers : " + graph.countFollowersByUsername(user.getUsername()));
                System.out.println("==============LIST OF MY FRIENDS============= ");
                ArrayList<User> usersFollowed = new ArrayList<>(graph.showFollowedUsers(user.getUsername()));
                if (usersFollowed.size() != 0) {
                    int z = 0;
                    for (z = 0; z < usersFollowed.size(); z++) {


                        System.out.println(z + " : name = " + usersFollowed.get(z).getUsername() + "   country = " + usersFollowed.get(z).getCountry());
                        if (z != (usersFollowed.size() - 1)) {
                            System.out.println("--------------------------------------------------");
                        }
                    }

                } else {
                    System.out.println("he dont have friends");
                }
                System.out.println("==============LIST OF COMMENTS MADE============= ");
                ArrayList<Review> friendsReviews = new ArrayList<>(graph.showMyComment(user.getUsername()));
                if (friendsReviews.size() != 0) {
                    int k = 0;
                    for (k = 0; k < friendsReviews.size(); k++) {
                        System.out.println(k + " : Comment  ");
                        System.out.println(friendsReviews.get(k).getDescription());
                        System.out.println("rating = " + friendsReviews.get(k).getRating());
                        System.out.println("like = " + graph.countLikeByDescription(friendsReviews.get(k).getDescription()));
                        System.out.println("wine = " + graph.findWineByDescription(friendsReviews.get(k).getDescription()).get(0).getWineName());
                        if (graph.checkIfLikedByDescription(friendsReviews.get(k).getDescription(), myUsername) == 1) {
                            System.out.println("Like = V");
                        } else if (graph.checkIfLikedByDescription(friendsReviews.get(k).getDescription(), myUsername) == 0) {
                            System.out.println("Like = X");
                        }

                        if (k != (friendsReviews.size() - 1)) {
                            System.out.println("------------------------------------------------");
                        }
                    }
                    System.out.println("==================================================" + "\n");
                } else {
                    System.out.println("He dont have review");
                }
                System.out.println("What do you want do?");
                System.out.println("1.  Delete one review"); //-->mongo ok
                System.out.println("2.  Delete account"); //--> mongo ok
                Scanner scanSelectionOption = new Scanner(System.in);
                String selectionOption = scanSelectionOption.nextLine();
                if (selectionOption.equals("1")) {
                    System.out.println("Select Comment to delete :");
                    Scanner scanSelect2Option = new Scanner(System.in);
                    String selectedReviewOption = scanSelect2Option.nextLine();
                    if (selectedReviewOption.equals("X")) {

                    } else {
                        try {
                            int selectedReviewIntOption = Integer.parseInt(selectedReviewOption);
                            if(selectedReviewIntOption>=0 && selectedReviewIntOption<=(friendsReviews.size()-1)){
                                graph.deleteAllRelationLikeByDescription(friendsReviews.get(selectedReviewIntOption).getDescription());
                                graph.deleteAllRelationRelatedByDescription(friendsReviews.get(selectedReviewIntOption).getDescription());
                                graph.deleteAllRelationCreatedByDescription(friendsReviews.get(selectedReviewIntOption).getDescription());
                                graph.deleteCommentByDescription(friendsReviews.get(selectedReviewIntOption).getDescription());
                                mongo.deleteComment(friendsReviews.get(selectedReviewIntOption).getDescription(),graph.findUserByDescription(friendsReviews.get(selectedReviewIntOption).getDescription()).get(0).getUsername(),graph.findWineByDescription(friendsReviews.get(selectedReviewIntOption).getDescription()).get(0).getWineName());
                            }else{
                                System.out.println("selection wrong");
                            }

                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }

                } else if (selectionOption.equals("2")) {
                    graph.deleteAllRelationFollow(user.getUsername());
                    //graph.deleteAllRelationFollowed(myUsername);
                    graph.deleteAllRelationLike(user.getUsername());
                    graph.deleteAllRelationCreated(user.getUsername());
                    graph.deleteUserByUsername(user.getUsername());
                    try{mongo.deleteAllCommentForGivenUser(user.getUsername());
                    }catch (UserNotPresentException e){
                        System.out.println(e.getMessage());
                    }
                }

            } else if (selection.equals("3")) {
                graph.deleteAllRelationFollow(user.getUsername());
                //graph.deleteAllRelationFollowed(myUsername);
                graph.deleteAllRelationLike(user.getUsername());
                graph.deleteAllRelationCreated(user.getUsername());
                graph.deleteUserByUsername(user.getUsername());
                try{mongo.deleteAllCommentForGivenUser(user.getUsername());
                }catch (UserNotPresentException e){
                    System.out.println(e.getMessage());
                }

            }
        } else {
            System.out.println("User dont found");
        }


    }

    public void show10Wine(ArrayList<Wine> wines, int times, int perTimes) {
        int i = times * perTimes;
        if (wines.size() == 0) {
            System.out.println("No wine on the social");
        }
        for (i = times * perTimes; (i < wines.size() && i < ((perTimes * times) + perTimes)); i++) {
            System.out.println("Wine to select " + i + " :");
            //calculateSpaceMenuAndPrint("Wine to select " + i + " :");
            System.out.println("wine name = " + wines.get(i).getWineName());
            //calculateSpaceMenuAndPrint("wine name = " + wines.get(i).getWineName());
            System.out.println("designation = " + wines.get(i).getDesignation());
            //calculateSpaceMenuAndPrint("designation = " + wines.get(i).getDesignation());
            System.out.println("price = " + wines.get(i).getPrice());
            //calculateSpaceMenuAndPrint("price = " + wines.get(i).getPrice());
            System.out.println("province = " + wines.get(i).getProvince());
            //calculateSpaceMenuAndPrint("province = " + wines.get(i).getProvince());

            System.out.println("variety = " + wines.get(i).getVariety());
            //calculateSpaceMenuAndPrint("variety = " + wines.get(i).getVariety());

            System.out.println("winery = " + wines.get(i).getWinery());
            //calculateSpaceMenuAndPrint("winery = " + wines.get(i).getWinery());

            if (i != (wines.size() - 1)) {
                System.out.println("------------------------------------------------");
            }
        }
    }

    public void writeCommentonWine(ArrayList<Wine> wines, String myUsername) {
        System.out.println("Select wine to write comment:");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            try {
                int selectedInt = Integer.parseInt(selected);
                if (selectedInt >= 0 && selectedInt <= (wines.size() - 1)) {
                    if (graph.checkIfCommentedWine(wines.get(selectedInt).getWineName(), myUsername) == 0) {
                        int correctDescr = 0;
                        while (correctDescr == 0) {
                            System.out.println("Insert the comment");
                            Scanner scanComment = new Scanner(System.in);
                            String description = scanComment.nextLine();
                            if (description.length() <= 140) {
                                correctDescr = 1;
                                System.out.println("Insert the rating");
                                Scanner scanRating = new Scanner(System.in);
                                String rating = scanRating.nextLine();
                                int len = rating.length();
                                if (onlyDigits(rating, len) == false || len > 2) {
                                    try {
                                        throw new WrongInsertionException("Rating cannot be a string and cannot be over 99. Insert an integer");
                                    } catch (WrongInsertionException wex) {
                                        System.out.println(wex.getMessage());
                                    }
                                } else {
                                    graph.addComment(description, rating);
                                    graph.createRelationCreated(description, myUsername);
                                    graph.createRelationRelated(wines.get(selectedInt).getWineName(), description);
                                    User myUserBeans = null;
                                    myUserBeans = graph.showUserByUsername(myUsername);
                                    mongo.addComment(wines.get(selectedInt).getWineName(),myUsername,Integer.parseInt(rating),description, myUserBeans.getTwitter_taster_handle(),myUserBeans.getCountry(),myUserBeans.getEmail());
                                }
                            } else {
                                correctDescr = 0;
                                System.out.println("Comment too long, please delete " + (description.length() - 140));
                            }

                        }

                    } else {
                        System.out.println("You have already commented this wine");
                    }
                } else {
                    System.out.println("Selection wrong");
                }
            } catch (NumberFormatException ne) {
                System.out.println("Insert a number not a string");
            } catch (WineNotExistsException e) {
                System.out.println(e.getMessage());
            } catch (ReviewAlreadyInserted reviewAlreadyInserted) {
                System.out.println(reviewAlreadyInserted.getMessage());
            }
        }

    }

    public void showCommentonWine(ArrayList<Wine> wines, String myUsername) {
        System.out.println("Select wine: ");
        Scanner scanSelectionWine = new Scanner(System.in);
        String selectionWine = scanSelectionWine.nextLine();
        try {
            int convertedSelection = Integer.parseInt(selectionWine);
            if (convertedSelection > (wines.size() - 1) || convertedSelection < 0) {
                System.out.println("Selected last wine");
                convertedSelection = (wines.size() - 1);
            }
            ArrayList<Review> reviews = new ArrayList<>(graph.showAllCommentRelatedWineName(wines.get(convertedSelection).getWineName()));
            if (reviews.size() != 0) {
                int j = 0;
                System.out.println("=================Comment of " + wines.get(convertedSelection).getWineName() + "=======================");
                for (j = 0; j < reviews.size(); j++) {
                    System.out.println(j + " : Comment  ");
                    System.out.println(reviews.get(j).getDescription());
                    System.out.println("rating = " + reviews.get(j).getRating());
                    System.out.println("like = " + graph.countLikeByDescription(reviews.get(j).getDescription()));
                    System.out.println("made by:  = " + graph.findUserByDescription(reviews.get(j).getDescription()).get(0).getUsername());
                    if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 1) {
                        System.out.println("Like = V");
                    } else if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 0) {
                        System.out.println("Like = X");
                    }

                    if (j != (reviews.size() - 1)) {
                        System.out.println("------------------------------------------------");
                    }
                }
                System.out.println("=======================================================");
                System.out.println("what do you want to do?");
                System.out.println("1 : Put like on a Post");
                System.out.println("2 : Delete Like on a Post");
                Scanner scanSelectlike = new Scanner(System.in);
                String selectedLike = scanSelectlike.nextLine();
                if (selectedLike.equals("1")) {
                    System.out.println("Select a comment to put like :");
                    Scanner scanSelect = new Scanner(System.in);
                    String selected = scanSelect.nextLine();
                    if (selected.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selected);
                            if (selectedInt >= 0 && selectedInt <= (reviews.size() - 1)) {
                                graph.putLikeByDescription(reviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("i cant put like on this");
                            }

                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }
                    }
                } else if (selectedLike.equals("2")) {
                    System.out.println("Select a comment to delete like :");
                    Scanner scanSelectDeleteLike = new Scanner(System.in);
                    String selectedDeleteLike = scanSelectDeleteLike.nextLine();
                    if (selectedDeleteLike.equals("X")) {

                    } else {
                        try {
                            int selectedInt = Integer.parseInt(selectedDeleteLike);
                            if (selectedInt >= 0 && selectedInt <= (reviews.size() - 1)) {
                                graph.deleteLikeByDescription(reviews.get(selectedInt).getDescription(), myUsername);
                            } else {
                                System.out.println("Selection wrong");
                            }
                        } catch (NumberFormatException nex) {
                            System.out.println("You have to insert a number not a string");
                        }

                    }
                }
            } else {
                System.out.println("No comment for this review. Do you add a comment? y/n"); //---> mongo ok
                Scanner scanSelect = new Scanner(System.in);
                String selectionAdd = scanSelect.nextLine();
                if (selectionAdd.equals("y")) {
                    int correctDescr = 0;
                    while (correctDescr == 0) {
                        System.out.println("Insert the comment");
                        Scanner scanComment = new Scanner(System.in);
                        String description = scanComment.nextLine();
                        if (description.length() <= 140) {
                            correctDescr = 1;
                            System.out.println("Insert the rating");
                            Scanner scanRating = new Scanner(System.in);
                            String rating = scanRating.nextLine();
                            int len = rating.length();
                            if (onlyDigits(rating, len) == false || len > 2) {
                                try {
                                    throw new WrongInsertionException("Rating cannot be a string and cannot be over 99. Insert an integer");
                                } catch (WrongInsertionException wex) {
                                    System.out.println(wex.getMessage());
                                }
                            } else {

                                graph.addComment(description, rating);
                                graph.createRelationCreated(description, myUsername);
                                graph.createRelationRelated(wines.get(convertedSelection).getWineName(), description);
                                User myUserBeans = null;
                                myUserBeans = graph.showUserByUsername(myUsername);
                                mongo.addComment(wines.get(convertedSelection).getWineName(),myUsername,Integer.parseInt(rating),description, myUserBeans.getTwitter_taster_handle(),myUserBeans.getCountry(),myUserBeans.getEmail());
                            }
                        } else {
                            correctDescr = 0;
                            System.out.println("Comment too long, please delete " + (description.length() - 140));
                        }

                    }
                } else {

                }
            }

        } catch (NumberFormatException nex) {
            System.out.println("You have to insert the number not the string");
        } catch (WineNotExistsException e) {
            System.out.println(e.getMessage());
        } catch (ReviewAlreadyInserted reviewAlreadyInserted) {
            System.out.println(reviewAlreadyInserted.getMessage());
        }
    }

    public void calculateSpaceMenuAndPrint (String line ){
        int i =1;
        for(i=1;i<(65 - line.length());i++){
            System.out.print(" ");
        }
        System.out.println("║");
    }

    public void showAllWineMenu(String myUsername) {
        ArrayList<Wine> wines = new ArrayList<>(graph.showAllWine());
        int times = 0;
        int perTimes = 10;
        if (wines.size() != 0) {
            System.out.println("==============List of All Wine on Social============= ");
            show10Wine(wines, times, perTimes);
            times++;
            String selectionMore = "y";
            while (selectionMore.equals("y")) {
                if (perTimes * times < (wines.size() - 1)) {
                    System.out.println("\nDo you want to see 10 more wine? y/n");
                    Scanner scanSelectionMore = new Scanner(System.in);
                    selectionMore = scanSelectionMore.nextLine();
                    if (selectionMore.equals("y")) {
                        times++;
                        show10Wine(wines, times, perTimes);
                    } else if (selectionMore.equals("n")) {
                        System.out.println("===================================================== ");
                        System.out.println("\nWhat do you want to do?");
                        System.out.println("1" + " Write Comment on specific wine");
                        System.out.println("2" + " See comment of specific wine");
                        Scanner scanSelection = new Scanner(System.in);
                        String selection = scanSelection.nextLine();
                        if (selection.equals("1")) {
                            writeCommentonWine(wines, myUsername); //-->mongo ok
                        } else if (selection.equals("2")) {
                            showCommentonWine(wines, myUsername); //---> mongo ok
                        }
                    }
                } else {
                    System.out.println("===================================================== ");
                    selectionMore = "n";
                    System.out.println("\nWhat do you want to do?");
                    System.out.println("1" + " Write Comment on specific wine"); //---> mongo ok
                    System.out.println("2" + " See comment of specific wine"); //---> mongo ok
                    Scanner scanSelection = new Scanner(System.in);
                    String selection = scanSelection.nextLine();
                    if (selection.equals("1")) {
                        writeCommentonWine(wines, myUsername);
                    } else if (selection.equals("2")) {
                        showCommentonWine(wines, myUsername);
                    }
                }

            }

        } else {
            System.out.println("No wine on the social");
        }
    }

    public void addWineAdmin() throws WrongInsertionException {
        System.out.println("Type wine name :");
        Scanner scanName = new Scanner(System.in);
        String name = scanName.nextLine();
        System.out.println("Type wine designation :");
        Scanner scanDesignation = new Scanner(System.in);
        String designation = scanDesignation.nextLine();
        System.out.println("Type wine price :");
        Scanner scanPrice = new Scanner(System.in);
        String price = scanPrice.nextLine();
        int len = price.length();
        if (onlyDigits(price,len)== false)
                throw new WrongInsertionException("Price can contains only digits not string");
        System.out.println("Type wine province :");
        Scanner scanProvince = new Scanner(System.in);
        String province = scanProvince.nextLine();
        System.out.println("Type wine variety :");
        Scanner scanVariety = new Scanner(System.in);
        String variety = scanVariety.nextLine();
        System.out.println("Type wine country :");
        Scanner scanCountry = new Scanner(System.in);
        String country = scanCountry.nextLine();
        System.out.println("Type wine winery :");
        Scanner scanWinery = new Scanner(System.in);
        String winery = scanWinery.nextLine();
        graph.addWine(name, designation, price, province, variety, winery);
        mongo.addWine(name,variety,country,province,designation,winery,Integer.parseInt(price));
    }

    public void wineToDelete(ArrayList<Wine> wines) {
        System.out.println("Select wine to delete :");
        Scanner selectionWine = new Scanner(System.in);
        String selectioned = selectionWine.nextLine();
        if (selectioned.equals("X")) {

        } else {
            try {
                int convertedSelectionWine = Integer.parseInt(selectioned);
                if (convertedSelectionWine >= 0 && convertedSelectionWine <= (wines.size() - 1)) {
                    //wines.get(convertedSelection).getWineName()
                    ArrayList<Review> reviewToDelete = new ArrayList<>(graph.showAllCommentRelatedWineName(wines.get(convertedSelectionWine).getWineName()));
                    for (int j = 0; j < reviewToDelete.size(); j++) {
                        //delete comment related to the wine
                        graph.deleteAllRelationLikeByDescription(reviewToDelete.get(j).getDescription());
                        graph.deleteAllRelationRelatedByDescription(reviewToDelete.get(j).getDescription());
                        graph.deleteAllRelationCreatedByDescription(reviewToDelete.get(j).getDescription());
                        graph.deleteCommentByDescription(reviewToDelete.get(j).getDescription());
                        mongo.deleteComment(reviewToDelete.get(j).getDescription(),graph.findUserByDescription(reviewToDelete.get(j).getDescription()).get(0).getUsername(),wines.get(convertedSelectionWine).getWineName());
                    }
                    //delete relation of wine selected
                    graph.deleteAllRelatedBynameWine(wines.get(convertedSelectionWine).getWineName());
                    //delete wine selected
                    graph.deleteWineByName(wines.get(convertedSelectionWine).getWineName());
                    mongo.deleteWine(wines.get(convertedSelectionWine).getWineName());

                } else {
                    System.out.println("selection wrong");
                }
            } catch (NumberFormatException nex) {
                System.out.println("You have to insert a number not a string");
            }
        }
    }

    public void showAllWineMenuAdmin(String myUsername) {
        ArrayList<Wine> wines = new ArrayList<>(graph.showAllWine());
        int i = 0;
        int times = 0;
        int perTimes = 10;
        if (wines.size() != 0) {
            System.out.println("==============List of All Wine on Social============= ");
            show10Wine(wines, times, perTimes);
            times++;
            String selectionMore = "y";
            while (selectionMore.equals("y")) {
                if (perTimes * times < (wines.size() - 1)) {
                    System.out.println("\nDo you want to see 10 more wine? y/n");
                    Scanner scanSelectionMore = new Scanner(System.in);
                    selectionMore = scanSelectionMore.nextLine();
                    if (selectionMore.equals("y")) {
                        times++;
                        show10Wine(wines, times, perTimes);
                    } else if (selectionMore.equals("n")) {
                        System.out.println("===================================================== ");
                        System.out.println("\nWhat do you want to do?");
                        System.out.println("1" + " Write Comment on specific wine"); //--->mongo ok
                        System.out.println("2" + " See comment of specific wine"); // --->mongo ok
                        System.out.println("3" + " Delete specific wine"); //-->mongo ok
                        System.out.println("4" + " Add specific wine"); //-->mongo ok
                        Scanner scanSelection = new Scanner(System.in);
                        String selection = scanSelection.nextLine();
                        if (selection.equals("1")) {
                            writeCommentonWine(wines, myUsername);
                        } else if (selection.equals("2")) {
                            showCommentonWine(wines, myUsername);
                        } else if (selection.equals("3")) {
                            wineToDelete(wines);
                        } else if (selection.equals("4")) {
                            try {
                                addWineAdmin();
                            } catch (WrongInsertionException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                } else {
                    System.out.println("===================================================== ");
                    selectionMore = "n";
                    System.out.println("\nWhat do you want to do?");
                    System.out.println("1" + " Write Comment on specific wine"); //-->mongo ok
                    System.out.println("2" + " See comment of specific wine"); //--->mongo ok
                    System.out.println("3" + " Delete specific wine");//--->mongo ok
                    System.out.println("4" + " Add specific wine"); //-->mongo ok
                    Scanner scanSelection = new Scanner(System.in);
                    String selection = scanSelection.nextLine();
                    if (selection.equals("1")) {
                        writeCommentonWine(wines, myUsername);
                    } else if (selection.equals("2")) {
                        showCommentonWine(wines, myUsername);
                    } else if (selection.equals("3")) {
                        wineToDelete(wines);
                    } else if (selection.equals("4")) {
                        try {
                            addWineAdmin();
                        } catch (WrongInsertionException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        } else {
            System.out.println("No wine on the social");
        }

    }


    public void deleteComment(ArrayList<Review> reviews) {
        System.out.println("Select Comment to delete :");
        Scanner scanSelect2 = new Scanner(System.in);
        String selectedReview = scanSelect2.nextLine();
        if (selectedReview.equals("X")) {

        } else {
            try{
                int selectedReviewInt = Integer.parseInt(selectedReview);
                if (selectedReviewInt >= 0 && selectedReviewInt <= (reviews.size() - 1)) {
                    graph.deleteAllRelationLikeByDescription(reviews.get(selectedReviewInt).getDescription());
                    graph.deleteAllRelationRelatedByDescription(reviews.get(selectedReviewInt).getDescription());
                    graph.deleteAllRelationCreatedByDescription(reviews.get(selectedReviewInt).getDescription());
                    graph.deleteCommentByDescription(reviews.get(selectedReviewInt).getDescription());
                    mongo.deleteComment(reviews.get(selectedReviewInt).getDescription(),graph.findUserByDescription(reviews.get(selectedReviewInt).getDescription()).get(0).getUsername(),graph.findWineByDescription(reviews.get(selectedReviewInt).getDescription()).get(0).getWineName());
                } else {
                    System.out.println("Selection wrong");
                }
            } catch (NumberFormatException nex){
                System.out.println("You have to insert a number not a string");
            }

        }
    }

    public void deleteAccount(String username) {
        try {
            graph.deleteAllRelationFollow(username);
        } catch (IndexOutOfBoundsException ecc) {

        }
        try {
            graph.deleteAllRelationLike(username);
        } catch (IndexOutOfBoundsException ecc) {

        }
        try {
            graph.deleteAllRelationCreated(username);
        } catch (IndexOutOfBoundsException ecc) {

        }
        //graph.deleteAllRelationFollowed(myUsername);
        graph.deleteUserByUsername(username);
        try{
            mongo.deleteAllCommentForGivenUser(username);
        }catch (UserNotPresentException e){
            System.out.println(e.getMessage());
        }
    }

    public void show10User(ArrayList<User> users, int times, int perTimes) {
        int i = times * perTimes;
        if (users.size() == 0) {
            System.out.println("No wine on the social");
        }
        for (i = times * perTimes; (i < users.size() && i < ((perTimes * times) + perTimes)); i++) {
            System.out.println(i + ": name : " + users.get(i).getUsername() + "  countr: " + users.get(i).getCountry() + "  followers: " + graph.countFollowersByUsername(users.get(i).getUsername()));
        }
    }


    public void usersMenuBanAdmin(String myUsername) {
        System.out.println("What do you want do?");
        System.out.println("1.  Search Specific User and Ban"); //--> mongo ok
        System.out.println("2.  Broswe all user and Ban");
        Scanner scanSelectionBanMenu = new Scanner(System.in);
        String selectionBanMenu = scanSelectionBanMenu.nextLine();
        if(selectionBanMenu.equals("1")){
            searchUserfromAdmin(myUsername);
        }else if(selectionBanMenu.equals("2")){
            ArrayList<User> users = new ArrayList<>(graph.showAllUser());
            int times = 0;
            int perTimes = 10;
            if (users.size() != 0) {
                System.out.println("=============BROSWE USER===============");
                show10User(users, times, perTimes);
                times++;
                String selectionMore = "y";
                while (selectionMore.equals("y")) {
                    if (perTimes * times < (users.size() - 1)) {
                        System.out.println("\nDo you want to see 10 more users? y/n");
                        Scanner scanSelectionMore = new Scanner(System.in);
                        selectionMore = scanSelectionMore.nextLine();
                        if (selectionMore.equals("y")) {
                            times++;
                            show10User(users, times, perTimes);
                        } else if (selectionMore.equals("n")) {
                            //start see profile menu
                            System.out.println("See profile of user :");
                            Scanner selectionuser = new Scanner(System.in);
                            String selectioned = selectionuser.nextLine();
                            if (selectioned.equals("X")) {

                            } else {
                                try{
                                    int convertedSelection = Integer.parseInt(selectioned);
                                    if (convertedSelection >= 0 && convertedSelection <= (users.size() - 1)) {
                                        System.out.println("==============PROFILE============= ");
                                        System.out.println("Name : " + users.get(convertedSelection).getUsername());
                                        System.out.println("Country : " + users.get(convertedSelection).getCountry());
                                        System.out.println("Email : " + users.get(convertedSelection).getEmail());
                                        System.out.println("Twitter Tag : " + users.get(convertedSelection).getTwitter_taster_handle());
                                        System.out.println("Followed Friends : " + graph.showFollowedUsers(users.get(convertedSelection).getUsername()).size());
                                        System.out.println("Followers : " + graph.countFollowersByUsername(users.get(convertedSelection).getUsername()));
                                        System.out.println("==============LIST OF MY FRIENDS============= ");
                                        ArrayList<User> usersFollowed = new ArrayList<>(graph.showFollowedUsers(users.get(convertedSelection).getUsername()));
                                        if (usersFollowed.size() != 0) {
                                            int z = 0;
                                            for (z = 0; z < usersFollowed.size(); z++) {


                                                System.out.println(z + " : name = " + usersFollowed.get(z).getUsername() + "   country = " + usersFollowed.get(z).getCountry());
                                                if (z != (usersFollowed.size() - 1)) {
                                                    System.out.println("--------------------------------------------------");
                                                }
                                            }

                                        } else {
                                            System.out.println("You dont have friends");
                                        }
                                        System.out.println("==============LIST OF COMMENTS MADE============= ");
                                        ArrayList<Review> myReviews = new ArrayList<>(graph.showMyComment(users.get(convertedSelection).getUsername()));
                                        if (myReviews.size() != 0) {
                                            int k = 0;
                                            for (k = 0; k < myReviews.size(); k++) {
                                                System.out.println(k + " : Comment  ");
                                                System.out.println(myReviews.get(k).getDescription());
                                                System.out.println("rating = " + myReviews.get(k).getRating());
                                                System.out.println("like = " + graph.countLikeByDescription(myReviews.get(k).getDescription()));
                                                System.out.println("wine = " + graph.findWineByDescription(myReviews.get(k).getDescription()).get(0).getWineName());
                                                if (graph.checkIfLikedByDescription(myReviews.get(k).getDescription(), myUsername) == 1) {
                                                    System.out.println("Like = V");
                                                } else if (graph.checkIfLikedByDescription(myReviews.get(k).getDescription(), myUsername) == 0) {
                                                    System.out.println("Like = X");
                                                }

                                                if (k != (myReviews.size() - 1)) {
                                                    System.out.println("------------------------------------------------");
                                                }
                                            }
                                            System.out.println("==================================================" + "\n");
                                        } else {
                                            System.out.println("He dont have review");
                                        }

                                        System.out.println("What do you want do?");
                                        System.out.println("1.  Delete one review"); //-->mongo ok
                                        System.out.println("2.  Delete account"); //-->mongo ok
                                        Scanner scanSelection = new Scanner(System.in);
                                        String selection = scanSelection.nextLine();
                                        if (selection.equals("1")) {
                                            deleteComment(myReviews);
                                        } else if (selection.equals("2")) {
                                            deleteAccount(users.get(convertedSelection).getUsername());
                                        }

                                    } else {
                                        System.out.println("Selection wrong");
                                    }
                                }catch (NumberFormatException nex){
                                    System.out.println("You have to insert a number not a string");
                                }

                            }
                            //end see profile menu
                        }
                    } else {
                        //start see profile menu
                        selectionMore = "n";
                        System.out.println("See profile of user :");
                        Scanner selectionuser = new Scanner(System.in);
                        String selectioned = selectionuser.nextLine();
                        if (selectioned.equals("X")) {

                        } else {
                            try{
                                int convertedSelection = Integer.parseInt(selectioned);
                                if (convertedSelection >= 0 && convertedSelection <= (users.size() - 1)) {
                                    System.out.println("==============PROFILE============= ");
                                    System.out.println("Name : " + users.get(convertedSelection).getUsername());
                                    System.out.println("Country : " + users.get(convertedSelection).getCountry());
                                    System.out.println("Email : " + users.get(convertedSelection).getEmail());
                                    System.out.println("Twitter Tag : " + users.get(convertedSelection).getTwitter_taster_handle());
                                    System.out.println("Followed Friends : " + graph.showFollowedUsers(users.get(convertedSelection).getUsername()).size());
                                    System.out.println("Followers : " + graph.countFollowersByUsername(users.get(convertedSelection).getUsername()));
                                    System.out.println("==============LIST OF MY FRIENDS============= ");
                                    ArrayList<User> usersFollowed = new ArrayList<>(graph.showFollowedUsers(users.get(convertedSelection).getUsername()));
                                    if (usersFollowed.size() != 0) {
                                        int z = 0;
                                        for (z = 0; z < usersFollowed.size(); z++) {


                                            System.out.println(z + " : name = " + usersFollowed.get(z).getUsername() + "   country = " + usersFollowed.get(z).getCountry());
                                            if (z != (usersFollowed.size() - 1)) {
                                                System.out.println("--------------------------------------------------");
                                            }
                                        }

                                    } else {
                                        System.out.println("You dont have friends");
                                    }
                                    System.out.println("==============LIST OF COMMENTS MADE============= ");
                                    ArrayList<Review> myReviews = new ArrayList<>(graph.showMyComment(users.get(convertedSelection).getUsername()));
                                    if (myReviews.size() != 0) {
                                        int k = 0;
                                        for (k = 0; k < myReviews.size(); k++) {
                                            System.out.println(k + " : Comment  ");
                                            System.out.println(myReviews.get(k).getDescription());
                                            System.out.println("rating = " + myReviews.get(k).getRating());
                                            System.out.println("like = " + graph.countLikeByDescription(myReviews.get(k).getDescription()));
                                            System.out.println("wine = " + graph.findWineByDescription(myReviews.get(k).getDescription()).get(0).getWineName());
                                            if (graph.checkIfLikedByDescription(myReviews.get(k).getDescription(), myUsername) == 1) {
                                                System.out.println("Like = V");
                                            } else if (graph.checkIfLikedByDescription(myReviews.get(k).getDescription(), myUsername) == 0) {
                                                System.out.println("Like = X");
                                            }

                                            if (k != (myReviews.size() - 1)) {
                                                System.out.println("------------------------------------------------");
                                            }
                                        }
                                        System.out.println("==================================================" + "\n");
                                    } else {
                                        System.out.println("He dont have review");
                                    }

                                    System.out.println("What do you want do?");
                                    System.out.println("1.  Delete one review"); //--->mongo ok
                                    System.out.println("2.  Delete account"); //--> mongo ok
                                    Scanner scanSelection = new Scanner(System.in);
                                    String selection = scanSelection.nextLine();
                                    if (selection.equals("1")) {
                                        deleteComment(myReviews);
                                    } else if (selection.equals("2")) {
                                        deleteAccount(users.get(convertedSelection).getUsername());
                                    }

                                } else {
                                    System.out.println("Selection wrong");
                                }
                            }catch (NumberFormatException nex){
                                System.out.println("You have to insert a number not a string");
                            }

                        }
                        //end see profile menu
                    }
                }
            } else {
                System.out.println("No users on the social");
            }
        }

    }


    public void reviewToPutLike(ArrayList<Review> reviews, String username) {
        System.out.println("Select a comment to put like :");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("X")) {

        } else {
            try{
                int selectedInt = Integer.parseInt(selected);
                if (selectedInt >= 0 && selectedInt <= (reviews.size() - 1)) {
                    graph.putLikeByDescription(reviews.get(selectedInt).getDescription(), username);
                } else {
                    System.out.println("Selection wrong");
                }
            }catch (NumberFormatException nex){
                System.out.println("You have to insert a number not a string");
            }

        }
    }

    public void reviewToDeleteLike(ArrayList<Review> reviews, String username) {
        System.out.println("Select a comment to delete like :");
        Scanner scanSelectDeleteLike = new Scanner(System.in);
        String selectedDeleteLike = scanSelectDeleteLike.nextLine();
        if (selectedDeleteLike.equals("X")) {

        } else {
            try{
                int selectedInt = Integer.parseInt(selectedDeleteLike);
                if (selectedInt >= 0 && selectedInt <= (reviews.size() - 1)) {
                    graph.deleteLikeByDescription(reviews.get(selectedInt).getDescription(), username);
                } else {
                    System.out.println("Selection wrong");
                }
            } catch (NumberFormatException nex){
                System.out.println("You have to insert a number not a string");
            }
        }
    }

    public void reviewToDelete(ArrayList<Review> reviews) {
        System.out.println("Select a comment to delete :");
        Scanner scanSelectDelete = new Scanner(System.in);
        String selectedDelete = scanSelectDelete.nextLine();
        if (selectedDelete.equals("X")) {

        } else {
            try{
                int selectedDeleteInt = Integer.parseInt(selectedDelete);
                if (selectedDeleteInt >= 0 && selectedDeleteInt <= (reviews.size() - 1)) {
                    graph.deleteAllRelationLikeByDescription(reviews.get(selectedDeleteInt).getDescription());
                    graph.deleteAllRelationRelatedByDescription(reviews.get(selectedDeleteInt).getDescription());
                    graph.deleteAllRelationCreatedByDescription(reviews.get(selectedDeleteInt).getDescription());
                    graph.deleteCommentByDescription(reviews.get(selectedDeleteInt).getDescription());
                    mongo.deleteComment(reviews.get(selectedDeleteInt).getDescription(),graph.findUserByDescription(reviews.get(selectedDeleteInt).getDescription()).get(0).getUsername(),graph.findWineByDescription(reviews.get(selectedDeleteInt).getDescription()).get(0).getWineName());
                } else {
                    System.out.println("Selection wrong");
                }
            }catch (NumberFormatException nex){
                System.out.println("You have to insert a number not a string");
            }
        }
    }

    public void show10Comment(ArrayList<Review> reviews, int times, int perTimes, String myUsername) {
        int j = times * perTimes;
        if (reviews.size() == 0) {
            System.out.println("No wine on the social");
        }
        for (j = times * perTimes; (j < reviews.size() && j < ((perTimes * times) + perTimes)); j++) {
            System.out.println(j + " : Comment  ");
            System.out.println(reviews.get(j).getDescription());
            System.out.println("rating = " + reviews.get(j).getRating());
            System.out.println("like = " + graph.countLikeByDescription(reviews.get(j).getDescription()));
            System.out.println("made by:  = " + graph.findUserByDescription(reviews.get(j).getDescription()).get(0).getUsername());
            if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 1) {
                System.out.println("Like = V");
            } else if (graph.checkIfLikedByDescription(reviews.get(j).getDescription(), myUsername) == 0) {
                System.out.println("Like = X");
            }

            if (j != (reviews.size() - 1)) {
                System.out.println("------------------------------------------------");
            }
        }

    }


    public void showCommentAdminMenu(final String myUsername) {
        ArrayList<Review> reviews = new ArrayList<>(graph.showAllComments());
        int times = 0;
        int perTimes = 10;
        if (reviews.size() != 0) {
            int j = 0;
            System.out.println("=================All Comments=======================");
            show10Comment(reviews, times, perTimes, myUsername);
            times++;
            String selectionMore = "y";
            while (selectionMore.equals("y")) {
                if (perTimes * times < (reviews.size() - 1)) {
                    System.out.println("\nDo you want to see 10 more comment? y/n");
                    Scanner scanSelectionMore = new Scanner(System.in);
                    selectionMore = scanSelectionMore.nextLine();
                    if (selectionMore.equals("y")) {
                        times++;
                        show10Comment(reviews, times, perTimes, myUsername);
                    } else if (selectionMore.equals("n")) {
                        System.out.println("=======================================================");
                        System.out.println("what do you want to do?");
                        System.out.println("1 : Put like on a Post");
                        System.out.println("2 : Delete Like on a Post");
                        System.out.println("3 : Delete  a Post"); //--> mongo ok
                        Scanner scanSelectlike = new Scanner(System.in);
                        String selectedLike = scanSelectlike.nextLine();
                        if (selectedLike.equals("1")) {
                            reviewToPutLike(reviews, myUsername);
                        } else if (selectedLike.equals("2")) {
                            reviewToDeleteLike(reviews, myUsername);

                        } else if (selectedLike.equals("3")) {
                            reviewToDelete(reviews);
                        }
                    }
                } else {
                    System.out.println("=======================================================");
                    selectionMore = "n";
                    System.out.println("what do you want to do?");
                    System.out.println("1 : Put like on a Post");
                    System.out.println("2 : Delete Like on a Post");
                    System.out.println("3 : Delete  a Post"); //-->mongo ok
                    Scanner scanSelectlike = new Scanner(System.in);
                    String selectedLike = scanSelectlike.nextLine();
                    if (selectedLike.equals("1")) {
                        reviewToPutLike(reviews, myUsername);
                    } else if (selectedLike.equals("2")) {
                        reviewToDeleteLike(reviews, myUsername);

                    } else if (selectedLike.equals("3")) {
                        reviewToDelete(reviews);
                    }
                }

            }


        } else {
            System.out.println("No comment on the social");
        }


    }

     static boolean onlyDigits(String str, int n) {
        for (int i = 0; i < n; i++) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private ArrayList<User> show10RandomUser(String myUsername) {
        ArrayList<User> listUser = new ArrayList<User>(graph.show10RandomUsers(myUsername));
        return listUser;
    }


    public void statsMenuAdmin(){
        System.out.println("what do you want to do?");
        System.out.println("1 : See top 5 country according to the rating");
        System.out.println("2 : See top 10 user with highest n° comment per variety ");
        System.out.println("3 : See top 20 wines with price lower than X");
        Scanner scanSelect = new Scanner(System.in);
        String selected = scanSelect.nextLine();
        if (selected.equals("1")) {
            adv_mongo.topFiveCountryAccordingRating();
        }else if(selected.equals("2")){
            adv_mongo.topTenUsersMadeHighestumberOfReveiwsPerVarieties();
        }else if(selected.equals("3")){
            adv_mongo.topThirtyWinesWithPriceLowerThan();
        }

    }

}



