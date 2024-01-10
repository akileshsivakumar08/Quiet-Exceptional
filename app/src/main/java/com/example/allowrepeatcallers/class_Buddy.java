package com.example.allowrepeatcallers;

import java.io.Serializable;

public class class_Buddy implements Serializable {


    private String Buddy_PhNo;
    private String Buddy_Message;
    private String Buddy_name;
    class_Buddy(String name, String Phno,String message){
        Buddy_name=name;
        Buddy_PhNo=Phno;
        Buddy_Message=message;
    }

    public String getBuddy_PhNo() {
        return Buddy_PhNo;
    }

    public String getBuddy_Message() {
        return Buddy_Message;
    }

    public String getBuddy_name() {
        return Buddy_name;
    }

    public void setBuddy_PhNo(String buddy_PhNo) {
        Buddy_PhNo = buddy_PhNo;
    }

    public void setBuddy_Message(String buddy_Message) {
        Buddy_Message = buddy_Message;
    }

    public void setBuddy_name(String buddy_name) {
        this.Buddy_name = buddy_name;
    }
}
