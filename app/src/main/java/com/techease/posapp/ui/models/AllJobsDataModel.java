package com.techease.posapp.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllJobsDataModel {


    @SerializedName("job_id")
    @Expose
    private String jobId;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("shot_desc")
    @Expose
    private String shotDesc;
    @SerializedName("brief_desc")
    @Expose
    private String briefDesc;
    @SerializedName("pay_per_job")
    @Expose
    private String payPerJob;
    @SerializedName("featured_img")
    @Expose
    private String featuredImg;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("date")
    @Expose
    private String date;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getShotDesc() {
        return shotDesc;
    }

    public void setShotDesc(String shotDesc) {
        this.shotDesc = shotDesc;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getPayPerJob() {
        return payPerJob;
    }

    public void setPayPerJob(String payPerJob) {
        this.payPerJob = payPerJob;
    }

    public String getFeaturedImg() {
        return featuredImg;
    }

    public void setFeaturedImg(String featuredImg) {
        this.featuredImg = featuredImg;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
