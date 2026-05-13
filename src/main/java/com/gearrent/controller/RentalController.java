package com.gearrent.controller;
import com.gearrent.dao.*; import com.gearrent.entity.*;
import com.gearrent.service.*; import com.gearrent.ui.MainApp; import com.gearrent.util.*;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class RentalController {
    @FXML private ComboBox<Branch> branchBox;
    @FXML private ComboBox<Equipment> equipmentBox;
    @FXML private ComboBox<Customer> customerBox;
    @FXML private ComboBox<RentalStatus> statusFilter;
    @FXML private DatePicker startDate, endDate;
    @FXML private Label quoteLabel;
    @FXML private TableView<Rental> table;
    @FXML private TableColumn<Rental,String> cId,cEq,cCust,cBranch,cFrom,cTo,cAmt,cPay,cStatus;
    private final RentalDAO dao=new RentalDAO();
    private final RentalService service=new RentalService();
    private final BranchDAO branchDao=new BranchDAO();
    private final EquipmentDAO eqDao=new EquipmentDAO();
    private final CustomerDAO custDao=new CustomerDAO();

    @FXML public void initialize(){
        try {
            branchBox.getItems().setAll(branchDao.findAll());
            equipmentBox.getItems().setAll(eqDao.findAll());
            customerBox.getItems().setAll(custDao.findAll());
        } catch(Exception e){AlertUtil.error(e.getMessage());}
        statusFilter.getItems().setAll(RentalStatus.values());
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getRentalId())));
        cEq.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEquipmentName()));
        cCust.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCustomerName()));
        cBranch.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBranchName()));
        cFrom.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getStartDate().toString()));
        cTo.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEndDate().toString()));
        cAmt.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getFinalPayable().toString()));
        cPay.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getPaymentStatus().name()));
        cStatus.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getRentalStatus().name()));
        try { dao.markOverdueRentals(); } catch(Exception ignored){}
        load();
    }
    @FXML private void load(){
        try {
            Integer bid = Session.getUser()!=null && Session.getUser().getRole()!=UserRole.ADMIN
                          ? Session.getUser().getBranchId() : null;
            table.setItems(FXCollections.observableArrayList(dao.findAll(bid, statusFilter.getValue())));
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void quote(){
        try {
            RentalPriceService.Quote q=service.quote(equipmentBox.getValue().getEquipmentId(),
                customerBox.getValue().getCustomerId(), startDate.getValue(), endDate.getValue());
            quoteLabel.setText("Days "+q.days+" | Rental "+q.rentalAmount+" | LongDisc "+q.longRentalDisc+
                " | MemDisc "+q.membershipDisc+" | Final "+q.finalPayable+" | Deposit "+q.securityDeposit);
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void create(){
        try {
            Rental r=service.createRental(equipmentBox.getValue().getEquipmentId(),
                customerBox.getValue().getCustomerId(),
                branchBox.getValue().getBranchId(),
                startDate.getValue(), endDate.getValue());
            AlertUtil.info("Rental #"+r.getRentalId()+" created. Pay: "+r.getFinalPayable()+" Deposit: "+r.getSecurityDeposit()); load();
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
