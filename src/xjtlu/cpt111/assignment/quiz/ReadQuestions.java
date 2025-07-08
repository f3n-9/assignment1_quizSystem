package xjtlu.cpt111.assignment.quiz;

import xjtlu.cpt111.assignment.quiz.model.Difficulty;
import xjtlu.cpt111.assignment.quiz.model.Option;
import xjtlu.cpt111.assignment.quiz.model.Question;
import xjtlu.cpt111.assignment.quiz.util.IOUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code ReadQuestions} class provides methods for reading questions from a file system,
 * selecting topics and difficulties, and performing operations like shuffling options.
 */
public class ReadQuestions {

//    private static final String QUESTIONS_BANK_PATH = "assignment1_quizSystem/resources/questionsBank";
    private static final String QUESTIONS_BANK_PATH = "resources/questionsBank";

    /**
     * Retrieves a list of topics available in the given directory.
     *
     * @param path the directory path to search for topics
     * @return a list of topic names extracted from the file names
     * @throws IllegalArgumentException if no topics are found in the directory
     */
    private static List<String> getTopicsFromPath(String path) { // get the number of topics in the read file path
        List<String> topics = new ArrayList<>();
        File folder = new File(path);

        if (folder.exists() && folder.isDirectory()) {

            for (File file : folder.listFiles()) {
                String filename = file.getName();

                // Check if it meets the "questions_*.xml"
                if (filename.startsWith("questions_") && filename.endsWith(".xml")) {
                    // Extract the topic name
                    String topic = filename.substring(10, filename.length() - 4);
                    topics.add(topic);
                }
            }
        } else {
            System.out.println("Directory not found: " + path);
        }
        if (topics.isEmpty()) {
            throw new IllegalArgumentException("There is no question. Please contact the teacher for assistance.");
        }
        return topics;
    }

    /**
     * Allows the user to select a topic and retrieves questions for that topic.
     *
     * @return an array of {@code Question} objects for the selected topic
     * @throws IllegalArgumentException if no questions are available for the selected topic
     */
    public static Question[] SelectTopic() {

        Scanner sc = new Scanner(System.in);

        Question[] questions = null;

        System.out.println("===\n=== read questions - started\n===");
        try {

            // The range is given based on the file name
            List<String> topics = getTopicsFromPath(QUESTIONS_BANK_PATH);
            String field;
            while (true) {
                System.out.print("Please select the topic (");
                for (int i = 0; i < topics.size(); i++) {
                    System.out.print("\"" + topics.get(i) + "\"");
                    if (i != topics.size() - 1) {
                        System.out.print(" or ");
                    }
                }
                System.out.print(")\n");

                field = sc.nextLine();

                if (topics.contains(field)) {
                    break; // If the input is valid, jump out of the loop
                } else {
                    System.out.println("Invalid topic selected. Please select a valid topic.\n");
                }
            }

            String filename = QUESTIONS_BANK_PATH + "/questions_" + field + ".xml";
            questions = IOUtilities.readQuestions(filename);
        }
        catch(Exception e){
            e.printStackTrace();
        } finally{
            System.out.println("===\n=== read questions - complete\n===\n");
        }

        if (questions == null || questions.length == 0) {
            throw new IllegalArgumentException("No questions in this topic, Please contact the teacher for assistance.");
        }
        return questions;
    }

