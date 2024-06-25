package com.kirbysmashyeet.simpletimerapplication;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class TimerController {
    @FXML
    private TextField timerDisplay;
    @FXML
    private TextField hourInput;
    @FXML
    private TextField minuteInput;
    @FXML
    private TextField secondInput;
    @FXML
    private Button setButton;
    @FXML
    private Button startAndStopButton;
    @FXML
    private Button resetButton;

    private Timer timer;
    private boolean isTimerRunning;
    private PauseTransition debounceTransition;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;
    private Clip clip;


    public void initialize() {
        setupInputFilter(hourInput);
        setupInputFilter(minuteInput);
        setupInputFilter(secondInput);
        timer = new Timer();
        isTimerRunning = false;
        debounceTransition = new PauseTransition(Duration.seconds(1));
        debounceTransition.setOnFinished(_ -> startAndStopButton.setDisable(false));
    }

    @FXML
    private void setTimer() {
        int hour = parseInput(hourInput.getText());
        int minute = parseInput(minuteInput.getText());
        int second = parseInput(secondInput.getText());

        timer.set(hour, minute, second);
        updateTimerDisplay();
    }

    @FXML
    private void startAndStopTimer() {
        if (isTimerRunning) {
            setOneSecondDebounce();
            stopTimer();
            startAndStopButton.setText("Start");
        } else {
            setOneSecondDebounce();
            startTimer();
            startAndStopButton.setText("Stop");
        }
    }

    @FXML
    private void resetTimer() {
        stopTimer();
        startAndStopButton.setText("Start");
        hourInput.setText("");
        minuteInput.setText("");
        secondInput.setText("");
        timer.reset();
        clip.stop();
        updateTimerDisplay();
    }

    /*

        HELPER METHODS

    */

    private void setupInputFilter(TextField textField) {
        String regex = determineRegex(textField.getId());

        // Create a filter that determines what is allowed to be typed in the text field
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex)) {
                return change;
            }
            return null;
        };

        IntegerStringConverter converter = new IntegerStringConverter();

        TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, null, filter);
        // formats the text input based on the filter provided

        textField.setTextFormatter(textFormatter);
    }

    private String determineRegex(String inputType) {
        return switch (inputType) {
            case "hourInput" -> "\\d*"; // Allows any number of digits
            case "minuteInput", "secondInput" -> "([0-5]?[0-9]?)?"; // Allows only numbers 0-59
            default -> " ";
        };
    }

    private int parseInput(String text) {
        int DEFAULT_VALUE = 0;
        try {
            if (text.isEmpty()) {
                return DEFAULT_VALUE;
            } else {
                return Integer.parseInt(text);
            }
        } catch (NumberFormatException e) {
            return DEFAULT_VALUE;
        }
    }

    private void updateTimerDisplay() {
        timerDisplay.setText(timer.getTime());
    }

    private void startTimer() {
        isTimerRunning = true;
        timer.start();

        updateTimerDisplay();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                // Makes sure that the display updates every second in line with the timer
                updateTimerDisplay();

                // Plays the alarm sound when the timer finishes
                if (timer.getTime().equals("00:00:00")) {
                    timerFinished();
                }
            });
        }, 0, 1, TimeUnit.SECONDS);    }

    private void stopTimer() {
        isTimerRunning = false;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
        timer.stop();
    }

    private void setOneSecondDebounce() {
        startAndStopButton.setDisable(true);
        debounceTransition.playFromStart(); // Create a 1 sec debounce to prevent spamming
    }

    private void timerFinished() {
        playAlarmSound();
        stopTimer();
        timerDisplay.setText("Time's Up!");
        startAndStopButton.setText("Start");
    }

    private void playAlarmSound() {
        try {
            URL soundFileURL = getClass().getResource("/alarm-sound.wav");
            if (soundFileURL == null) {
                System.err.println("Sound file not found: alarm-sound.wav");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFileURL);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}