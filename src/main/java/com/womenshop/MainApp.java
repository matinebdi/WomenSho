package com.womenshop;

import com.womenshop.dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseInitializer.initialize();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setTitle("WomenShop - Stock Management");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}