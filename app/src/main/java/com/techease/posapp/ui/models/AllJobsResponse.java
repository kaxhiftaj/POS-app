package com.techease.posapp.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllJobsResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("job_images")
    @Expose
    private List<AllJobImagesModel> jobImages = null;
    @SerializedName("All_jobs")
    @Expose
    private List<AllJobsDataModel> allJobs = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AllJobImagesModel> getAllJobImagesModels() {
        return jobImages;
    }

    public void setAllJobImagesModels(List<AllJobImagesModel> jobImages) {
        this.jobImages = jobImages;
    }

    public List<AllJobsDataModel> getAllJobsDataModels() {
        return allJobs;
    }

    public void setAllJobsDataModels(List<AllJobsDataModel> allJobs) {
        this.allJobs = allJobs;
    }
}
