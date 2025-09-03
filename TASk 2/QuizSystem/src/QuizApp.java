import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizApp extends JFrame implements ActionListener {

    // GUI Components
    private JLabel questionLabel, timerLabel;
    private JRadioButton[] optionButtons = new JRadioButton[4];
    private ButtonGroup optionsGroup;
    private JButton nextButton, prevButton, submitButton;
    private Timer timer;

    // Quiz Logic
    private List<String[]> questions = new ArrayList<>(); // [question, opt1, opt2, opt3, opt4, correctAnsIndex]
    private Map<Integer, Integer> userAnswers = new HashMap<>(); // <QuestionIndex, SelectedOptionIndex>
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int timeLeft = 15; // 15 seconds per question

    public QuizApp() {
        setTitle("Online Quiz System");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loadQuestions();
        initComponents();
        displayQuestion();

        setVisible(true);
    }

    private void loadQuestions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("questions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] questionBlock = new String[6];
                questionBlock[0] = line; // Question
                for (int i = 1; i <= 4; i++) {
                    questionBlock[i] = reader.readLine(); // Options
                }
                questionBlock[5] = reader.readLine(); // Correct Answer Index
                questions.add(questionBlock);
                reader.readLine(); // Skip separator "---"
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading questions from file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initComponents() {
        // Top Panel for Timer
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerLabel = new JLabel("Time Left: 15s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel for Question and Options
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        questionLabel = new JLabel("Question will be here.");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        optionsGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionsGroup.add(optionButtons[i]);
            centerPanel.add(optionButtons[i]);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Navigation Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit");

        prevButton.addActionListener(this);
        nextButton.addActionListener(this);
        submitButton.addActionListener(this);

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize and start the timer
        setupTimer();
    }

    private void setupTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft + "s");
            if (timeLeft <= 0) {
                timer.stop();
                handleTimeUp();
            }
        });
    }

    private void handleTimeUp() {
        storeAnswer(); // Store null if no answer is selected
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            // Last question, so submit automatically
            calculateAndShowResult();
        }
    }

    private void displayQuestion() {
        timer.stop();
        timeLeft = 15;
        timerLabel.setText("Time Left: " + timeLeft + "s");
        timer.start();

        String[] q = questions.get(currentQuestionIndex);
        questionLabel.setText("<html>Q" + (currentQuestionIndex + 1) + ": " + q[0] + "</html>");
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q[i + 1]);
        }

        // Clear previous selection
        optionsGroup.clearSelection();

        // Restore user's previous answer if it exists
        if(userAnswers.containsKey(currentQuestionIndex)) {
            optionButtons[userAnswers.get(currentQuestionIndex)].setSelected(true);
        }

        // Update button states
        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < questions.size() - 1);
    }

    private void storeAnswer() {
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                userAnswers.put(currentQuestionIndex, i);
                return;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        storeAnswer();

        if (e.getSource() == nextButton) {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            }
        } else if (e.getSource() == prevButton) {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion();
            }
        } else if (e.getSource() == submitButton) {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit?", "Confirm Submission", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                calculateAndShowResult();
            }
        }
    }

    private void calculateAndShowResult() {
        timer.stop();
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            int correctAnswerIndex = Integer.parseInt(questions.get(i)[5]);
            if (userAnswers.containsKey(i) && userAnswers.get(i) == correctAnswerIndex) {
                score++;
            }
        }

        // Display Result Summary
        int totalQuestions = questions.size();
        double percentage = ((double) score / totalQuestions) * 100;
        String resultMessage = String.format(
                "Quiz Finished!\n\nTotal Questions: %d\nCorrect Answers: %d\nIncorrect Answers: %d\nYour Score: %.2f%%",
                totalQuestions, score, totalQuestions - score, percentage
        );

        JOptionPane.showMessageDialog(this, resultMessage, "Result Summary", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
