package xjtlu.cpt111.assignment.quiz;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The ScoreManager class handles storing, retrieving, and displaying user scores for different quiz topics.
 * It manages recent scores, the highest score for each topic, and provides functionality to update a CSV file
 * that persists this data.
 */
public class ScoreManager {
//    private static final String CSV_FILE = "assignment1_quizSystem/resources/score.csv";
    private static final String CSV_FILE = "resources/score.csv";
    private static final int MAX_RECENT_SCORES = 3;
    private String username;
    private String topic;

    /**
     * Constructs a ScoreManager instance with the specified username, and topic.
     *
     * @param username    the name of the user
     * @param topic       the topic of the quiz
     */
    public ScoreManager(String username, String topic) {
        this.username = username;
        this.topic = topic;
    }

    /**
     * Saves the current score to the CSV file. It updates the recent scores and highest score
     * for the given user and topic.
     */
    public void saveScore(int currentScore) {

        // Detect the presence of score.csv
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating CSV file: " + e.getMessage());
                return;
            }
        }

        List<String[]> allData = new ArrayList<>(); // Used to write to csv, each row is username, topicName, score, type
        List<Integer> recentScores = new ArrayList<>(); // The last 3 results
        int topicHighestScore = -1;
        String highestUserForTopic = "";

        // read from csv
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                allData.add(fields);

                String user = fields[0];
                String topicName = fields[1];
                int score = Integer.parseInt(fields[2]);
                String type = fields[3];

                // recent 3 scores
                if (user.equals(username) && topicName.equals(topic) && type.equals("recent")) {
                    recentScores.add(score);
                }
                // highest for each topic
                if (topicName.equals(topic) && type.equals("highest")) {
                    topicHighestScore = score;
                    highestUserForTopic = user;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // If there are more than 3 records, delete them
        recentScores.add(currentScore);
        if (recentScores.size() > MAX_RECENT_SCORES) {
            recentScores = recentScores.subList(recentScores.size() - MAX_RECENT_SCORES, recentScores.size());
        }

        // If it is the highest score, it will be updated (it will not be updated if it is the same score as the highest score)
        if (currentScore > topicHighestScore) {
            topicHighestScore = currentScore;
            highestUserForTopic = username;
        }

        // Update the data in allData and write back to csv
        // remove old data
        allData.removeIf(record -> record[0].equals(username) && record[1].equals(topic) && record[3].equals("recent")); // recent
        allData.removeIf(record -> record[1].equals(topic) && record[3].equals("highest")); // highest
        // Write back to recent
        for (int score : recentScores) {
            allData.add(new String[] {username, topic, String.valueOf(score), "recent"});
        }
        // Write back to highest
        allData.add(new String[] {highestUserForTopic, topic, String.valueOf(topicHighestScore), "highest"});

        // write csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (String[] record : allData) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays the recent scores of the current user for the selected topic.
     */
    public void displayRecentScores() {
        System.out.println("Recent scores:");
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            int count = 0;
            boolean hasscore = false;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String user = fields[0];
                String topicName = fields[1];
                int score = Integer.parseInt(fields[2]);
                String type = fields[3];

                if (user.equals(username) && topicName.equals(topic) && type.equals("recent")) {
                    System.out.println("Score " + (++count) + ": " + score); // Score1 is the most previous score, and Score3 is the current score
                    hasscore = true;
                }
            }
            if (!hasscore) {
                System.out.println("No scores recorded for the user yet.");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays the highest score for the selected topic.
     */
    public void displayHighestScore() {
        System.out.print("Highest scores for " + topic + ": ");
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            int highestScore = 0;
            String highestUser = "";
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String user = fields[0];
                String topicName = fields[1];
                int score = Integer.parseInt(fields[2]);
                String type = fields[3];

                if (topicName.equals(topic) && type.equals("highest")) {
                    highestScore = score;
                    highestUser = user;
                }
            }
            if (!highestUser.isEmpty()) {
                System.out.println(highestUser + "\nScore: " + highestScore);
            } else {
                System.out.println("No scores recorded for this topic yet.");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



//    public static void main(String[] args) {
//        ScoreManager a = new ScoreManager("a",10,"cs");
//        a.saveScore();
//        a.displayRecentScores();
//        a.displayHighestScore();
//    }

}
