package com.team1.airline;

import com.team1.airline.gui.MainApp;
import com.team1.airline.dao.impl.DataManager;
import javax.swing.SwingUtilities;


public class Main {
    public static void main(String[] args) {
        DataManager.getInstance().loadAllData();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataManager.getInstance().saveAllData();
        }));

        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}

