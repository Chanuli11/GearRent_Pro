package com.gearrent.controller;
import com.gearrent.dao.CustomerDAO; import com.gearrent.entity.*;
import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class CustomerController {
    @FXML private TextField name, nic, contact, email, address;
    @FXML private ComboBox<MembershipType> membership;
    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer,String> cId,cName,cNic,cContact,cEmail,cMember,cDeposit;
    private final CustomerDAO dao = new CustomerDAO();
    private Customer sel;
    @FXML public void initialize(){
        membership.getItems().setAll(MembershipType.values());
        membership.setValue(MembershipType.REGULAR);
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getCustomerId())));
        cName.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getName()));
        cNic.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getNic()));
        cContact.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getContact()));
        cEmail.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEmail()));
        cMember.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getMembership().name()));
        cDeposit.setCellValueFactory(d->{
            try { return new SimpleStringProperty(dao.totalActiveDeposits(d.getValue().getCustomerId()).toString()); }
            catch(Exception e){ return new SimpleStringProperty("?"); }
        });
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
            if (b!=null){ sel=b; name.setText(b.getName()); nic.setText(b.getNic());
                contact.setText(b.getContact()); email.setText(b.getEmail());
                address.setText(b.getAddress()); membership.setValue(b.getMembership()); }
        });
        load();
    }
    private void load(){ try { table.setItems(FXCollections.observableArrayList(dao.findAll())); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    private Customer build(){
        Customer c=new Customer(); c.setName(name.getText()); c.setNic(nic.getText());
        c.setContact(contact.getText()); c.setEmail(email.getText());
        c.setAddress(address.getText()); c.setMembership(membership.getValue()); return c;
    }
    @FXML private void save(){ try { dao.save(build()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void update(){ if(sel==null){AlertUtil.warn("Select a row"); return;}
        try { Customer c=build(); c.setCustomerId(sel.getCustomerId()); dao.update(c); clear(); load(); }
        catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void delete(){ if(sel==null){AlertUtil.warn("Select a row"); return;}
        try { dao.delete(sel.getCustomerId()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void clear(){ sel=null; name.clear(); nic.clear(); contact.clear(); email.clear(); address.clear(); membership.setValue(MembershipType.REGULAR); }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
