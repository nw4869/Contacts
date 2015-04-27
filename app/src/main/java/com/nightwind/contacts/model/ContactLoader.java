package com.nightwind.contacts.model;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.*;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
* Created by nightwind on 15/4/21.
*/
public class ContactLoader extends AsyncTaskLoader<Contact> {


    /**
     * Projection used for the query that loads all data for the entire contact (except for
     * social stream items).
     */
    private static class ContactQuery {
        static final String[] COLUMNS = new String[] {
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME_SOURCE,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
                ContactsContract.Contacts.PHONETIC_NAME,/**/
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.STARRED,
                ContactsContract.Contacts.CONTACT_PRESENCE,
                ContactsContract.Contacts.CONTACT_STATUS,
                ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP,
                ContactsContract.Contacts.CONTACT_STATUS_RES_PACKAGE,
                ContactsContract.Contacts.CONTACT_STATUS_LABEL,
                ContactsContract.Contacts.Entity.CONTACT_ID,
                ContactsContract.Contacts.Entity.RAW_CONTACT_ID,

                RawContacts.ACCOUNT_NAME,
                RawContacts.ACCOUNT_TYPE,
                RawContacts.DATA_SET,
                RawContacts.DIRTY,
                RawContacts.VERSION,
                RawContacts.SOURCE_ID,
                RawContacts.SYNC1,
                RawContacts.SYNC2,
                RawContacts.SYNC3,
                RawContacts.SYNC4,
                RawContacts.DELETED,

                ContactsContract.Contacts.Entity.DATA_ID,
                Data.DATA1,
                Data.DATA2,
                Data.DATA3,
                Data.DATA4,
                Data.DATA5,
                Data.DATA6,
                Data.DATA7,
                Data.DATA8,
                Data.DATA9,
                Data.DATA10,
                Data.DATA11,
                Data.DATA12,
                Data.DATA13,
                Data.DATA14,
                Data.DATA15,
                Data.SYNC1,
                Data.SYNC2,
                Data.SYNC3,
                Data.SYNC4,
                Data.DATA_VERSION,
                Data.IS_PRIMARY,
                Data.IS_SUPER_PRIMARY,
                Data.MIMETYPE,

                CommonDataKinds.GroupMembership.GROUP_SOURCE_ID,

                Data.PRESENCE,
                Data.CHAT_CAPABILITY,
                Data.STATUS,
                Data.STATUS_RES_PACKAGE,
                Data.STATUS_ICON,
                Data.STATUS_LABEL,
                Data.STATUS_TIMESTAMP,

                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.SEND_TO_VOICEMAIL,
                ContactsContract.Contacts.CUSTOM_RINGTONE,
                ContactsContract.Contacts.IS_USER_PROFILE,

                Data.TIMES_USED,
                Data.LAST_TIME_USED,
        };

        public static final int NAME_RAW_CONTACT_ID = 0;
        public static final int DISPLAY_NAME_SOURCE = 1;
        public static final int LOOKUP_KEY = 2;
        public static final int DISPLAY_NAME = 3;
        public static final int ALT_DISPLAY_NAME = 4;
        public static final int PHONETIC_NAME = 5;
        public static final int PHOTO_ID = 6;
        public static final int STARRED = 7;
        public static final int CONTACT_PRESENCE = 8;
        public static final int CONTACT_STATUS = 9;
        public static final int CONTACT_STATUS_TIMESTAMP = 10;
        public static final int CONTACT_STATUS_RES_PACKAGE = 11;
        public static final int CONTACT_STATUS_LABEL = 12;
        public static final int CONTACT_ID = 13;
        public static final int RAW_CONTACT_ID = 14;

        public static final int ACCOUNT_NAME = 15;
        public static final int ACCOUNT_TYPE = 16;
        public static final int DATA_SET = 17;
        public static final int DIRTY = 18;
        public static final int VERSION = 19;
        public static final int SOURCE_ID = 20;
        public static final int SYNC1 = 21;
        public static final int SYNC2 = 22;
        public static final int SYNC3 = 23;
        public static final int SYNC4 = 24;
        public static final int DELETED = 25;

        public static final int DATA_ID = 26;
        public static final int DATA1 = 27;
        public static final int DATA2 = 28;
        public static final int DATA3 = 29;
        public static final int DATA4 = 30;
        public static final int DATA5 = 31;
        public static final int DATA6 = 32;
        public static final int DATA7 = 33;
        public static final int DATA8 = 34;
        public static final int DATA9 = 35;
        public static final int DATA10 = 36;
        public static final int DATA11 = 37;
        public static final int DATA12 = 38;
        public static final int DATA13 = 39;
        public static final int DATA14 = 40;
        public static final int DATA15 = 41;
        public static final int DATA_SYNC1 = 42;
        public static final int DATA_SYNC2 = 43;
        public static final int DATA_SYNC3 = 44;
        public static final int DATA_SYNC4 = 45;
        public static final int DATA_VERSION = 46;
        public static final int IS_PRIMARY = 47;
        public static final int IS_SUPERPRIMARY = 48;
        public static final int MIMETYPE = 49;

        public static final int GROUP_SOURCE_ID = 50;

        public static final int PRESENCE = 51;
        public static final int CHAT_CAPABILITY = 52;
        public static final int STATUS = 53;
        public static final int STATUS_RES_PACKAGE = 54;
        public static final int STATUS_ICON = 55;
        public static final int STATUS_LABEL = 56;
        public static final int STATUS_TIMESTAMP = 57;

        public static final int PHOTO_URI = 58;
        public static final int SEND_TO_VOICEMAIL = 59;
        public static final int CUSTOM_RINGTONE = 60;
        public static final int IS_USER_PROFILE = 61;

        public static final int TIMES_USED = 62;
        public static final int LAST_TIME_USED = 63;
    }

    public ContactLoader(Context context) {
        super(context);
    }

    @Override
    public Contact loadInBackground() {
//        ContentResolver resolver = getContext().getContentResolver();
//        String projection[] = new String[] {Data._ID, CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.LABEL};
//
//        Cursor cursor = resolver.query(Data.CONTENT_URI, projection, null, null, null);
//        List<Contact> contacts = new ArrayList<>();
//        try {
//            cursor.moveToFirst();
//            do {
//                Contact contact = new Contact();
//                contact.setName(cursor.getString(3));
//                contact.setPhoneNumber(cursor.getString(1));
//                contacts.add(contact);
//            } while (cursor.moveToNext());
//        } finally {
//            cursor.close();
//        }
//
//        return contacts.get(0);
        return null;
    }


}