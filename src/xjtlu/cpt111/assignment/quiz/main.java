package xjtlu.cpt111.assignment.quiz;

import xjtlu.cpt111.assignment.quiz.model.Option;
import xjtlu.cpt111.assignment.quiz.model.Question;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The main class of the quiz application.
 * Handles user login, question selection, answering, and score management.
 */
public class main {

    /** Username of the player */
    private static String username;
    /** Selected topic for the quiz */
    private static String topic;
    /** Total points scored by the user */
    private static int totalpoints = 0;

    // The number of questions of each difficulty
    // If there are too many questions, an error will be reported if the total number of questions exceeds the total number of question banks
    private static final int EASY = 1;
    private static final int MEDIUM = 1;
    private static final int HARD = 1;
    private static final int VERY_HARD = 1;

    /**
     * The main method to start the quiz application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        while (true) {
            // Signup and Login
            Login login = new Login();
            login.start();
            username = login.getUsername();
            System.out.println("Welcome " + username + "\n");

            // choose topic
            Question[] allquestions;
            allquestions = ReadQuestions.SelectTopic(); // allquestions represents all questions in this topic
            topic = allquestions[0].getTopic();

            while (true) {
                Scanner sc = new Scanner(System.in);

                System.out.println("What you want to do?");
                System.out.println("1. Do a quiz");
                System.out.println("2. Your Dashboard");
                System.out.println("3. Logout");
                System.out.println("4. Exit");
                System.out.println("Select from:");
                String command = sc.nextLine();
                switch (command) {
                    case "1":
                        System.out.println("Quiz Starts!!!");
                        Question[] selected = ReadQuestions.selectQuestions(allquestions, EASY, MEDIUM, HARD, VERY_HARD);  // Choose the question

                        // Present the questions
                        for (int i = 0; i < selected.length; i++) {
                            System.out.println(selected[i].getQuestionStatement());
                            Option[] options = selected[i].getOptions();

                            ReadQuestions.shuffleOptions(options); // shuffle options

                            for (int j = 0; j < options.length; j++) {
                                System.out.println((j + 1) + ": " + options[j].getAnswer());
                                //                System.out.println(options[j].isCorrectAnswer()); //TODO: For testing
                            }

                            // answer
                            int ans = -1;
                            while (true) {
                                System.out.println("Please print the answer (Print a number)");
                                try {
                                    ans = sc.nextInt();
                                    if (ans >= 1 && ans <= options.length) { // Validity test
                                        if (options[ans - 1].isCorrectAnswer()) {
                                            totalpoints++;
                                        }
                                        //                        System.out.println("points:" + totalpoints + "\n"); //TODO: For testing
                                        break;
                                    } else {
                                        System.out.println("Invalid input");
                                    }
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input");
                                    sc.next();
                                }
                            }
                        }
                        System.out.println("points: " + totalpoints + "\n");

                        // Leaderboard, save results
                        ScoreManager manager = new ScoreManager(username, topic);
                        manager.saveScore(totalpoints);
                        manager.displayRecentScores();
                        System.out.println("Congratulations! You have completed the quiz!\n\n");
                        break;

                    case "2":
                        manager = new ScoreManager(username, topic);
                        manager.displayRecentScores();
                        manager.displayHighestScore();
                        System.out.println("Press Enter to continue...");
                        while (true) {
                            String input = sc.nextLine();
                            if (input.equals("")) {
                                break;
                            }
                            System.out.println("Please press Enter to continue...");
                        }
                        break;

                    case "3":
                        break;

                    case "4":
                        System.exit(0);

                    default:
                        System.out.println("There is no such action.");

                }

                if (command.equals("3")) {
                    break;
                }
            }
        }
    }
}
