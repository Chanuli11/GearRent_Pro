package com.gearrent.controller;
import com.gearrent.dao.RentalDAO; import com.gearrent.entity.*;
import com.gearrent.ui.MainApp; import com.gearrent.util.*;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
import java.time.LocalDate; import java.time.temporal.ChronoUnit;
public class OverdueController {
    @FXML private TableView<Rental> table;
    @FXML private TableColumn<Rental,String> cId,cCust,cBranch,cEq,cDue,cDays;
    private final RentalDAO dao=new RentalDAO();
    @FXML public void initialize(){
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getRentalId())));
        cCust.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCustomerName()));
        cBranch.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBranchName()));
        cEq.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEquipmentName()));
        cDue.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEndDate().toString()));
        cDays.setCellValueFactory(d->new SimpleStringProperty(
            String.valueOf(ChronoUnit.DAYS.between(d.getValue().getEndDate(), LocalDate.now()))));
        try {
            dao.markOverdueRentals();
            Integer bid = Session.getUser()!=null && Session.getUser().getRole()!=UserRole.ADMIN
                          ? Session.getUser().getBranchId() : null;
            table.setItems(FXCollections.observableArrayList(dao.findOverdue(bid)));
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
