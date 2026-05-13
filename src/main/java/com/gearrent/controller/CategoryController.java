package com.gearrent.controller;
import com.gearrent.dao.CategoryDAO; import com.gearrent.entity.Category;
import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
import java.math.BigDecimal;
public class CategoryController {
    @FXML private TextField name, description, basePrice, weekend, lateFee;
    @FXML private CheckBox active;
    @FXML private TableView<Category> table;
    @FXML private TableColumn<Category,String> cId,cName,cDesc,cFactor,cWeekend,cLate,cActive;
    private final CategoryDAO dao = new CategoryDAO();
    private Category sel;
    @FXML public void initialize(){
        cId.setCellValueFactory(d->new SimpleStringProperty(String.valueOf(d.getValue().getCategoryId())));
        cName.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getName()));
        cDesc.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getDescription()));
        cFactor.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getBasePriceFactor().toString()));
        cWeekend.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getWeekendMultiplier().toString()));
        cLate.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getLateFeePerDay().toString()));
        cActive.setCellValueFactory(d->new SimpleStringProperty(d.getValue().isActive()?"Yes":"No"));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
            if (b!=null){ sel=b; name.setText(b.getName()); description.setText(b.getDescription());
                basePrice.setText(b.getBasePriceFactor().toString());
                weekend.setText(b.getWeekendMultiplier().toString());
                lateFee.setText(b.getLateFeePerDay().toString()); active.setSelected(b.isActive()); }
        });
        load();
    }
    private void load(){
        try { table.setItems(FXCollections.observableArrayList(dao.findAll())); }
        catch(Exception e){ AlertUtil.error(e.getMessage()); }
    }
    private Category build(){
        Category c=new Category(); c.setName(name.getText()); c.setDescription(description.getText());
        c.setBasePriceFactor(new BigDecimal(basePrice.getText()));
        c.setWeekendMultiplier(new BigDecimal(weekend.getText()));
        c.setLateFeePerDay(new BigDecimal(lateFee.getText()));
        c.setActive(active.isSelected()); return c;
    }
    @FXML private void save(){ try { dao.save(build()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void update(){ if(sel==null){AlertUtil.warn("Select a row"); return;}
        try { Category c=build(); c.setCategoryId(sel.getCategoryId()); dao.update(c); clear(); load(); }
        catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void delete(){ if(sel==null){AlertUtil.warn("Select a row"); return;}
        try { dao.delete(sel.getCategoryId()); clear(); load(); } catch(Exception e){AlertUtil.error(e.getMessage());} }
    @FXML private void clear(){ sel=null; name.clear(); description.clear(); basePrice.clear(); weekend.clear(); lateFee.clear(); active.setSelected(true); }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
