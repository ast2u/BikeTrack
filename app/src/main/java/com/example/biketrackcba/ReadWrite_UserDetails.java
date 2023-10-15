package com.example.biketrackcba;

public class ReadWrite_UserDetails {
    public String username, bdate, gender,mobile,emnumber1,emnumber2;

    public ReadWrite_UserDetails(){

    };
    public  ReadWrite_UserDetails(String textUsername,String textBdate,
                                  String textGender,String textMobile,String emnumber1,String emnumber2){


        this.username=textUsername;
        this.bdate=textBdate;
        this.gender=textGender;
        this.mobile=textMobile;
        this.emnumber1=emnumber1;
        this.emnumber2=emnumber2;

    }
}
