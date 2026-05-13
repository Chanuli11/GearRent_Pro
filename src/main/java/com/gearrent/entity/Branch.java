package com.gearrent.entity;
public class Branch {
    private int branchId;
    private String branchCode, name, address, contact;
    public Branch() {}
    public Branch(int id, String code, String name, String address, String contact){
        this.branchId=id; this.branchCode=code; this.name=name; this.address=address; this.contact=contact;
    }
    public int getBranchId(){return branchId;} public void setBranchId(int v){branchId=v;}
    public String getBranchCode(){return branchCode;} public void setBranchCode(String v){branchCode=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getContact(){return contact;} public void setContact(String v){contact=v;}
    @Override public String toString(){return name;}
}
