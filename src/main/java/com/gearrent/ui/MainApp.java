package com.gearrent.ui;
import javafx.application.Application; import javafx.fxml.FXMLLoader;
import javafx.scene.Scene; import javafx.stage.Stage;
public class MainApp extends Application {
    private static Stage primary;
    @Override public void start(Stage stage) throws Exception {
        primary = stage;
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/ui/LoginScreen.fxml")));
        stage.setTitle("GearRent Pro - Login");
        stage.setScene(scene); stage.show();
    }
    public static Stage getPrimary(){ return primary; }
    public static void setRoot(String fxml, String title) throws Exception {
        Scene s = new Scene(FXMLLoader.load(MainApp.class.getResource("/ui/"+fxml)));
        primary.setTitle(title); primary.setScene(s);
    }
    public static void main(String[] args){ launch(args); }
}
