package com.gearrent.entity;
import java.math.BigDecimal;
public class Equipment {
    private int equipmentId, categoryId, branchId, purchaseYear;
    private String brand, model, categoryName, branchName;
    private BigDecimal dailyBasePrice, securityDeposit;
    private EquipmentStatus status;
    public Equipment(){}
    public int getEquipmentId(){return equipmentId;} public void setEquipmentId(int v){equipmentId=v;}
    public int getCategoryId(){return categoryId;} public void setCategoryId(int v){categoryId=v;}
    public int getBranchId(){return branchId;} public void setBranchId(int v){branchId=v;}
    public int getPurchaseYear(){return purchaseYear;} public void setPurchaseYear(int v){purchaseYear=v;}
    public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
    public String getModel(){return model;} public void setModel(String v){model=v;}
    public String getCategoryName(){return categoryName;} public void setCategoryName(String v){categoryName=v;}
    public String getBranchName(){return branchName;} public void setBranchName(String v){branchName=v;}
    public BigDecimal getDailyBasePrice(){return dailyBasePrice;} public void setDailyBasePrice(BigDecimal v){dailyBasePrice=v;}
    public BigDecimal getSecurityDeposit(){return securityDeposit;} public void setSecurityDeposit(BigDecimal v){securityDeposit=v;}
    public EquipmentStatus getStatus(){return status;} public void setStatus(EquipmentStatus v){status=v;}
    @Override public String toString(){return "#"+equipmentId+" "+brand+" "+model;}
}
