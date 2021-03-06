package eu.poweredbypaella.paellapos.controllers;

import eu.poweredbypaella.paellapos.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PresentationStackController implements Initializable {
    // STACK PANE
    @FXML
    public StackPane stack;

    // PAGES
    @FXML
    public AnchorPane checkoutPage;
    @FXML
    public AnchorPane inventoryManagement;
    @FXML
    public VBox loginPage;
    @FXML
    public AnchorPane managerMenu;
    @FXML
    public AnchorPane orderList;
    @FXML
    public AnchorPane orderPlacements;
    @FXML
    public AnchorPane receipts;
    @FXML
    public AnchorPane reportsPage;

    // PAGES
    @FXML
    public CheckoutPageController checkoutPageController;
    @FXML
    public InventoryManagementController inventoryManagementController;
    @FXML
    public LoginPageController loginPageController;
    @FXML
    public ManagerMenuController managerMenuController;
    @FXML
    public OrderListController orderListController;
    @FXML
    public OrderPlacementsController orderPlacementsController;
    @FXML
    public ReceiptsController receiptsController;
    @FXML
    public ReportsController reportsPageController;

    // DATA
    public int employeeID = 2;

    // CHEAT SHEET REFERENCE
    public CheatSheetController cheatSheetController;


    // Page switching
    public void openCheckoutPage() {
        checkoutPage.toFront();
    }

    public void openInventoryManagementPage() {
        inventoryManagement.toFront();
    }

    public void openLoginPage() {
        loginPage.toFront();
    }

    public void openManagerMenuPage() {
        managerMenu.toFront();
    }

    public void openOrderListPage() {
        orderList.toFront();
    }

    public void openOrderPlacementsPage() {
        orderPlacements.toFront();
    }

    public void openReceiptsPage() {
        receipts.toFront();
    }

    public void openReportsPage() {
        reportsPage.toFront();
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkoutPageController.parent = this;
        inventoryManagementController.parent = this;
        loginPageController.parent = this;
        managerMenuController.parent = this;
        orderListController.parent = this;
        orderPlacementsController.parent = this;
        receiptsController.parent = this;
        reportsPageController.parent = this;
    }
}
