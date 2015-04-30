package com.nightwind.contacts.model;

import com.nightwind.contacts.model.dataitem.DataItem;

import java.util.List;

/**
 * Created by nightwind on 15/4/21.
 */
public class Contact {
    private String name;
    private String photoUri;
    private String lookupUri;
    private List<DataItem> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public String getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(String lookupUri) {
        this.lookupUri = lookupUri;
    }
}
