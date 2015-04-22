package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;

/**
 * Created by nightwind on 15/4/21.
 */
public class DataItem {
    private final ContentValues mContentValues;
    protected String mKind;

    public DataItem(ContentValues values) {
        this.mContentValues = values;
    }

    public ContentValues getContentValues() {
        return mContentValues;
    }

    public String getKid() {
        return mKind;
    }

    public void setKind(String kind) {
        this.mKind = kind;
    }
}
