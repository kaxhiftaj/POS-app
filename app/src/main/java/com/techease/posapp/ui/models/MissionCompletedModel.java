package com.techease.posapp.ui.models;

import com.android.volley.toolbox.StringRequest;

public class MissionCompletedModel {
    String company_name;
    String mission_title;
    String short_description;
    String brief_description;
    String company_logo;
    String date;
    String time;
    String comments;

    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getMission_title() {
        return mission_title;
    }
    public void setMission_title(String mission_title) {
        this.mission_title = mission_title;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getBrief_description() {
        return brief_description;
    }

    public void setBrief_description(String brief_description) {
        this.brief_description = brief_description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }
}
