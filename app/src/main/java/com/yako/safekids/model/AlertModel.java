package com.yako.safekids.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by macbook on 15/08/2016.
 */
public class AlertModel implements Serializable {
    List<Kids> listKids;
    Date alertDate;
    int extraTime;
    String locationName;
    double latitude;
    double longitude;

    public AlertModel() {}

    public AlertModel(List<Kids> listKids, Date alertDate, int extraTime) {
        this.listKids = listKids;
        this.alertDate = alertDate;
        this.extraTime = extraTime;
    }

    public AlertModel(List<Kids> listKids, Date alertDate, int extraTime, String locationName, double latitude, double longitude) {
        this.listKids = listKids;
        this.alertDate = alertDate;
        this.extraTime = extraTime;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public List<Kids> getListKids() {
        return listKids;
    }

    public void setListKids(List<Kids> listKids) {
        this.listKids = listKids;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }
}
