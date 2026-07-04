# Screen Break

A small JavaFX desktop app that reminds you to rest your eyes at a fixed interval. Enter how often you want to be reminded, start the timer, and the app pops up a break reminder each time the interval elapses — while a progress bar shows how far you are into the current interval. It lives in the system tray so it can keep running quietly in the background.

## Features

- **Configurable interval** — set the reminder frequency (in minutes) from the UI.
- **Live progress** — a progress bar and `elapsed / total minutes` label track the current interval second by second.
- **Break reminders** — a dialog prompts you to look away from the screen when the interval completes, then the cycle restarts automatically.
- **System tray integration** — closing the window keeps the app running in the tray; right-click the tray icon to reopen or exit.
- **Responsive layout** — the window contents reflow as you resize.

## Tech stack

- **Java 25** (`maven.compiler.release=25`)
- **JavaFX 21.0.2** (`javafx-controls`, `javafx-fxml`) for the UI
- **java.desktop / AWT** for the system tray
- **Maven** for build and run (via the `javafx-maven-plugin`)

## Project structure

```text
screen_break_app/
├── pom.xml                        # Maven build + JavaFX run configuration
└── src/main/
    ├── java/
    │   ├── module-info.java        # Java module declaration
    │   └── com/reminder/
    │       ├── App.java            # Application entry point; scene + system tray
    │       ├── TimerControl.java   # FXML controller — binds UI to the timer
    │       └── ReminderTimer.java  # Controls the JavaFX Timeline behavior; exposes progress
    └── resources/
        ├── com/reminder/
        │   └── timerControl.fxml   # Main window layout
        ├── icon.png
        └── newIcon.png             # System tray icon
```

## Prerequisites

- **JDK 25** or newer on your `PATH`.
- **Maven** — either a standalone install (`mvn`) or via your IDE's bundled Maven / the project's wrapper. JavaFX itself is pulled in automatically as a Maven dependency, so no separate SDK download is needed.

## Build & run

From the project root:

```sh
# Compile
mvn clean compile

# Run the application
mvn clean javafx:run
```

The `javafx:run` goal is preconfigured to launch `com.reminder.App`.

## Usage

1. Launch the app — the main window shows the **Screen Break Reminder Settings** screen.
2. Enter an interval in minutes in the text field.
3. Click **Start the Reminder**. The status changes to *Running* and the progress bar begins filling.
4. When the interval elapses, a **"Look outside for 20 seconds"** reminder appears, then the timer restarts for the next interval.
5. Click **Stop the Reminder** to halt and reset.
6. Closing the window minimizes the app to the system tray. Double-click the tray icon (or use its right-click menu) to reopen it, or choose **Exit** to quit completely.

## How it works

- **`ReminderTimer`** owns a JavaFX `Timeline` that ticks once per second. It exposes two observable properties — `progressProperty()` (0.0 → 1.0) and `timeTextProperty()` (`"3 / 20 minutes"`) — and fires the break `Alert` when an interval completes.
- **`TimerControl`** is a thin FXML controller: in `initialize()` it *binds* the progress bar and label directly to the timer's properties, so the UI stays in sync automatically. The Start/Stop buttons just delegate to `ReminderTimer.start(minutes)` / `stop()`.
- **`App`** loads `timerControl.fxml`, sets up the AWT system tray, and keeps the JVM alive after the window closes via `Platform.setImplicitExit(false)`.

## License

Portions of the FXML scaffolding are derived from the Gluon JavaFX project template (BSD-style license; see the header in `timerControl.fxml`).
