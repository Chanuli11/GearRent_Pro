package com.gearrent.entity;
import java.math.BigDecimal; import java.time.LocalDate;
public class Rental {
    private int rentalId, equipmentId, customerId, branchId;
    private LocalDate startDate, endDate, actualReturnDate;
    private BigDecimal rentalAmount, securityDeposit, membershipDisc, longRentalDisc, lateFee, damageCharge, finalPayable;
    private String damageNotes, customerName, equipmentName, branchName;
    private PaymentStatus paymentStatus;
    private RentalStatus rentalStatus;
    public Rental(){}
    public int getRentalId(){return rentalId;} public void setRentalId(int v){rentalId=v;}
    public int getEquipmentId(){return equipmentId;} public void setEquipmentId(int v){equipmentId=v;}
    public int getCustomerId(){return customerId;} public void setCustomerId(int v){customerId=v;}
    public int getBranchId(){return branchId;} public void setBranchId(int v){branchId=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
    public LocalDate getActualReturnDate(){return actualReturnDate;} public void setActualReturnDate(LocalDate v){actualReturnDate=v;}
    public BigDecimal getRentalAmount(){return rentalAmount;} public void setRentalAmount(BigDecimal v){rentalAmount=v;}
    public BigDecimal getSecurityDeposit(){return securityDeposit;} public void setSecurityDeposit(BigDecimal v){securityDeposit=v;}
    public BigDecimal getMembershipDisc(){return membershipDisc;} public void setMembershipDisc(BigDecimal v){membershipDisc=v;}
    public BigDecimal getLongRentalDisc(){return longRentalDisc;} public void setLongRentalDisc(BigDecimal v){longRentalDisc=v;}
    public BigDecimal getLateFee(){return lateFee;} public void setLateFee(BigDecimal v){lateFee=v;}
    public BigDecimal getDamageCharge(){return damageCharge;} public void setDamageCharge(BigDecimal v){damageCharge=v;}
    public BigDecimal getFinalPayable(){return finalPayable;} public void setFinalPayable(BigDecimal v){finalPayable=v;}
    public String getDamageNotes(){return damageNotes;} public void setDamageNotes(String v){damageNotes=v;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;}
    public String getEquipmentName(){return equipmentName;} public void setEquipmentName(String v){equipmentName=v;}
    public String getBranchName(){return branchName;} public void setBranchName(String v){branchName=v;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}
    public RentalStatus getRentalStatus(){return rentalStatus;} public void setRentalStatus(RentalStatus v){rentalStatus=v;}
}
