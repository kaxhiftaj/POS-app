package com.techease.posapp.ui.models;

public class UserAcceptedModel {
    String first_name;
    String last_name;
    String email;
    String users_id;
    String job_title;
    String description;
    String latitude;
    String longitude;
    String image;

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name){
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name){
        this.last_name = last_name;
    }

    public String getUsers_id() {
        return users_id;
    }
    public void setUsers_id(String users_id){
        this.users_id = users_id;
    }

    public String getJob_title() {
        return job_title;
    }
    public void setJob_title(String jobTitle){
        this.job_title = jobTitle;
    }

    public String getDescription() {
        return description;
    }
    public void  setDescription(String description){
        this.description = description;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image){
        this.image = image;
    }
}
