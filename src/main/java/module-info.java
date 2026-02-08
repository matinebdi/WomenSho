module com.womenshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.womenshop;
    opens com.womenshop.controller;
    opens com.womenshop.model;
    opens com.womenshop.dao;
    opens com.womenshop.util;

    exports com.womenshop;
}
