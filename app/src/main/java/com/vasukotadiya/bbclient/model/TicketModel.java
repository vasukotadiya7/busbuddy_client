package com.vasukotadiya.bbclient.model;


import android.text.BoringLayout;

public class TicketModel {
    private String BusNo;
    private String Date;
    private String FromLocation;
    private String ToLocation;
    private String StartTime;
    private String EndTime;
    private String PassengerName;
    private String PassengerPhone;
    private String BusType;
    private String SeatNo;
    private  String Price;
    private Boolean isCanceled;
    private Boolean isReviewed;

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public Boolean getisReviewed() {
        return isReviewed;
    }

    public void setisReviewed(Boolean reviewed) {
        isReviewed = reviewed;
    }

    public String getBusNo() {
        return BusNo;
    }

    public void setBusNo(String busNo) {
        this.BusNo = busNo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getFromLocation() {
        return FromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.FromLocation = fromLocation;
    }

    public String getToLocation() {
        return ToLocation;
    }

    public void setToLocation(String toLocation) {
        this.ToLocation = toLocation;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        this.StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        this.EndTime = endTime;
    }

    public String getPassengerName() {
        return PassengerName;
    }

    public void setPassengerName(String passengerName) {
        this.PassengerName = passengerName;
    }

    public String getPassengerPhone() {
        return PassengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.PassengerPhone = passengerPhone;
    }

    public String getBusType() {
        return BusType;
    }

    public void setBusType(String busType) {
        this.BusType = busType;
    }

    public String getSeatNo() {
        return SeatNo;
    }

    public void setSeatNo(String seatNo) {
        this.SeatNo = seatNo;
    }

    public String getPrice() {
        return Price;
    }

    public Boolean getisCanceled() {
        return isCanceled;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setisCanceled(Boolean canceled) {
        isCanceled = canceled;
    }
}

