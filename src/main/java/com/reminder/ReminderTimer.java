package com.reminder;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Owns the JavaFX {@link Timeline} that drives a screen-break reminder.
 * It ticks once per second, exposing observable progress (0.0–1.0) and a
 * "elapsed / total minutes" text that UI controls can bind to directly.
 */
public class ReminderTimer {

    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty timeText = new SimpleStringProperty("0 / 0 minutes");

    private Timeline timeline;
    private int totalSeconds;
    private int elapsedSeconds;
    private boolean running;
    private boolean soundAlertEnabled = false;
    private MediaPlayer mediaPlayer;

    public ReminderTimer(boolean soundAlertEnabled) {
        this.soundAlertEnabled = soundAlertEnabled;
        this.totalSeconds = 0;
        this.elapsedSeconds = 0;
        this.running = false;
    }

    public void updateSoundAlertEnabled(boolean enabled) {
        this.soundAlertEnabled = enabled;
    }

    /**
     * Supplies the player used for the break chime. Passed in from the
     * controller (which owns sound-file selection) so this class stays focused
     * on timing rather than building {@link javafx.scene.media.Media} itself.
     * Pass {@code null} to clear it.
     */
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    /** Bar can bind to this: 0.0 at the start of an interval, 1.0 when it fires. */
    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    /** Label can bind to this: "3 / 20 minutes". */
    public ReadOnlyStringProperty timeTextProperty() {
        return timeText;
    }

    public boolean isRunning() {
        return running;
    }

    /** Start (or restart) the reminder cycle with the given interval in minutes. */
    public boolean start(int intervalMinutes) {
        if (intervalMinutes <= 0) {
            return false;
        }
        stop(); // clear any previous run and its timeline
        totalSeconds = intervalMinutes * 60;
        elapsedSeconds = 0;
        updateDisplay();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        running = true;
        return true;
    }

    /** Stop the cycle and reset the display to idle. */
    public boolean stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        running = false;
        elapsedSeconds = 0;
        totalSeconds = 0;
        progress.set(0);
        timeText.set("0 / 0 minutes");
        return true;
    }

    private void tick() {
        elapsedSeconds++;
        if (elapsedSeconds >= totalSeconds) {
            showBreakReminder();
            elapsedSeconds = 0; // begin the next interval
        }
        updateDisplay();
    }

    private void updateDisplay() {
        progress.set(totalSeconds == 0 ? 0 : (double) elapsedSeconds / totalSeconds);
        timeText.set((elapsedSeconds / 60) + " / " + (totalSeconds / 60) + " minutes");
    }

    private void showBreakReminder() {
        // Play the chime first so it coincides with the reminder appearing.
        if (soundAlertEnabled && mediaPlayer != null) {
            // stop() rewinds to the start, so repeated breaks replay the clip.
            mediaPlayer.stop();
            mediaPlayer.play();
        }

        // Already on the JavaFX thread (Timeline callback). show() is non-blocking,
        // so the timeline keeps ticking while the reminder is on screen.
        Alert alert = new Alert(AlertType.INFORMATION, "Look outside for 20 seconds");
        alert.setHeaderText(null);
        alert.setTitle("Screen Break");
        alert.show();
    }
}
