package com.nightwind.contacts.model.dataitem;

import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Created by nightwind on 15/4/21.
 */
public class GroupMembershipDataItem extends DataItem {

    /* package */ GroupMembershipDataItem(ContentValues values) {
        super(values);
    }

    public Long getGroupRowId() {
        return getContentValues().getAsLong(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID);
    }

    public String getGroupSourceId() {
        return getContentValues().getAsString(ContactsContract.CommonDataKinds.GroupMembership.GROUP_SOURCE_ID);
    }
}
