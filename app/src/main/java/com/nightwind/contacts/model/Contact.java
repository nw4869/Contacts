package com.nightwind.contacts.model;

import com.nightwind.contacts.model.dataitem.DataItem;

import java.util.List;

/**
 * Created by nightwind on 15/4/21.
 */
public class Contact {
    private long id;
    private long rawContactId;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(long rawContactId) {
        this.rawContactId = rawContactId;
    }
}
