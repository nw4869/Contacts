package com.nightwind.contacts.model;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.EmailDataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nightwind on 15/4/22.
 */
public class Contacts {

    private static final String TAG = "Contacts";
    private final Context context;

    public Contacts(Context context) {
        this.context = context;
    }

    public List<Contact> searchContact(String queryName) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                Uri.encode(queryName));
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(lookupUri, null, null, null, null);
        List<Contact> contacts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                String fullName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                contact.setName(fullName);
                contact.setLookupUri(lookupKey);
                contact.setPhotoUri(photoUri);
//                Log.d(TAG, "name = " + fullName + " lookupKey = " + lookupKey);

                Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey + "/entities");
                String[] COLUMNS = new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.Data.MIMETYPE,
                };

                //获取联系人的手机号码
                Cursor dataCursor = resolver.query(contactUri,
                        COLUMNS,
                        ContactsContract.Data.MIMETYPE + "=?",
                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                        null);
                if (dataCursor.moveToFirst()) {
                    List<DataItem> dataItems = new ArrayList<>();
                    do {
                        String phoneNumber = dataCursor.getString(0);
//                        Log.d(TAG, "\t\tphoneNumber = " + phoneNumber);

                        ContentValues values = new ContentValues();
                        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                        values.put(ContactsContract.CommonDataKinds.Phone.DATA, phoneNumber);
                        dataItems.add(DataItem.createFrom(values));
                    } while (dataCursor.moveToNext());
                    contact.setData(dataItems);
                }
                contacts.add(contact);
                dataCursor.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }

    public void saveContact(String name, String photoUri, List<DataItem> dataItems) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .build());
        for (DataItem dataItem: dataItems) {
            if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                PhoneDataItem phoneDataItem = (PhoneDataItem) dataItem;
                ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneDataItem.getNumber())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, dataItem.getType())
                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phoneDataItem.getLabel())
                        .build());
            } else if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                EmailDataItem emailDataItem = (EmailDataItem) dataItem;
                ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailDataItem.getAddress())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, dataItem.getType())
                        .withValue(ContactsContract.CommonDataKinds.Email.LABEL, emailDataItem.getLabel())
                        .build());
            }
        }
        ContentProviderResult[] results = context.getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, ops);
        for(ContentProviderResult result : results)
        {
            Log.i(TAG, result.uri.toString());
        }

    }


    public void updateContact(Contact contact) throws RemoteException, OperationApplicationException {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (DataItem dataItem: contact.getData()) {
            if (dataItem.getId() == 0) {
                // insert
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.getRawContactId())
                        .withValue(ContactsContract.Data.MIMETYPE, dataItem.getMimeType())
                        .withValue(ContactsContract.Data.DATA1, dataItem.getData())
                        .withValue(ContactsContract.Data.DATA2, dataItem.getType())
                        .withValue(ContactsContract.Data.DATA3, dataItem.getLabel())
                        .build());
            } else {
                if (TextUtils.isEmpty(dataItem.getData())) {
                    // delete
                    ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data._ID + "=?", new String[] {String.valueOf(dataItem.getId())})
                            .build());
                } else {
                    // update
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(dataItem.getId())})
                            .withValue(ContactsContract.Data.DATA1, dataItem.getData())
                            .withValue(ContactsContract.Data.DATA2, dataItem.getType())
                            .withValue(ContactsContract.Data.DATA3, dataItem.getLabel())
                            .build());
                }
            }
        }
        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }

    public boolean deleteContact(String lookupKey) {
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        int result = context.getContentResolver().delete(uri, null, null);
        return result == 1;
    }
}
