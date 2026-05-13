package com.gearrent.controller;
import com.gearrent.dao.BranchDAO; import com.gearrent.entity.Branch;
import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class BranchController {
    @FXML private TextField code, name, address, contact;
    @FXML private TableView<Branch> table;
    @FXML private TableColumn<Branch,String> cId,cCode,cName,cAddress,cContact;
    private final BranchDAO dao = new BranchDAO();
    private Branch selected;
    @FXML public void initialize(){
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getBranchId())));
        cCode.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBranchCode()));
        cName.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getName()));
        cAddress.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getAddress()));
        cContact.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getContact()));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
            if (b!=null){ selected=b; code.setText(b.getBranchCode()); name.setText(b.getName());
                address.setText(b.getAddress()); contact.setText(b.getContact()); }
        });
        load();
    }
    private void load(){
        try { table.setItems(FXCollections.observableArrayList(dao.findAll())); }
        catch(Exception e){ AlertUtil.error(e.getMessage()); }
    }
    @FXML private void save(){
        try { Branch b=new Branch(0,code.getText(),name.getText(),address.getText(),contact.getText()); dao.save(b); clear(); load(); }
        catch(Exception e){ AlertUtil.error(e.getMessage()); }
    }
    @FXML private void update(){
        if (selected==null){ AlertUtil.warn("Select a row"); return; }
        try { selected.setBranchCode(code.getText()); selected.setName(name.getText());
              selected.setAddress(address.getText()); selected.setContact(contact.getText());
              dao.update(selected); clear(); load(); }
        catch(Exception e){ AlertUtil.error(e.getMessage()); }
    }
    @FXML private void delete(){
        if (selected==null){ AlertUtil.warn("Select a row"); return; }
        try { dao.delete(selected.getBranchId()); clear(); load(); }
        catch(Exception e){ AlertUtil.error(e.getMessage()); }
    }
    @FXML private void clear(){ selected=null; code.clear(); name.clear(); address.clear(); contact.clear(); }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
