package xjtlu.cpt111.assignment.quiz;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Login class handles user authentication and registration for the quiz system.
 * It provides functionalities to log in, register, and manage user data stored in a CSV file.
 */
public class Login {
    Scanner sc = new Scanner(System.in);

    /** Path to the CSV file storing user information. */
//    private static final String CSV_FILE = "assignment1_quizSystem/resources/users.csv";
    private static final String CSV_FILE = "resources/users.csv";
    /** The username of the currently logged-in user. */
    private String username;

    /**
     * Gets the username of the currently logged-in user.
     *
     * @return the username as a String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Starts the login or registration process.
     * Displays a menu for users to choose between logging in or registering.
     */
    public void start() {

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("---------Login---------");
            System.out.println("1. User login");
            System.out.println("2. Registration");
            System.out.println("3. Exit");
            System.out.println("Select from:");

            String command = sc.nextLine();
            switch (command) {
                case "1":  //User login
                    username = login();
                    if (username != null) {
                        isRunning = false;
                    }
                    break;
                case "2":
                    Registration();
                    break;
                //open an account
                case "3":
                    System.exit(0);
                default:
                    System.out.println("There is no such action.");

            }
        }
    }

    /**
     * Logs the user into the system by validating credentials stored in the CSV file.
     *
     * @return the username if login is successful, or null if no accounts exist
     */
    private String login() {
        String[][] usersData = this.CSVtoArray();
        System.out.println("---------Login---------");
        // 1.Determine if the account exists
//        if (usersData.length == 0) { //If the csv is empty, you cannot enter the login, otherwise you will never be able to enter the correct account
//            System.out.println("There is no account in the system, please register first\n");
//            return null; //Jump out of login
//        }
        System.out.println("Please enter your username");
        String cardid = sc.next();
        boolean cardExists=false;
        int rowIndex=0;
        if (usersData.length != 0) {
            for (int i = 0; i < usersData.length; i++) {
                if (cardid.equals(usersData[i][0])) {
                    cardExists = true;
                    rowIndex = i; // Determine the number of lines
                    break;
                }
            }
        }
        if(!cardExists) {
            System.out.println("The username you entered does not exist, please try again\n");
            sc.nextLine();
            return null;
        }
        else {
            while (true) {
                // The username exists, enter the password
                System.out.println("Please enter password");
                String password = sc.next();
                if (password.equals(usersData[rowIndex][2])){
                    System.out.print("Login successful ");
                    return cardid;
                }else {
                    System.out.println("The password is incorrect, please re-enter it");
                }
            }
        }
    }

    /**
     * Registers a new user by collecting username, name, and password.
     * Validates input and ensures no duplicate usernames exist in the system.
     */
    public void Registration() {
        System.out.println("-------Registration-------");
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username");
        String nickname = sc.nextLine();
        System.out.println("Please enter your name:");
        String name = sc.nextLine();
        System.out.println("Please enter your password");
        String password = sc.nextLine();

        // Determine if the account exists
        if (nickname.length() == 0 || name.length() == 0 || password.length() == 0) {
            System.out.println("Information cannot be empty\n");
            return; // jump out
        }
        String[][] usersData = this.CSVtoArray();

        // Check that one person has created two duplicate accounts
        boolean cardExists = false;
        for (int i = 0; i < usersData.length; i++) {
            if (nickname.equals(usersData[i][0])) {
                cardExists = true;
                break;
            }
        }
        if (cardExists) {
            System.out.println("Cannot re-register");
            return;
        }

        // import
        String[] newUser = new String[3];
        newUser[0] = nickname;
        newUser[1] = name;
        newUser[2] = password;

        CSVWriter(newUser);

    }

    /**
     * Reads user data from the CSV file and converts it into a 2D array.
     *
     * @return a 2D array where each row represents a user's data (username, name, password)
     */
    public String[][] CSVtoArray() {
        // Detect the presence of score.csv
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating CSV file: " + e.getMessage());
            }
        }

        String line = "";
        String cvsSplitBy = ","; // Separator in CSV file

        ArrayList<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the ArrayList to a two-dimensional array
        String[][] usersData = new String[data.size()][]; // Array name：userData
        for (int i = 0; i < data.size(); i++) {
            usersData[i] = data.get(i);
        }

//        for (String[] row : usersData) { //TODO：test
//            for (String value : row) {
//                System.out.print(value + " ");
//            }
//            System.out.println();
//        }
        return usersData;
    }

    /**
     * Writes a new user's data to the CSV file.
     *
     * @param newUser an array containing the new user's data (username, name, password)
     */
    public void CSVWriter(String[] newUser) {    //Array type
        String cvsSplitBy = ","; // Separator in CSV file

        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE, true))) {

            StringBuilder sb = new StringBuilder();
            for (String value : newUser) {
                sb.append(value).append(cvsSplitBy);
            }
            sb.deleteCharAt(sb.length() - 1); // Remove the last delimiter
            pw.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

