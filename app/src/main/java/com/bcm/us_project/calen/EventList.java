package com.bcm.us_project.calen;

// 일정을 등록하기 위해 받는 값들
public class EventList {

    String userID;
    String dateName;
    String location;
    String userDate;
    String dateMemo;

    public EventList(String userID, String dateName, String location, String userDate, String dateMemo) {
        this.userID = userID;
        this.dateName = dateName;
        this.location = location;
        this.userDate = userDate;
        this.dateMemo = dateMemo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getDateMemo() {
        return dateMemo;
    }

    public void setDateMemo(String dateMemo) {
        this.dateMemo = dateMemo;
    }

}
