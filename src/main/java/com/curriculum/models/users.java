package com.curriculum.models;

import java.time.LocalDateTime;

public class users {
    private int  user_Id;
    private String  first_Name;
    private String last_Name;
    private String email;
    private String password;
    private boolean status;
    private LocalDateTime created_at;

    public users (){}

    public users (int user_id,
                  String first_Name,
                  String last_Name,
                  String email,
                  String password,
                  boolean status,
                  LocalDateTime created_at){

        this.user_Id = user_id;
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.created_at = created_at;
    }
    public int getUser_id(){return user_Id;}
    public void setUser_id(int user_id){this.user_Id= user_id;}
    public  String getFirst_Name(){return first_Name;}
    public void setFirst_Name(String first_Name){this.first_Name= first_Name;}
    public  String getLast_Name(){return last_Name;}
    public void setLast_Name(String last_Name){this.last_Name= last_Name;}
    public  String getEmail(){return email;}
    public void setEmail(String email){this.email= email;}
    public  String getPassword(){return password;}
    public void setPassword(String password){this.password= password;}
    public  boolean getStaus(){return status;}
    public void setStatus(boolean status){this.status= status;}
    public  LocalDateTime getCreated_at(){return created_at;}
    public void setCreated_at(LocalDateTime created_at){this.created_at = created_at;}
}
