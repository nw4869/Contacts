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
//    public final static int PHOTO_THUMBNAIL_URI = 7;

    private static final String[] COLUMNS = new String[] {
            ContactsContract.Contacts._ID, // ..........................................0
            ContactsContract.Contacts.DISPLAY_NAME, // .................................1
            ContactsContract.Contacts.STARRED, // ......................................2
            ContactsContract.Contacts.PHOTO_URI, // ....................................3
            ContactsContract.Contacts.LOOKUP_KEY, // ...................................4
            ContactsContract.Contacts.CONTACT_PRESENCE, // .............................5
            ContactsContract.Contacts.CONTACT_STATUS, // ...............................6
//            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, // ..........................7
            ContactsContract.Contacts.SORT_KEY_PRIMARY
    };

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Contact> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                COLUMNS, null,null, "sort_key COLLATE LOCALIZED asc");
        List<Contact> contacts = new ArrayList<>();
        try {
            if (!cursor.moveToFirst()) {
                return contacts;
            }

            do {
                int id = cursor.getInt(CONTACT_ID);
                String name = cursor.getString(DISPLAY_NAME);
                int starred = cursor.getInt(STARRED);
                String lookupUri = cursor.getString(LOOKUP_KEY);
                String photoUri = cursor.getString(PHOTO_URI);
//                String photoThumbUri = cursor.getString(ContactsLoader.PHOTO_THUMBNAIL_URI);
                String photoThumbUri = null;
//                Log.d("ContactFragment", "id = " + id + " name = " + name + " starred = " + starred + " photoUri = " + photoUri + " photoThumbUri = " + photoThumbUri);

                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhotoUri(photoUri);
                contact.setLookupUri(lookupUri);
                contacts.add(contact);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return contacts;
    }
}
