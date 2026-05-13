package com.gearrent.controller;
import com.gearrent.dao.*; import com.gearrent.entity.*;
import com.gearrent.service.*; import com.gearrent.ui.MainApp; import com.gearrent.util.*;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class ReservationController {
    @FXML private ComboBox<Branch> branchBox;
    @FXML private ComboBox<Equipment> equipmentBox;
    @FXML private ComboBox<Customer> customerBox;
    @FXML private DatePicker startDate, endDate;
    @FXML private TableView<Reservation> table;
    @FXML private TableColumn<Reservation,String> cId,cEq,cCust,cFrom,cTo,cStatus;
    private final BranchDAO branchDao=new BranchDAO();
    private final EquipmentDAO eqDao=new EquipmentDAO();
    private final CustomerDAO custDao=new CustomerDAO();
    private final ReservationDAO resDao=new ReservationDAO();
    private final ReservationService resService=new ReservationService();
    private final RentalService rentalService=new RentalService();

    @FXML public void initialize(){
        try {
            branchBox.getItems().setAll(branchDao.findAll());
            equipmentBox.getItems().setAll(eqDao.findAll());
            customerBox.getItems().setAll(custDao.findAll());
        } catch(Exception e){ AlertUtil.error(e.getMessage()); }
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getReservationId())));
        cEq.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEquipmentName()));
        cCust.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCustomerName()));
        cFrom.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getStartDate().toString()));
        cTo.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEndDate().toString()));
        cStatus.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getStatus().name()));
        load();
    }
    private void load(){
        try {
            Integer bid = Session.getUser()!=null && Session.getUser().getBranchId()!=null
                          && Session.getUser().getRole()!=UserRole.ADMIN
                          ? Session.getUser().getBranchId() : null;
            table.setItems(FXCollections.observableArrayList(resDao.findAll(bid)));
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void create(){
        try {
            Reservation r=resService.create(equipmentBox.getValue().getEquipmentId(),
                customerBox.getValue().getCustomerId(),
                branchBox.getValue().getBranchId(),
                startDate.getValue(), endDate.getValue());
            AlertUtil.info("Reservation #"+r.getReservationId()+" created"); load();
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void cancel(){
        Reservation r=table.getSelectionModel().getSelectedItem();
        if (r==null){ AlertUtil.warn("Select a reservation"); return; }
        try { resService.cancel(r.getReservationId()); load(); } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void convert(){
        Reservation r=table.getSelectionModel().getSelectedItem();
        if (r==null){ AlertUtil.warn("Select a reservation"); return; }
        try { Rental rental=rentalService.convertReservationToRental(r.getReservationId());
              AlertUtil.info("Created Rental #"+rental.getRentalId()+" Total: "+rental.getFinalPayable()); load(); }
        catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
