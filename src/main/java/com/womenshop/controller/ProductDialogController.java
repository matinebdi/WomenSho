package com.womenshop.controller;

import com.womenshop.dao.ProductDAO;
import com.womenshop.model.*;
import com.womenshop.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductDialogController {
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField txtName, txtPurchasePrice, txtSalePrice, txtSize, txtStock;
    @FXML private Label lblSize;

    private ProductDAO dao = new ProductDAO();

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("Clothes", "Shoes", "Accessory");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.setOnAction(e -> {
            boolean isAccessory = "Accessory".equals(typeCombo.getValue());
            lblSize.setVisible(!isAccessory);
            txtSize.setVisible(!isAccessory);
        });
    }

    @FXML
    private void handleSave() {
        try {
            String name = txtName.getText();
            double pPrice = Double.parseDouble(txtPurchasePrice.getText());
            double sPrice = Double.parseDouble(txtSalePrice.getText());
            String type = typeCombo.getValue();
            
            Product p;
            if (type.equals("Clothes")) {
                p = new Clothes(name, pPrice, sPrice, Integer.parseInt(txtSize.getText()));
            } else if (type.equals("Shoes")) {
                p = new Shoes(name, pPrice, sPrice, Integer.parseInt(txtSize.getText()));
            } else {
                p = new Accessory(name, pPrice, sPrice);
            }

            String stockText = txtStock.getText().trim();
            if (!stockText.isEmpty()) {
                int stock = Integer.parseInt(stockText);
                if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
                p.setNbItems(stock);
            }

            dao.addProduct(p);
            closeStage();
        } catch (Exception e) {
            AlertHelper.showMessage("Validation Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) txtName.getScene().getWindow()).close(); }
}