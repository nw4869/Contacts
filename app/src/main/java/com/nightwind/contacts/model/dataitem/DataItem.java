package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;
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

}
