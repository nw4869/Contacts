package com.nightwind.contacts.model;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
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
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phoneDataItem.getLabel())
                        .build());
            } else if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                EmailDataItem emailDataItem = (EmailDataItem) dataItem;
                ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailDataItem.getAddress())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
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

    public void deleteContact(String lookupKey) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.DELETED, 1);
        ContentResolver resolver = context.getContentResolver();
//        resolver.update(ContactsContract.RawContacts.CONTENT_URI, values,
//                ContactsContract.RawContacts. + "")
    }

    public void updateContact(Contact contact) throws RemoteException, OperationApplicationException {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();


        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Contacts.LOOKUP_KEY + " = ?",
                        new String[]{contact.getLookupUri()})
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .build());

        for (DataItem dataItem: contact.getData()) {

            if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                PhoneDataItem phoneDataItem = (PhoneDataItem) dataItem;
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                .withSelection(ContactsContract.Contacts.LOOKUP_KEY + " = ? AND " +
                                                ContactsContract.RawContacts.Data.MIMETYPE + " = ? ",
                                        new String[]{contact.getLookupUri(), dataItem.getMimeType()})
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneDataItem.getNumber())
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phoneDataItem.getLabel())
                                .build()
                );

            } else if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                EmailDataItem emailDataItem = (EmailDataItem) dataItem;

                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                .withSelection(ContactsContract.Contacts.LOOKUP_KEY + " = ? AND " +
                                                ContactsContract.RawContacts.Data.MIMETYPE + " = ? ",
                                        new String[]{contact.getLookupUri(), dataItem.getMimeType()})
                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailDataItem.getAddress())
                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .withValue(ContactsContract.CommonDataKinds.Email.LABEL, emailDataItem.getLabel())
                                .build()
                );
            }
        }

        ContentProviderResult[] results = context.getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, ops);
        for(ContentProviderResult result : results)
        {
            Log.i(TAG, result.uri.toString());
        }
    }
}
