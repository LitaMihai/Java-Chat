package com.messages;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    private String name;
    private MessageType type;
    private String msg;
    private int onlineCount;
    private ArrayList<User> list;
    private ArrayList<User> users;
    private Status status;
    private byte[] voiceMsg;
    private String picture;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        if(name.isEmpty() == false)
            this.name = name;
    }

    public MessageType getType(){
        return this.type;
    }

    public void setType(MessageType type){
        if(type != null)
            this.type = type;
    }

    public String getMsg(){
        return this.msg;
    }

    public void setMsg(String msg){
        if(msg.isEmpty() == false)
            this.msg = msg;
    }

    public int getOnlineCount(){
        return this.onlineCount;
    }

    public void setOnlineCount(int onlineCount){
        //if(onlineCount != 0)
            this.onlineCount = onlineCount;
    }

    public ArrayList<User> getUserList(){
        return this.list;
    }

    public void setUserList(HashMap<String, User> userList) {
        if(userList.values().isEmpty() == false)
            this.list = new ArrayList<>(userList.values());
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public void setUsers(ArrayList<User> users){
        if(users.isEmpty() == false)
            this.users = users;
    }

    public Status getStatus(){
        return this.status;
    }

    public void setStatus(Status status){
        if(status != null)
            this.status = status;
    }

    public byte[] getVoiceMsg(){
        return this.voiceMsg;
    }

    public void setVoiceMsg(byte[] voiceMsg){
        if(voiceMsg != null)
            this.voiceMsg = voiceMsg;
    }

    public String getPicture(){
        return this.picture;
    }

    public void setPicture(String picture){
        if(picture.isEmpty() == false)
            this.picture = picture;

    }
}
