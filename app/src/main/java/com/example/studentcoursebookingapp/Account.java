package com.example.studentcoursebookingapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
    private String username;
    private String pwd;
    private String role;

//    public Account() {
//        this("", "", "");
//    }
//
//    public Account(String username, String pwd, String role) {
//        this.username = username;
//        this.pwd = pwd;
//        this.role = role;
//    }

    public static boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9_]{7,29}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    public static boolean isValidPassword(String pwd) {
        String regex = "^(?=.*[0-9])" // digit must occur at least once
                + "(?=.*[a-z])(?=.*[A-Z])" // lower case letter and upper case letter must occur at least once for each
                + "(?=\\S+$).{6,12}$"; // spaces aren't allow and the password length must be at least 6 but at most 12
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pwd);
        return m.matches();
    }
}
