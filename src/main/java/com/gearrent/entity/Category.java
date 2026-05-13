package com.gearrent.entity;
import java.math.BigDecimal;
public class Category {
    private int categoryId; private String name, description;
    private BigDecimal basePriceFactor, weekendMultiplier, lateFeePerDay;
    private boolean active;
    public Category(){}
    public int getCategoryId(){return categoryId;} public void setCategoryId(int v){categoryId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getBasePriceFactor(){return basePriceFactor;} public void setBasePriceFactor(BigDecimal v){basePriceFactor=v;}
    public BigDecimal getWeekendMultiplier(){return weekendMultiplier;} public void setWeekendMultiplier(BigDecimal v){weekendMultiplier=v;}
    public BigDecimal getLateFeePerDay(){return lateFeePerDay;} public void setLateFeePerDay(BigDecimal v){lateFeePerDay=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    @Override public String toString(){return name;}
}
