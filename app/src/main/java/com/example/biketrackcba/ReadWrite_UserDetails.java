package com.example.biketrackcba;

public class ReadWrite_UserDetails {
    public String username, bdate, gender,mobile;

    public ReadWrite_UserDetails(){

    };
    public  ReadWrite_UserDetails(String textUsername,String textBdate,
                                  String textGender,String textMobile){


        this.username=textUsername;
        this.bdate=textBdate;
        this.gender=textGender;
        this.mobile=textMobile;

    }
}
