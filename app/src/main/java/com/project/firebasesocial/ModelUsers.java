package com.project.firebasesocial;

public class ModelUsers {

    //Use same name as in Firebase database
    String name, email, search, phone, image, cover, uid;

    public ModelUsers(){

    }

    public ModelUsers(String name,String email,String search,String phone,String image,String cover){
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
    }

    public  String getName(){
        return  name;
    }

    public void setName(){
        this.name = name;
    }


    public  String getemail(){
        return  email;
    }

    public void setemail(){
        this.email = email;
    }

    public  String getsearch(){
        return  search;
    }

    public void setsearch(){
        this.search = search;
    }

    public  String getphone(){
        return  phone;
    }

    public void setphone(){
        this.phone = phone;
    }

    public  String getimage(){
        return  image;
    }

    public void setimage(){
        this.image = image;
    }

    public  String getcover(){
        return  cover;
    }

    public void setcover(){
        this.cover = cover;
    }

    public  String getUid(){
        return  uid;
    }

    public void setUid(){
        this.uid = uid;
    }
}
