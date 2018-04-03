package com.example.redwan.firebasechatapp;

/**
 * Created by redwan on 22-Mar-18.
 */

public class Messages {
    private String message, type;
    private String  time;
    private boolean seen;
    private String from;
    private String sms_id;
    private String isDelete;
    private int position;

    public Messages(){

    }
    public Messages(String from) {
        this.from = from;
    }

    public Messages(String message, String type, String time, boolean seen, String sms_id, String isDelete, int position) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.sms_id = sms_id;
        this.isDelete = isDelete;
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSms_id() {
        return sms_id;
    }

    public void setSms_id(String sms_id) {
        this.sms_id = sms_id;
    }

    public String getIsDelete() {
        return isDelete;
    }
    public void setIsDelete(String isEdit) {
        this.isDelete = isDelete;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}
