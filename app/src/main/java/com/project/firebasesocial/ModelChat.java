package com.project.firebasesocial;

public class ModelChat {
    String message, receiver, sender, timestamp;
    boolean isSeen;

    public ModelChat(){

    }

    public ModelChat(String message, String receiver, String sender, String timestamp, boolean isSeen){
        this.message = message;
        this.receiver= receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessage(){
         return message;
    }

    public void setMessage(String message){
        this.message = message;
    }


    public String getreceiver(){
        return receiver;
    }

    public void setreceiver(String receiver){
        this.receiver = receiver;
    }

    public String getsender(){
        return sender;
    }

    public void setsender(String sender){
        this.sender = sender;
    }

    public String gettimestamp(){
        return timestamp;
    }

    public void settimestamp(String timestamp){
        this.timestamp = timestamp;
    }


    public boolean isSeen(){
        return isSeen;
    }

    public void setSeen(boolean Seen){
        isSeen = Seen;
    }

}
