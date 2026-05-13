package com.gearrent.entity;
public class Customer {
    private int customerId; private String name, nic, contact, email, address;
    private MembershipType membership;
    public Customer(){}
    public int getCustomerId(){return customerId;} public void setCustomerId(int v){customerId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getNic(){return nic;} public void setNic(String v){nic=v;}
    public String getContact(){return contact;} public void setContact(String v){contact=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public MembershipType getMembership(){return membership;} public void setMembership(MembershipType v){membership=v;}
    @Override public String toString(){return name+" ("+nic+")";}
}