    /**
     * Selects a specified number of questions for each difficulty level from the provided question pool.
     *
     * @param allquestions an array of all available {@code Question} objects
     * @param EASY         the number of easy questions to select
     * @param MEDIUM       the number of medium questions to select
     * @param HARD         the number of hard questions to select
     * @param VERY_HARD    the number of very hard questions to select
     * @return an array of selected {@code Question} objects
     * @throws IllegalArgumentException if the requested number of questions cannot be satisfied
     */
    public static Question[] selectQuestions(Question[] allquestions, int EASY, int MEDIUM, int HARD, int VERY_HARD) {
        if ((EASY< 0 || MEDIUM< 0 || HARD< 0 || VERY_HARD < 0) || (EASY + MEDIUM + HARD + VERY_HARD == 0)) {
            throw new IllegalArgumentException("The number of questions is incorrect.  Please contact the teacher for assistance.");
        }

        int[] canselect = new int[allquestions.length]; // 0 represents can be selected, 1 represents cannot be selected (selected, scrapped question)

        // Determine whether the question is scrapped Scrap the question；getTopic() is empty，getQuestionStatement() is empty，getOptions() length<=1，options[].isCorrectAnswer()true number !=1
        for (int i = 0; i < allquestions.length; i++) {

            if (allquestions[i].getTopic() == null || allquestions[i].getQuestionStatement() == null) { // It is possible that this judgment can be commented out, because the ReadQuestion function given by him cannot read null values
                canselect[i] = 1;
            }

            if (allquestions[i].getOptions().length <=1) {
                canselect[i] = 1;
            }

            int numofcorrect = 0;
            Option[] options = allquestions[i].getOptions();
            for (int j = 0; j < options.length; j++) {
                if (options[j].isCorrectAnswer()) {
                    numofcorrect++;
                }
            }
            if (numofcorrect != 1) {
                canselect[i] = 1;
            }
        }

        // Test whether the questions of each difficulty of EASY, MEDIUM, HARD,and VERY_HARD are enough, and if they are not enough, they will report errors
        int easycount = 0;
        int mediumcount = 0;
        int hardcount = 0;
        int veryhardcount = 0;
        for (int i = 0; i < canselect.length; i++) {
            if (canselect[i] == 0) { //Count how many questions there are in each difficulty
                if (allquestions[i].getDifficulty() == Difficulty.EASY) {
                    easycount++;
                }
                else if (allquestions[i].getDifficulty() == Difficulty.MEDIUM) {
                    mediumcount++;
                }
                else if (allquestions[i].getDifficulty() == Difficulty.HARD) {
                    hardcount++;
                }
                else if (allquestions[i].getDifficulty() == Difficulty.VERY_HARD) {
                    veryhardcount++;
                }
            }
        }
        if (easycount < EASY || mediumcount < MEDIUM || hardcount < HARD || veryhardcount < VERY_HARD) {
            throw new IllegalArgumentException("There are not enough questions. Please contact the teacher for assistance.");
        }

        // Randomly select among the ones that can be selected, and those who have been selected cannot be selected again
        Question[] selected = new Question[EASY+MEDIUM+HARD+VERY_HARD];
        int i = 0;
        while (true) {
            int random = (int) (Math.random() * allquestions.length);
            if (canselect[random] == 0 && (allquestions[random].getDifficulty()==Difficulty.EASY && EASY > 0)) {
                selected[i] = allquestions[random];
                canselect[random] = 1;
                i++;
                EASY--;
            }
            if (canselect[random] == 0 && (allquestions[random].getDifficulty()==Difficulty.MEDIUM && MEDIUM > 0)) {
                selected[i] = allquestions[random];
                canselect[random] = 1;
                i++;
                MEDIUM--;
            }
            if (canselect[random] == 0 && (allquestions[random].getDifficulty()==Difficulty.HARD && HARD > 0)) {
                selected[i] = allquestions[random];
                canselect[random] = 1;
                i++;
                HARD--;
            }
            if (canselect[random] == 0 && (allquestions[random].getDifficulty()==Difficulty.VERY_HARD && VERY_HARD > 0)) {
                selected[i] = allquestions[random];
                canselect[random] = 1;
                i++;
                VERY_HARD--;
            }
            if (selected[selected.length-1] != null) { // Fill up, end
                break;
            }
        }
        return selected;
    }

    /**
     * Shuffles the order of options in a {@code Question}.
     *
     * @param options an array of {@code Option} objects to be shuffled
     */
    public static void shuffleOptions(Option[] options) {
        for (int i = 0; i < options.length; i++) {

            int random = (int) (Math.random() * options.length);

            Option temp = options[random];
            options[random] = options[i];
            options[i] = temp;
        }
    }

}
