package com.techease.posapp.ui.models;

/**
 * Created by kaxhiftaj on 4/4/18.
 */

public class JobsModel {

    String job_id;
    String job_title;
    String company_name;
    String short_desc;
    String brief_desc;
    String pay_per_job;
    String job_desc;
    String job_time;
    String feature_image;
    String latitude;
    String longitude;
    String jobImages;

    public String getJob_id(){
        return job_id;
    }
    public void setJob_id(String job_id){
        this.job_id = job_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getBrief_desc() {
        return brief_desc;
    }

    public void setBrief_desc(String brief_desc) {
        this.brief_desc = brief_desc;
    }

    public String getPay_per_job() {
        return pay_per_job;
    }

    public void setPay_per_job(String pay_per_job) {
        this.pay_per_job = pay_per_job;
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

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getJob_desc() {
        return job_desc;
    }

    public void setJob_desc(String job_desc) {
        this.job_desc = job_desc;
    }

    public String getJob_time() {
        return job_time;
    }

    public void setJob_time(String job_time) {
        this.job_time = job_time;
    }

    public String getFeature_image() {
        return feature_image;
    }
    public void setFeature_image(String feature_image) {
        this.feature_image = feature_image;
    }

    public String getJobImages() {
        return jobImages;
    }

    public void setJobImages(String jobImages) {
        this.jobImages = jobImages;
    }
}
