package com.gearrent.controller;
import com.gearrent.dao.RentalDAO; import com.gearrent.entity.Rental;
import com.gearrent.service.ReturnService; import com.gearrent.ui.MainApp; import com.gearrent.util.AlertUtil;
import javafx.fxml.FXML; import javafx.scene.control.*;
import java.math.BigDecimal; import java.time.LocalDate;
public class ReturnController {
    @FXML private TextField rentalId, damageCharge, damageNotes;
    @FXML private DatePicker actualReturn;
    @FXML private CheckBox damaged;
    @FXML private Label info, result;
    private final RentalDAO dao=new RentalDAO();
    private final ReturnService service=new ReturnService();
    @FXML private void loadRental(){
        try {
            Rental r=dao.findById(Integer.parseInt(rentalId.getText()));
            if (r==null){ AlertUtil.error("Not found"); return; }
            info.setText("Customer: "+r.getCustomerName()+" | Eq: "+r.getEquipmentName()+
                " | Period: "+r.getStartDate()+" to "+r.getEndDate()+" | Deposit: "+r.getSecurityDeposit());
            actualReturn.setValue(LocalDate.now());
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void process(){
        try {
            BigDecimal dc = damageCharge.getText().isBlank()? BigDecimal.ZERO : new BigDecimal(damageCharge.getText());
            ReturnService.Settlement s=service.processReturn(Integer.parseInt(rentalId.getText()),
                actualReturn.getValue(), dc, damageNotes.getText(), damaged.isSelected());
            result.setText("Late days: "+s.lateDays+" | Late fee: "+s.lateFee+" | Damage: "+s.damageCharge+
                " | Total charges: "+s.totalCharges+" | Refund: "+s.refundToCustomer+" | Extra payable: "+s.extraPayable);
        } catch(Exception e){AlertUtil.error(e.getMessage());}
    }
    @FXML private void back() throws Exception { MainApp.setRoot("Dashboard.fxml","GearRent Pro - Dashboard"); }
}
