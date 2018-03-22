package com.example.redwan.firebasechatapp;

/**
 * Created by redwan on 23-Mar-18.
 */

public class Conv {

    public boolean seen;
    public long timestamp;

    public Conv(){

    }

    public Conv(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
