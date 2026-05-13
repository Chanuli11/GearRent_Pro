package com.gearrent.controller;
import com.gearrent.dao.BranchDAO; import com.gearrent.entity.Branch;
import com.gearrent.service.ReportService; import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty; import javafx.collections.FXCollections;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class ReportController {
    @FXML private DatePicker from, to;
    @FXML private ComboBox<Branch> branchBox;
    @FXML private Label title;
    @FXML private TableView<Object> table;
    private final ReportService svc=new ReportService();
    @FXML public void initialize(){
        try { branchBox.getItems().setAll(new BranchDAO().findAll()); } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void branchReport(){
        try {
            var rows = svc.branchRevenue(from.getValue(), to.getValue());
            table.getColumns().clear();
            addCol("Branch", r->((ReportService.BranchRevenueRow)r).branch);
            addCol("Rentals", r->String.valueOf(((ReportService.BranchRevenueRow)r).rentals));
            addCol("Income", r->((ReportService.BranchRevenueRow)r).income.toString());
            addCol("Late Fees", r->((ReportService.BranchRevenueRow)r).lateFees.toString());
            addCol("Damage", r->((ReportService.BranchRevenueRow)r).damageCharges.toString());
            table.setItems(FXCollections.observableArrayList(rows));
            title.setText("Branch-wise Revenue Report ("+from.getValue()+" to "+to.getValue()+")");
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void utilReport(){
        try {
            if (branchBox.getValue()==null){ AlertUtil.warn("Pick a branch"); return; }
            var rows = svc.equipmentUtilization(branchBox.getValue().getBranchId(), from.getValue(), to.getValue());
            table.getColumns().clear();
            addCol("Equipment", r->((ReportService.UtilizationRow)r).equipment);
            addCol("Days Rented", r->String.valueOf(((ReportService.UtilizationRow)r).daysRented));
            addCol("Range Days", r->String.valueOf(((ReportService.UtilizationRow)r).daysInRange));
            addCol("Utilization %", r->String.format("%.2f", ((ReportService.UtilizationRow)r).pct));
            table.setItems(FXCollections.observableArrayList(rows));
            title.setText("Equipment Utilization - "+branchBox.getValue().getName());
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @SuppressWarnings({"rawtypes","unchecked"})
    private void addCol(String name, java.util.function.Function<Object,String> f){
        TableColumn col=new TableColumn(name);
        col.setCellValueFactory(d->new SimpleStringProperty(f.apply(((TableColumn.CellDataFeatures)d).getValue())));
        col.setPrefWidth(150);
        table.getColumns().add(col);
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
