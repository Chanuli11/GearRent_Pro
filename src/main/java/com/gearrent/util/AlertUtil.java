package com.gearrent.util;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class AlertUtil {
    public static void info(String msg){show(AlertType.INFORMATION,"Info",msg);}
    public static void error(String msg){show(AlertType.ERROR,"Error",msg);}
    public static void warn(String msg){show(AlertType.WARNING,"Warning",msg);}
    public static boolean confirm(String msg){
        Alert a=new Alert(AlertType.CONFIRMATION,msg);
        return a.showAndWait().filter(b->b.getButtonData().isDefaultButton()).isPresent();
    }
    private static void show(AlertType t,String title,String msg){
        Alert a=new Alert(t,msg); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}
