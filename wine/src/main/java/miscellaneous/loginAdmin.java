package miscellaneous;

import java.util.Scanner;

public class loginAdmin {

    public boolean logIn(){
        String Username = "admin";
        String Password = "root";

        Scanner input1 = new Scanner(System.in);
        System.out.println("Enter Username : ");
        String username = input1.next();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter Password : ");
        String password = input2.next();

        if (username.equals(Username) && password.equals(Password)) {
            System.out.println("Access Granted! Welcome!");
            return true;
        }

        else if (username.equals(Username)) {
            System.out.println("Invalid Password!");
            return false;
        } else if (password.equals(Password)) {
            System.out.println("Invalid Username!");
            return false;
        } else {
            System.out.println("Invalid Username & Password!");
            return false;
        }
    }

}
