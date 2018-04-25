package com.techease.posapp.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllJobImagesModel {


    @SerializedName("job_id")
    @Expose
    private String jobId;
    @SerializedName("job_images")
    @Expose
    private String jobImages;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobImages() {
        return jobImages;
    }

    public void setJobImages(String jobImages) {
        this.jobImages = jobImages;
    }

}
