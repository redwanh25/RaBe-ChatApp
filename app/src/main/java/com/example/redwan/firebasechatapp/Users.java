package com.example.redwan.firebasechatapp;

/**
 * Created by redwan on 15-Mar-18.
 */

public class Users {

    // variable name r database er folder er name same hote hobe. noile code cash korbe.
    private String Name;
    private String Image;
    private String Status;
    private String Thumb_image;
    private String Id;

    // ai default constructor na dile app crash korbe. so, be careful
    public Users(){

    }

    public Users(String Name, String Image, String Status, String Thumb_image, String Id) {
        this.Name = Name;
        this.Image = Image;
        this.Status = Status;
        this.Thumb_image = Thumb_image;
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }
    public String getImage() {
        return Image;
    }
    public String getStatus() {
        return Status;
    }
    public String getThumb_image(){
        return Thumb_image;
    }
    public String getId() {
        return Id;
    }
}
