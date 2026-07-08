package com.reminder;

/**
 * Plain entry point for the shaded (fat) jar.
 *
 * When the Main-Class extends javafx.application.Application, JavaFX's launcher
 * insists the runtime be present as modules on the module path and aborts with
 * "JavaFX runtime components are missing" if it isn't. Launching through a class
 * that does NOT extend Application bypasses that check, so the fat jar — which
 * carries JavaFX on the classpath — runs correctly.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
