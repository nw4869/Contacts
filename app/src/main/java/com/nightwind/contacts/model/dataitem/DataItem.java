package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;
import android.content.res.Resources;
import android.provider.ContactsContract;

/**
 * Created by nightwind on 15/4/21.
 */
public class DataItem {
    private final ContentValues mContentValues;
    protected String mKind;

    public DataItem(ContentValues values) {
        this.mContentValues = values;
    }

    private DataItem() {
        mContentValues = null;
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


    public static DataItem createFrom(ContentValues values) {
        final String mimeType = values.getAsString(ContactsContract.Contacts.Data.MIMETYPE);
        if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
            return new PhoneDataItem(values);
        } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
            return new EmailDataItem(values);
        }

        // generic
        return new DataItem(values);
    }


    /**
     * Returns the mimetype of the data.
     */
    public String getMimeType() {
        return mContentValues.getAsString(ContactsContract.Contacts.Data.MIMETYPE);
    }

    public void setMimeType(String mimeType) {
        mContentValues.put(ContactsContract.Contacts.Data.MIMETYPE, mimeType);
    }

    public static DataItem EMPTY_DATA_ITEM = new DataItem();

    public CharSequence getDisplayLabel(Resources res) {
        return mContentValues.getAsString(ContactsContract.Data.DATA3);
    }

    public String getLabel() {
        return mContentValues.getAsString(ContactsContract.Data.DATA3);
    }

    public void setLabel(String label) {
        mContentValues.put(ContactsContract.Data.DATA3, label);
    }

    public int getType() {
        return mContentValues.getAsInteger(ContactsContract.Data.DATA2);
    }

    public void setType(String type) {
        mContentValues.put(ContactsContract.Data.DATA2, type);
    }

    public String getData() {
        return mContentValues.getAsString(ContactsContract.Data.DATA1);
    }

    public void setData(String data) {
        mContentValues.put(ContactsContract.Data.DATA1, data);
    }

    public long getId() {
        return mContentValues.getAsLong(ContactsContract.Contacts.Data._ID);
    }
}
