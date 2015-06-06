package com.nightwind.contacts.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/4/25.
 */
public class ContactsLoader extends AsyncTaskLoader<List<Contact>> {

    public final static int CONTACT_ID = 0;
    public final static int DISPLAY_NAME = 1;
    public final static int STARRED = 2;
    public final static int PHOTO_URI = 3;
    public final static int LOOKUP_KEY = 4;
    public final static int CONTACT_PRESENCE = 5;
    public final static int CONTACT_STATUS = 6;
    public final static int PHOTO_THUMBNAIL_URI = 7;
    public final static int SORT_KEY_PRIMARY = 8;


     static final String[] COLUMNS = new String[] {
            ContactsContract.Contacts._ID, // ..........................................0
            ContactsContract.Contacts.DISPLAY_NAME, // .................................1
            ContactsContract.Contacts.STARRED, // ......................................2
            ContactsContract.Contacts.PHOTO_URI, // ....................................3
            ContactsContract.Contacts.LOOKUP_KEY, // ...................................4
            ContactsContract.Contacts.CONTACT_PRESENCE, // .............................5
            ContactsContract.Contacts.CONTACT_STATUS, // ...............................6
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, // ..........................7
            ContactsContract.Contacts.SORT_KEY_PRIMARY,//...............................8
    };
    private final boolean starred;
    private final long groupId;
    static String sortOrder = "sort_key COLLATE LOCALIZED asc";

    public ContactsLoader(Context context, boolean starred, long groupId) {
        super(context);
        this.starred = starred;
        this.groupId = groupId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Contact> loadInBackground() {

        // check if just show group members
        if (groupId > 0) {
            // load group members and return
            return new Contacts(getContext()).getGroupMembers(groupId);
        }


        String selection = null;
        String[] selectionArgs = null;
        if (starred) {
            selection = ContactsContract.Contacts.STARRED + "=?";
            selectionArgs = new String[] {"1"};
        }

        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                COLUMNS, selection, selectionArgs, sortOrder);
        List<Contact> contacts = new ArrayList<>();
        try {
            if (!cursor.moveToFirst()) {
                return contacts;
            }

            do {
                contacts.add(loadContact(cursor));
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return contacts;
    }

    static Contact loadContact(Cursor cursor) {
        int id = cursor.getInt(CONTACT_ID);
        String name = cursor.getString(DISPLAY_NAME);
        int starred = cursor.getInt(STARRED);
        String lookupUri = cursor.getString(LOOKUP_KEY);
        String photoUri = cursor.getString(PHOTO_URI);
//                String photoThumbUri = cursor.getString(ContactsLoader.PHOTO_THUMBNAIL_URI);
        String photoThumbUri = null;
        Log.d("ContactFragment", "id = " + id + " name = " + name + " starred = " + starred + " photoUri = " + photoUri + " photoThumbUri = " + photoThumbUri);

        Contact contact = new Contact();
        contact.setId(id);
        contact.setStarred(starred == 1);
        contact.setName(name);
        contact.setPhotoUri(photoUri);
        contact.setLookupUri(lookupUri);

        return contact;
    }
}
