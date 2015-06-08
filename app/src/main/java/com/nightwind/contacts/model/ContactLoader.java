package com.nightwind.contacts.model;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.*;
import android.util.Log;

import com.nightwind.contacts.model.dataitem.DataItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nightwind on 15/4/21.
 */
public class ContactLoader extends AsyncTaskLoader<Contact> {


    private final String contactLookupUri;
    private String[] selectionArgs = {""};

    /**
     * Projection used for the query that loads all data for the entire contact (except for
     * social stream items).
     */
    private static class ContactQuery {
        static final String[] COLUMNS = new String[] {
//                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME_SOURCE,
                ContactsContract.Contacts.DISPLAY_NAME_SOURCE,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
                ContactsContract.Contacts.PHONETIC_NAME,
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
//                RawContacts.DATA_SET,
                RawContacts.DIRTY,
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
//                ContactsContract.Contacts.IS_USER_PROFILE,
//
//                Data.TIMES_USED,
//                Data.LAST_TIME_USED,
        };

        public static final String SELECTION_LOOKUP = Data.LOOKUP_KEY + " = ?";

        public static final String SORT_ORDER = Data.MIMETYPE + " DESC ";

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

        public static final int PRESENCE = 51;//
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

    public ContactLoader(Context context, String contactLookupUri) {
        super(context);
        this.contactLookupUri = contactLookupUri;
        selectionArgs = new String[] {contactLookupUri};
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
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
        ContentResolver resolver = getContext().getContentResolver();

//        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactLookupUri);
        String strUri = ContactsContract.Contacts.CONTENT_LOOKUP_URI.toString() + "/" + contactLookupUri +"/entities";
//        String strUri = "content://com.android.contacts/contacts/lookup/2247i41f325320854105c.2247i3f177fc58fc6ac0c/entities";
        Uri uri = Uri.parse(strUri);

        Log.d("ContactLoader", "uri = " + uri);

        Cursor cursor = resolver.query(uri /*ContactsContract.Contacts.CONTENT_URI*/, ContactQuery.COLUMNS,
                ContactQuery.SELECTION_LOOKUP, selectionArgs,
                ContactQuery.SORT_ORDER);
        Contact contact = new Contact();
        List<DataItem> dataItems = new ArrayList<>();

        Set<String> groupTitleSet = new HashSet<>();

        try {
            if (cursor.moveToFirst()) {
                contact = loadContactHeaderData(cursor);
                ContentValues values;// = loadRawContactValues(cursor);
                do {
                    values = loadDataValues(cursor);
                    String mimeType = values.getAsString(ContactsContract.Contacts.Data.MIMETYPE);
                    if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            || mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            || mimeType.equals(CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)) {
                        DataItem dataItem = DataItem.createFrom(values);
                        String data = cursor.getString(ContactQuery.DATA1);
                        String label = cursor.getString(ContactQuery.DATA3);
                        if (mimeType.equals(CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)) {
                        String groupTitle = new Contacts(getContext()).getGroupTitle(Long.valueOf(data));
                            Log.d("ContactLoader", "group title = " + groupTitle);
                            if (groupTitleSet.contains(groupTitle) || groupTitle.equals("My Contacts") ||
                                    groupTitle.equals("Starred in Android")) {
                                continue;
                            } else {
                                groupTitleSet.add(groupTitle);
                            }
                        }
                        dataItems.add(dataItem);
//                        Log.d("ContactLoader", dataItems.size() + " mimeType = " + mimeType + " data = " + data + " label = " + label);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        contact.setData(dataItems);
        return contact;
    }

    private Contact loadContactHeaderData(Cursor cursor) {
        final long id = cursor.getLong(ContactQuery.CONTACT_ID);
        final long rawContactId = cursor.getLong(ContactQuery.RAW_CONTACT_ID);
        final String lookupUri = cursor.getString(ContactQuery.LOOKUP_KEY);
        final String photoUri = cursor.getString(ContactQuery.PHOTO_URI);
        final String name = cursor.getString(ContactQuery.DISPLAY_NAME);
        final boolean isStart = cursor.getInt(ContactQuery.STARRED) == 1;
//        Log.d("ContactLoader", "photoUri = " + photoUri + " name = " + name);

        Contact contact = new Contact();
        contact.setId(id);
        contact.setRawContactId(rawContactId);
        contact.setName(name);
        contact.setPhotoUri(photoUri);
        contact.setLookupUri(lookupUri);
        contact.setStarred(isStart);
        return contact;
    }

    /**
     * Extracts RawContact level columns from the cursor.
     */
    private ContentValues loadRawContactValues(Cursor cursor) {
        ContentValues cv = new ContentValues();

        cv.put(RawContacts._ID, cursor.getLong(ContactQuery.RAW_CONTACT_ID));

        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_NAME);
        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_TYPE);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SET);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DIRTY);
        cursorColumnToContentValues(cursor, cv, ContactQuery.VERSION);
        cursorColumnToContentValues(cursor, cv, ContactQuery.SOURCE_ID);
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC1);
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC2);
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC3);
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC4);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DELETED);
        cursorColumnToContentValues(cursor, cv, ContactQuery.CONTACT_ID);
        cursorColumnToContentValues(cursor, cv, ContactQuery.STARRED);

        return cv;
    }


    /**
     * Extracts Data level columns from the cursor.
     */
    private ContentValues loadDataValues(Cursor cursor) {
        ContentValues cv = new ContentValues();

        long id = cursor.getLong(ContactQuery.DATA_ID);
//        Log.d("contactLoader", "id = " + id);
        cv.put(Data._ID, id);

        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA1);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA2);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA3);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA4);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA5);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA6);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA7);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA8);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA9);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA10);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA11);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA12);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA13);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA14);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA15);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC1);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC2);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC3);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC4);
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_VERSION);
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_PRIMARY);
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_SUPERPRIMARY);
        cursorColumnToContentValues(cursor, cv, ContactQuery.MIMETYPE);
        cursorColumnToContentValues(cursor, cv, ContactQuery.GROUP_SOURCE_ID);
        cursorColumnToContentValues(cursor, cv, ContactQuery.CHAT_CAPABILITY);
//        cursorColumnToContentValues(cursor, cv, ContactQuery.TIMES_USED);
//        cursorColumnToContentValues(cursor, cv, ContactQuery.LAST_TIME_USED);

        return cv;
    }

    private void cursorColumnToContentValues(  Cursor cursor, ContentValues values, int index) {
        switch (cursor.getType(index)) {
            case Cursor.FIELD_TYPE_NULL:
                // don't put anything in the content values
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                values.put(ContactQuery.COLUMNS[index], cursor.getLong(index));
                break;
            case Cursor.FIELD_TYPE_STRING:
                values.put(ContactQuery.COLUMNS[index], cursor.getString(index));
                break;
            case Cursor.FIELD_TYPE_BLOB:
                values.put(ContactQuery.COLUMNS[index], cursor.getBlob(index));
                break;
            default:
                throw new IllegalStateException("Invalid or unhandled data type");
        }
    }

}
