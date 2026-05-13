package com.gearrent.controller;
import com.gearrent.entity.User; import com.gearrent.service.AuthService; import com.gearrent.ui.MainApp;
import javafx.fxml.FXML; import javafx.scene.control.*;
public class LoginController {
    @FXML private TextField username; @FXML private PasswordField password; @FXML private Label message;
    private final AuthService authService = new AuthService();
    @FXML private void onLogin() {
        try {
            User u = authService.login(username.getText(), password.getText());
            MainApp.setRoot("Dashboard.fxml", "GearRent Pro - "+u.getRole()+" ["+u.getFullName()+"]");
        } catch (Exception e) { message.setText(e.getMessage()); }
    }
}
