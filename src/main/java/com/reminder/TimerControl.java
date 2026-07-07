package com.reminder;

import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.Properties;

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
    @FXML
    private CheckBox soundAlertCheckBox;
    @FXML
    private Label soundFileLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;
    @FXML
    private TextArea reminderMessageArea;

    private Media soundMedia;
    private MediaPlayer mediaPlayer;
    private Properties appProperties;

    private boolean soundAlertEnabled = false;
    private final ReminderTimer reminderTimer = new ReminderTimer(soundAlertEnabled);
    private StringProperty status = new SimpleStringProperty("Idle");

    @FXML
    private void initialize() {
        // Bind the UI to the timer's observable state; after this we never touch
        // the nodes directly — updating the timer updates the display.
        statusLabel.textProperty().bind(status);
        reminderProgress.progressProperty().bind(reminderTimer.progressProperty());
        timeLabel.textProperty().bind(reminderTimer.timeTextProperty());
        volumeLabel.textProperty().bind(volumeSlider.valueProperty().asString("%.0f%%"));

        loadConfigInfo();

        // The scene/window aren't attached yet during initialize(), so wait for the
        // window to exist and then persist all config info whenever it's closed.
        intervalField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnCloseRequest(e -> saveConfigInfo());
                    }
                });
            }
        });

        reminderMessageArea.textProperty().addListener((obs, oldText, newText) -> {
            reminderTimer.updateReminderMessage(newText);
        });
    }

    private void loadConfigInfo() {
        appProperties = new AppData().getProperties();
        if (appProperties.containsKey("interval")) {
            intervalField.setText(appProperties.getProperty("interval"));
        } else {
            intervalField.setText("20");
        }
        if (appProperties.containsKey("soundAlertEnabled")) {
            soundAlertEnabled = Boolean.parseBoolean(appProperties.getProperty("soundAlertEnabled"));
            soundAlertCheckBox.setSelected(soundAlertEnabled);
        } else {
            soundAlertCheckBox.setSelected(false);
        }
        if (appProperties.containsKey("reminderMessage")) {
            reminderMessageArea.setText(appProperties.getProperty("reminderMessage"));
        } else {
            reminderMessageArea.setText("Look outside for 20 seconds.");
        }
        if (!appProperties.contains("volume")) {
            volumeSlider.setValue(50);
        } else {
            volumeSlider.setValue(Double.parseDouble(appProperties.getProperty("volume")));
        }
    }

    private void saveConfigInfo() {
        appProperties.setProperty("interval", intervalField.getText().trim());
        appProperties.setProperty("soundAlertEnabled", Boolean.toString(soundAlertCheckBox.isSelected()));
        appProperties.setProperty("reminderMessage", reminderMessageArea.getText().trim());
        appProperties.setProperty("volume", Double.toString(volumeSlider.getValue()));
        new AppData().saveProperties(appProperties);
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

    @FXML
    private void onSoundAlertChanged() {
        soundAlertEnabled = soundAlertCheckBox.isSelected();
        reminderTimer.updateSoundAlertEnabled(soundAlertEnabled);
        if (soundAlertCheckBox.isSelected()) {
            File soundFile = new AppData().loadSoundFile();
            if (soundFile != null && soundFile.exists()) {
                soundFileLabel.setText(soundFile.getName());
                soundMedia = new Media(soundFile.toURI().toString());
                mediaPlayer = new MediaPlayer(soundMedia);
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                reminderTimer.setMediaPlayer(mediaPlayer);
            } else {
                FileChooser chooseSoundFile = new FileChooser();
                chooseSoundFile.setTitle("Select Sound File");
                chooseSoundFile.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
                File selectedFile = chooseSoundFile.showOpenDialog(null);

                if (selectedFile != null) {
                    // Copy the picked file into our sounds/ folder so the choice
                    // survives restarts (loadSoundFile() reads from there next time).
                    new AppData().uploadSoundFile(selectedFile);
                    soundFileLabel.setText(selectedFile.getName());
                    soundMedia = new Media(selectedFile.toURI().toString());
                    mediaPlayer = new MediaPlayer(soundMedia);
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                    reminderTimer.setMediaPlayer(mediaPlayer);
                }
            }
        } else {
            soundFileLabel.setText("Sound alert disabled");
        }
    }
}
