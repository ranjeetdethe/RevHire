package com.revhire;

public class Main {
    public static void main(String[] args) {
        com.revhire.config.DatabaseInitializer.initialize();
        com.revhire.config.SchemaFixer.fixSchema();
        new com.revhire.ui.MainMenu().start();
    }
}
