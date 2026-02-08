package com.womenshop.util;

import javafx.scene.control.Alert;

public class AlertHelper {
    public static void showMessage(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}