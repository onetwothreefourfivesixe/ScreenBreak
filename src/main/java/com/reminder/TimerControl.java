package com.reminder;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class TimerControl {

    @FXML
    private Label statusLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private TextField intervalField;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private ProgressBar reminderProgress;

    private final ReminderTimer reminderTimer = new ReminderTimer();
    private final StringProperty status = new SimpleStringProperty("Idle");

    @FXML
    private void initialize() {
        // Bind the UI to the timer's observable state; after this we never touch
        // the nodes directly — updating the timer updates the display.
        statusLabel.textProperty().bind(status);
        reminderProgress.progressProperty().bind(reminderTimer.progressProperty());
        timeLabel.textProperty().bind(reminderTimer.timeTextProperty());
    }

    @FXML
    private void onStart() {
        int minutes;
        try {
            minutes = Integer.parseInt(intervalField.getText().trim());
        } catch (NumberFormatException e) {
            status.set("Enter a valid number");
            return;
        }
        if (reminderTimer.start(minutes)) {
            status.set("Running");
        } else {
            status.set("Interval must be > 0");
        }
    }

    @FXML
    private void onStop() {
        reminderTimer.stop();
        status.set("Idle");
    }
}
