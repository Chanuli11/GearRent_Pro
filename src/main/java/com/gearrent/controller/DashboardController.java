package com.gearrent.controller;
import com.gearrent.entity.*; import com.gearrent.ui.MainApp; import com.gearrent.util.*;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class DashboardController {
    @FXML private Label userInfo, welcomeMsg;
    @FXML private Button btnBranches, btnCategories, btnMembership;
    @FXML public void initialize() {
        User u = Session.getUser();
        if (u==null) return;
        userInfo.setText(u.getFullName()+" ("+u.getRole()+")");
        welcomeMsg.setText("Logged in as "+u.getRole());
        boolean admin = u.getRole()==UserRole.ADMIN;
        btnBranches.setDisable(!admin);
        btnMembership.setDisable(!admin);
        // Categories: Admin & Branch Manager
        btnCategories.setDisable(u.getRole()==UserRole.STAFF);
    }
    @FXML private void onLogout() throws Exception { Session.logout(); MainApp.setRoot("LoginScreen.fxml","GearRent Pro - Login"); }
    @FXML private void openBranches()      { open("BranchManagement.fxml","Branches"); }
    @FXML private void openCategories()    { open("CategoryManagement.fxml","Categories"); }
    @FXML private void openEquipment()     { open("EquipmentManagement.fxml","Equipment"); }
    @FXML private void openCustomers()     { open("CustomerManagement.fxml","Customers"); }
    @FXML private void openReservations()  { open("ReservationManagement.fxml","Reservations"); }
    @FXML private void openRentals()       { open("RentalManagement.fxml","Rentals"); }
    @FXML private void openReturns()       { open("ReturnManagement.fxml","Returns"); }
    @FXML private void openOverdue()       { open("OverdueRentals.fxml","Overdue Rentals"); }
    @FXML private void openReports()       { open("Reports.fxml","Reports"); }
    @FXML private void openMembership()    { open("MembershipConfig.fxml","Membership Config"); }
    private void open(String fxml, String title){
        try { MainApp.setRoot(fxml, "GearRent Pro - "+title); }
        catch (Exception e) { AlertUtil.error("Cannot open "+title+":\n"+e.getMessage()); }
    }
}
