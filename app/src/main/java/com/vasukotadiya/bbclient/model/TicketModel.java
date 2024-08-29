package com.vasukotadiya.bbclient.model;


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


}

