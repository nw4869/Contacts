package com.nightwind.contacts.model;

/**
 * Created by nightwind on 15/4/21.
 */
public class Contact {
    private String name;
    private String photoUri;
    private String phoneNumber;

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
