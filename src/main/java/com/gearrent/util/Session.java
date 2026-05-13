package com.gearrent.util;
import com.gearrent.entity.User;
public class Session {
    private static User currentUser;
    public static User getUser(){return currentUser;}
    public static void setUser(User u){currentUser=u;}
    public static void logout(){currentUser=null;}
}
