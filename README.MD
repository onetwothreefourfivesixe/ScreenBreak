# Screen Break

ScreenBreak is a small JavaFX based desktop app that reminds you to rest your eyes at a fixed interval. Enter how often you want to be reminded, start the timer, and the app pops up a break reminder each time the interval elapses — while a progress bar shows how far you are into the current interval. It lives in the system tray so it can keep running quietly in the background.

## Features

- **Configurable interval** — set the reminder frequency (in minutes) from the UI.
- **Live progress** — a progress bar and `elapsed / total minutes` label track the current interval second by second.
- **Break reminders** — a dialog prompts you to look away from the screen when the interval completes, then the cycle restarts automatically.
- **Custom sound alerts** — optionally play a sound when a break fires. Pick any `.wav`, `.mp3`, or `.aac` file; it's copied into the app's own storage so it keeps working even if you move or delete the original.
- **Volume control** — a slider adjusts the alert volume live.
- **Persistent settings** — your interval and sound preferences are saved to a per-user config folder and restored on the next launch.
- **System tray integration** — closing the window keeps the app running in the tray; right-click the tray icon to reopen or exit.
- **Responsive layout** — the window contents reflow as you resize.

## Installation

Prebuilt, self-contained packages (a trimmed Java runtime is bundled, so **no separate Java install is required**) are attached to each [GitHub Release](../../releases). Grab the download for your platform from the latest release's **Assets** list.

### Windows

1. Download **`ScreenBreak-windows.zip`**.
2. Right-click the zip → **Extract All…** to a folder you'll keep (e.g. `C:\Program Files\ScreenBreak` or your user folder).
3. Open the extracted `ScreenBreak` folder and double-click **`ScreenBreak.exe`**.
4. If SmartScreen warns about an unsigned app, click **More info → Run anyway** (this is expected for a beta that isn't code-signed yet).

> Prefer a traditional installer with a Start-menu entry? A Windows `.msi` can be built from source with `mvn clean package -P installer` (requires the [WiX Toolset v3](https://wixtoolset.org/)).

### Linux

1. Download **`ScreenBreak-linux.tar.gz`**.
2. Extract it: `tar -xzf ScreenBreak-linux.tar.gz`
3. Launch it: `./ScreenBreak/bin/ScreenBreak`

### macOS

A prebuilt macOS package isn't published yet. Build and run it from source using the [Build & run](#build--run) steps below.

## Start ScreenBreak automatically on login

ScreenBreak is most useful when it's always running, so it can nudge you throughout the day. Here's how to have your OS launch it at startup. (It minimizes to the system tray, so it stays out of your way.)

### Windows (startup)

1. Press <kbd>Win</kbd> + <kbd>R</kbd>, type **`shell:startup`**, and press <kbd>Enter</kbd> — this opens your Startup folder.
2. Right-click **`ScreenBreak.exe`** (from where you extracted it) → **Show more options → Send to → Desktop (create shortcut)**, or hold <kbd>Alt</kbd> and drag it to create a shortcut.
3. Move that shortcut into the Startup folder from step 1.

ScreenBreak will now launch each time you log in. (Remove the shortcut from that folder to stop auto-starting.)

### macOS (startup)

1. Open **System Settings → General → Login Items & Extensions**.
2. Under **Open at Login**, click the **+** button and select **ScreenBreak.app**.

### Linux (startup)

Most desktop environments run any `.desktop` file placed in `~/.config/autostart/` at login. Create one pointing at the launcher:

```sh
mkdir -p ~/.config/autostart
cat > ~/.config/autostart/screenbreak.desktop <<EOF
[Desktop Entry]
Type=Application
Name=ScreenBreak
Exec=/full/path/to/ScreenBreak/bin/ScreenBreak
X-GNOME-Autostart-enabled=true
EOF
```

Replace `/full/path/to/ScreenBreak` with the folder where you extracted the app. (GNOME users can alternatively use the **Startup Applications** tool if installed.)

## Tech stack

- **Java 25** (`maven.compiler.release=25`)
- **JavaFX 21.0.2** (`javafx-controls`, `javafx-fxml`, `javafx-media`) for the UI and audio playback
- **java.desktop / AWT** for the system tray
- **`java.util.Properties`** for persisting user settings to disk
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
    │       ├── TimerControl.java   # FXML controller — binds UI to the timer, handles sound selection
    │       ├── ReminderTimer.java  # Controls the JavaFX Timeline behavior; exposes progress, plays the alert
    │       └── AppData.java        # Per-user storage: config file + imported sound files
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
3. *(Optional)* Tick the **sound alert** checkbox. The first time, a file picker opens so you can choose a `.wav`, `.mp3`, or `.aac` sound; the file is copied into the app's storage and reused automatically afterwards. Use the **volume slider** to set how loud it plays.
4. Click **Start the Reminder**. The status changes to *Running* and the progress bar begins filling.
5. When the interval elapses, a **"Look outside for 20 seconds"** reminder appears (with your sound, if enabled), then the timer restarts for the next interval.
6. Click **Stop the Reminder** to halt and reset.
7. Closing the window minimizes the app to the system tray. Double-click the tray icon (or use its right-click menu) to reopen it, or choose **Exit** to quit completely.

Your interval and sound settings are remembered between runs.

## Where settings are stored

The app keeps its config and imported sounds in a per-user folder named `ScreenBreak`:

- **Windows** — `%APPDATA%\ScreenBreak` (e.g. `C:\Users\<you>\AppData\Roaming\ScreenBreak`)
- **macOS** — `~/Library/Application Support/ScreenBreak`
- **Other** — `~/ScreenBreak`

```text
ScreenBreak/
├── config.properties   # saved interval + sound preferences
└── sounds/             # the imported alert sound
```
