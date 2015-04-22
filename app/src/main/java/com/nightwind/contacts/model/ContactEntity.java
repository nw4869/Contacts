package com.nightwind.contacts.model;

/**
 * Created by nightwind on 15/4/20.
 */
public class ContactEntity {

    public String name;
    public String photoUri;
    public String phoneNumber;

    public ContactEntity() {
    }

    public ContactEntity(String name) {
        this.name = name;
    }

    public ContactEntity(String name, String photoUri) {
        this.name = name;
        this.photoUri = photoUri;
    }

    public ContactEntity(String name, String photoUri, String phoneNumber) {
        this.name = name;
        this.photoUri = photoUri;
        this.phoneNumber = phoneNumber;
    }
}
