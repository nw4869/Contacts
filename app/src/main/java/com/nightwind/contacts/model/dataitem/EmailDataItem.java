package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;
import android.content.res.Resources;
import android.provider.ContactsContract;

/**
 * Created by nightwind on 15/4/21.
 */
public class EmailDataItem extends DataItem {

    /* package */ EmailDataItem(ContentValues values) {
        super(values);
    }

    public String getAddress() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Email.ADDRESS);
    }

    public String getDisplayName() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
    }

    public String getData() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Email.DATA);
    }

    public String getLabel() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Email.LABEL);
    }

    public int getType() {
        return getContentValues().getAsInteger(ContactsContract.CommonDataKinds.Email.TYPE);
    }


    public CharSequence getDisplayLabel(Resources res) {
        return ContactsContract.CommonDataKinds.Email.getTypeLabel(res, getType(), getLabel());
    }

}
