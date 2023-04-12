package com.messages;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String picture;
    private Status status;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        if(name.isEmpty() == false)
            this.name = name;
    }

    public String getPicture(){
        return this.picture;
    }

    public void setPicture(String picture){
        if(picture.isEmpty() == false)
            this.picture = picture;
    }

    public Status getStatus(){
        return this.status;
    }

    public void setStatus(Status status){
        this.status = status;
    }
}
