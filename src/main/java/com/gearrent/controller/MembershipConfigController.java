package com.gearrent.controller;
import com.gearrent.dao.MembershipConfigDAO; import com.gearrent.entity.MembershipType;
import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
import java.math.BigDecimal; import java.util.Map;
public class MembershipConfigController {
    @FXML private ComboBox<MembershipType> levelBox;
    @FXML private TextField discount;
    @FXML private TableView<Map.Entry<MembershipType,BigDecimal>> table;
    @FXML private TableColumn<Map.Entry<MembershipType,BigDecimal>,String> cLevel,cDisc;
    private final MembershipConfigDAO dao=new MembershipConfigDAO();
    @FXML public void initialize(){
        levelBox.getItems().setAll(MembershipType.values());
        cLevel.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getKey().name()));
        cDisc.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getValue().toString()));
        load();
    }
    private void load(){
        try { table.setItems(FXCollections.observableArrayList(dao.findAll().entrySet())); }
        catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void update(){
        try { dao.update(levelBox.getValue(), new BigDecimal(discount.getText())); load(); }
        catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
