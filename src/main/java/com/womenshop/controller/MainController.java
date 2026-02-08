package com.womenshop.controller;

import com.womenshop.dao.ProductDAO;
import com.womenshop.dao.FinanceDAO;
import com.womenshop.model.*;
import com.womenshop.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML private Label lblCapital, lblIncome, lblCost;
    @FXML private Button btnTheme;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colID;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Double> colDiscount;
    @FXML private TableColumn<Product, Integer> colSold;
    @FXML private TableColumn<Product, Integer> colBought;

    private ProductDAO productDAO = new ProductDAO();
    private FinanceDAO financeDAO = new FinanceDAO();
    private ObservableList<Product> masterList = FXCollections.observableArrayList();
    private boolean darkMode = false;
    private static final String LIGHT_CSS = "/css/style.css";
    private static final String DARK_CSS = "/css/style-dark.css";

    @FXML
    public void initialize() {
        // Setup table columns
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("nbItems"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountPrice"));
        colSold.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
        colBought.setCellValueFactory(new PropertyValueFactory<>("totalBought"));

        filterCombo.getItems().addAll("All", "Clothes", "Shoes", "Accessory");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> applyFilter());

        loadData();
    }

    private void loadData() {
        try {
            financeDAO.loadFinances();
            List<Product> products = productDAO.getAllProducts();
            masterList.setAll(products);
            productTable.setItems(masterList);
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showMessage("Error", "Could not load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStats() {
        lblCapital.setText(String.format("Profit: %.2f €", Product.computeCurrentCapital()));
        lblIncome.setText(String.format("Income: %.2f €", Product.income));
        lblCost.setText(String.format("Cost: %.2f €", Product.cost));
        
        // Sync finances with DB
        try {
            financeDAO.updateFinances(Product.income, Product.cost);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilter() {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("All")) {
            productTable.setItems(masterList);
        } else {
            ObservableList<Product> filtered = FXCollections.observableArrayList();
            for (Product p : masterList) {
                if ((filter.equals("Clothes") && p instanceof Clothes) ||
                    (filter.equals("Shoes") && p instanceof Shoes) ||
                    (filter.equals("Accessory") && p instanceof Accessory)) {
                    filtered.add(p);
                }
            }
            productTable.setItems(filtered);
        }
    }

    @FXML
    private void handleSell() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Sell Items");
        dialog.setHeaderText("Sell " + selected.getName());
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                selected.sell(qty); // Logic inside Model
                productDAO.updateProduct(selected); // Logic inside DAO
                productTable.refresh();
                updateStats();
            } catch (IllegalArgumentException e) {
                AlertHelper.showMessage("Error", e.getMessage(), Alert.AlertType.WARNING);
            } catch (SQLException e) {
                AlertHelper.showMessage("DB Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handlePurchase() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Purchase Items");
        dialog.setHeaderText("Restock " + selected.getName());
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                selected.purchase(qty);
                productDAO.updateProduct(selected);
                productTable.refresh();
                updateStats();
            } catch (IllegalArgumentException e) {
                AlertHelper.showMessage("Budget Error", e.getMessage(), Alert.AlertType.WARNING);
            } catch (SQLException e) {
                AlertHelper.showMessage("DB Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleApplyDiscount() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showMessage("Info", "Please select a product first.", Alert.AlertType.WARNING);
            return;
        }
        selected.applyDiscount();
        try { productDAO.updateProduct(selected); } catch (SQLException e) {}
        productTable.refresh();
    }

    @FXML
    private void handleStopDiscount() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showMessage("Info", "Please select a product first.", Alert.AlertType.WARNING);
            return;
        }
        selected.unApplyDiscount();
        try { productDAO.updateProduct(selected); } catch (SQLException e) {}
        productTable.refresh();
    }

    @FXML
    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                productDAO.deleteProduct(selected.getId(), selected.getNbItems());
                masterList.remove(selected);
            } catch (IllegalArgumentException | SQLException e) {
                AlertHelper.showMessage("Delete Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/product_dialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Product");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene dialogScene = new Scene(loader.load());
            String css = darkMode ? DARK_CSS : LIGHT_CSS;
            dialogScene.getStylesheets().add(getClass().getResource(css).toExternalForm());
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();
            loadData();
        } catch (IOException e) {
            AlertHelper.showMessage("Error", "Could not open dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleToggleTheme() {
        Scene scene = btnTheme.getScene();
        scene.getStylesheets().clear();
        darkMode = !darkMode;
        if (darkMode) {
            scene.getStylesheets().add(getClass().getResource(DARK_CSS).toExternalForm());
            btnTheme.setText("Light Mode");
        } else {
            scene.getStylesheets().add(getClass().getResource(LIGHT_CSS).toExternalForm());
            btnTheme.setText("Dark Mode");
        }
    }
}