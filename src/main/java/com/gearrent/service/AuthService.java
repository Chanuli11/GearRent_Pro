package com.gearrent.service;
import com.gearrent.dao.UserDAO; import com.gearrent.entity.User; import com.gearrent.util.Session;
public class AuthService {
    private final UserDAO dao = new UserDAO();
    public User login(String username, String password) throws Exception {
        if (username==null || username.isBlank() || password==null || password.isBlank())
            throw new IllegalArgumentException("Username and password are required");
        User u = dao.authenticate(username.trim(), password);
        if (u==null) throw new IllegalArgumentException("Invalid credentials");
        Session.setUser(u);
        return u;
    }
}
