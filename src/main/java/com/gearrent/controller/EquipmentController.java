package com.gearrent.controller;
import com.gearrent.dao.*; import com.gearrent.entity.*;
import com.gearrent.ui.MainApp; import com.gearrent.util.*;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
import java.math.BigDecimal;
public class EquipmentController {
    @FXML private ComboBox<Branch> branchBox, fBranch;
    @FXML private ComboBox<Category> categoryBox, fCategory;
    @FXML private ComboBox<EquipmentStatus> statusBox, fStatus;
    @FXML private TextField brand, model, year, price, deposit, fText;
    @FXML private TableView<Equipment> table;
    @FXML private TableColumn<Equipment,String> cId,cCat,cBranch,cBrand,cModel,cYear,cPrice,cDep,cStatus;
    private final EquipmentDAO dao = new EquipmentDAO();
    private final BranchDAO branchDao = new BranchDAO();
    private final CategoryDAO catDao = new CategoryDAO();
    private Equipment sel;

    @FXML public void initialize(){
        try {
            branchBox.getItems().setAll(branchDao.findAll());
            fBranch.getItems().setAll(branchDao.findAll());
            categoryBox.getItems().setAll(catDao.findAll());
            fCategory.getItems().setAll(catDao.findAll());
        } catch(Exception e){ AlertUtil.error(e.getMessage()); }
        statusBox.getItems().setAll(EquipmentStatus.values());
        fStatus.getItems().setAll(EquipmentStatus.values());
        statusBox.setValue(EquipmentStatus.AVAILABLE);

        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getEquipmentId())));
        cCat.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCategoryName()));
        cBranch.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBranchName()));
        cBrand.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBrand()));
        cModel.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getModel()));
        cYear.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getPurchaseYear())));
        cPrice.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getDailyBasePrice().toString()));
        cDep.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getSecurityDeposit().toString()));
        cStatus.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getStatus().name()));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
            if (b!=null){ sel=b;
                branchBox.getSelectionModel().select(branchBox.getItems().stream().filter(x->x.getBranchId()==b.getBranchId()).findFirst().orElse(null));
                categoryBox.getSelectionModel().select(categoryBox.getItems().stream().filter(x->x.getCategoryId()==b.getCategoryId()).findFirst().orElse(null));
                brand.setText(b.getBrand()); model.setText(b.getModel());
                year.setText(String.valueOf(b.getPurchaseYear()));
                price.setText(b.getDailyBasePrice().toString());
                deposit.setText(b.getSecurityDeposit().toString());
                statusBox.setValue(b.getStatus());
            }
        });
        load();
    }
    private void load(){ try { table.setItems(FXCollections.observableArrayList(dao.findAll())); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    private Equipment build(){
        Equipment e=new Equipment();
        e.setBranchId(branchBox.getValue().getBranchId());
        e.setCategoryId(categoryBox.getValue().getCategoryId());
        e.setBrand(brand.getText()); e.setModel(model.getText());
        e.setPurchaseYear(Integer.parseInt(year.getText()));
        e.setDailyBasePrice(new BigDecimal(price.getText()));
        e.setSecurityDeposit(new BigDecimal(deposit.getText()));
        e.setStatus(statusBox.getValue()); return e;
    }
    @FXML private void save(){ try { dao.save(build()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void update(){ if(sel==null){AlertUtil.warn("Select"); return;}
        try { Equipment e=build(); e.setEquipmentId(sel.getEquipmentId()); dao.update(e); clear(); load(); }
        catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void delete(){ if(sel==null){AlertUtil.warn("Select"); return;}
        try { dao.delete(sel.getEquipmentId()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void clear(){ sel=null; brand.clear(); model.clear(); year.clear(); price.clear(); deposit.clear(); }
    @FXML private void search(){
        try {
            Integer bId = fBranch.getValue()==null?null:fBranch.getValue().getBranchId();
            Integer cId = fCategory.getValue()==null?null:fCategory.getValue().getCategoryId();
            table.setItems(FXCollections.observableArrayList(dao.search(bId,cId,fStatus.getValue(),fText.getText())));
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
