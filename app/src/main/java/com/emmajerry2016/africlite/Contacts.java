package com.emmajerry2016.africlite;

public class Contacts {

    //attribute
    public String name,status,image;

    //empty constructor
    public Contacts(){

    }

    //constructor
    public Contacts(String name) {
        this.name = name;
        this.status=status;
        this.image=image;
    }

    //getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
