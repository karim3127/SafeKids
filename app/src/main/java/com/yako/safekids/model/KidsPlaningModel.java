package com.yako.safekids.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macbook on 15/08/2016.
 */
public class KidsPlaningModel implements Serializable {
    Kids kids;
    String timeMode;
    int houre;
    int minute;
    String texto;
    List<Integer> days;

    public KidsPlaningModel() {}

    public KidsPlaningModel(Kids kids, String timeMode, int houre, int minute, String texto, List<Integer> days) {
        this.kids = kids;
        this.timeMode = timeMode;
        this.houre = houre;
        this.minute = minute;
        this.texto = texto;
        this.days = days;
    }

    public Kids getKids() {
        return kids;
    }

    public void setKids(Kids kids) {
        this.kids = kids;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }

    public int getHoure() {
        return houre;
    }

    public void setHoure(int houre) {
        this.houre = houre;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
