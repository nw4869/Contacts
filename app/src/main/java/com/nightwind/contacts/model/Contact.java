package com.nightwind.contacts.model;

import com.nightwind.contacts.model.dataitem.DataItem;

import java.util.List;

/**
 * Created by nightwind on 15/4/21.
 */
public class Contact {
    private String name;
    private String photoUri;
    private List<DataItem> data;

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

    public List<DataItem> getData() {
        return data;
    }

    void setData(List<DataItem> data) {
        this.data = data;
    }
}
