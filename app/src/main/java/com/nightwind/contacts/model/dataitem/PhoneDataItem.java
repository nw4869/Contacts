package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Created by nightwind on 15/4/22.
 */
public class PhoneDataItem extends DataItem {

    PhoneDataItem(ContentValues values) {
        super(values);
    }

    public String getNumber() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Phone.NUMBER);
    }

//    /**
//     * Returns the normalized phone number in E164 format.
//     */
//    public String getNormalizedNumber() {
//        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
//    }

    public String getLabel() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.Phone.LABEL);
    }

}
