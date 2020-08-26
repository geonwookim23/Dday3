package com.example.escbasicapp;

import java.util.ArrayList;

public class DummyData {
    public static ArrayList<Contact> contacts = new ArrayList<>();

    static{
        contacts.add(new Contact("김건우", "010-3321-8119", "kgw0428@hanyang.ac.kr"));
        contacts.add(new Contact("이지현", "010-9510-4652","zzh23@hanyang.ac.kr"));

    }
}
