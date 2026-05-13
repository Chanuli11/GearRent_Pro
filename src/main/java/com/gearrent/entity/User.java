package com.gearrent.entity;
public class User {
    private int userId; private String username, password, fullName;
    private UserRole role; private Integer branchId;
    public User() {}
    public int getUserId(){return userId;} public void setUserId(int v){userId=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public UserRole getRole(){return role;} public void setRole(UserRole v){role=v;}
    public Integer getBranchId(){return branchId;} public void setBranchId(Integer v){branchId=v;}
}
