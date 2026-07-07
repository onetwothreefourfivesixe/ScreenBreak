package com.reminder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        this.stage = stage;
        scene = new Scene(loadFXML("timerControl"), 640, 480);
        stage.setScene(scene);
        setupSystemTray();
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void setupSystemTray() {
        // Check if the OS actually supports system trays
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported on this OS.");
            return;
        }

        // Get the AWT SystemTray instance
        SystemTray tray = SystemTray.getSystemTray();

        // Load an image for the icon (ensure you have an icon.png in your resources)
        URL imageUrl = App.class.getResource("/newIcon.png");
        Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);

        // Create the tray icon
        TrayIcon trayIcon = new TrayIcon(image, "ScreenBreak");
        trayIcon.setImageAutoSize(true); // Resizes the image to fit the OS tray

        // --- ADD A RIGHT-CLICK MENU ---
        PopupMenu popup = new PopupMenu();

        MenuItem showItem = new MenuItem("Open Application");
        showItem.addActionListener(e -> showStage());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            // Remove the icon and shut down JavaFX
            tray.remove(trayIcon);
            Platform.exit();
        });

        popup.add(showItem);
        popup.addSeparator();
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);

        // --- ADD A DOUBLE-CLICK ACTION ---
        trayIcon.addActionListener(e -> showStage());

        // Finally, add the icon to the system tray
        try {
            tray.add(trayIcon);
        } catch (Exception e) {
            System.out.println("Could not add icon to tray.");
        }
    }

    private void showStage() {
        Platform.runLater(() -> {
            if (stage != null) {
                stage.show();
                stage.toFront(); // Brings the window to the foreground
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

}