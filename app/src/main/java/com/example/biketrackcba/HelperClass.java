package com.example.biketrackcba;

import java.util.Date;

public class HelperClass {
    String name,email,username,password;
    String bdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public HelperClass(String name, String email, String username, String password, String bdate) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.bdate = bdate;
    }
    public HelperClass(){

    }

}
