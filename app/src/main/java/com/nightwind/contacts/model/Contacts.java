package com.nightwind.contacts.model;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.nightwind.contacts.FileUtils;
import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.EmailDataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                String[] COLUMNS = new String[]{
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
        ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        for (DataItem dataItem : dataItems) {
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
        for (ContentProviderResult result : results) {
            Log.i(TAG, result.uri.toString());
        }

    }


    public void updateContact(Contact contact) throws RemoteException, OperationApplicationException {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?",
                        new String[]{String.valueOf(contact.getRawContactId())})
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getName())
                .build());
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?",
                        new String[]{String.valueOf(contact.getRawContactId())})
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                .build());

        for (DataItem dataItem : contact.getData()) {
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
                            .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(dataItem.getId())})
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

    public void deleteContact(String contactId, String rawContactId) throws RemoteException, OperationApplicationException {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{rawContactId})
                .build());

        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{contactId})
                .build());

        ops.add(ContentProviderOperation.newDelete(ContactsContract.Contacts.CONTENT_URI)
                .withSelection(ContactsContract.Contacts._ID + "=?", new String[]{contactId})
                .build());

        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

//        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
//        int result = context.getContentResolver().delete(uri, null, null);
//        return result == 1;
    }


    public boolean setStarred(String lookupKey, boolean starred) {//添加联系人到常用联系人
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.STARRED, starred ? 1 : 0);
        int result = context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values, ContactsContract.Contacts.LOOKUP_KEY + "=?", new String[]{lookupKey});
        return result == 1;
    }

    public void deleteAllContacts() throws RemoteException, OperationApplicationException {//清空通讯录

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long rawId = cursor.getLong(0);
                long contactId = cursor.getLong(1);
                deleteContact(String.valueOf(contactId), String.valueOf(rawId));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // clear groups
//        My Contacts") || title.equals("Starred in Android"
        int result = resolver.delete(ContactsContract.Groups.CONTENT_URI,
                ContactsContract.Groups.TITLE + " != 'My Contacts'" +
                        " and " + ContactsContract.Groups.TITLE +
                        " != 'Starred in Android'",
                null);
        Log.d(TAG, "delete groups number = " + result);
    }

    public String getLookupKey(String name) {
        String lookupKey = null;

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts.LOOKUP_KEY},
                ContactsContract.Contacts.DISPLAY_NAME + "=?",
                new String[]{name}, null);

        if (cursor.moveToFirst()) {
            lookupKey = cursor.getString(0);
        }
        return lookupKey;
    }

    public boolean nameIsExist(String name) {
        return getLookupKey(name) != null;
    }


    public void exportContacts() throws IOException {//导出联系人
        String path = Environment.getExternalStorageDirectory().getPath() + "/contacts.vcf";

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int index = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        FileOutputStream fout = new FileOutputStream(path);
        byte[] data = new byte[1024 * 1];
        while (cur.moveToNext()) {
            String lookupKey = cur.getString(index);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileInputStream fin = fd.createInputStream();
            int len = -1;
            while ((len = fin.read(data)) != -1) {
                fout.write(data, 0, len);
            }
            fin.close();
        }
        fout.close();
    }


    public void importContacts(Uri fileUri) throws IOException {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
        //storage path is path of your vcf file and vFile is name of that file.

        //Uri.fromFile(new File(path))
//        intent.setDataAndType(fileUri, "text/x-vcard");
//        context.startActivity(intent);


        //get all contacts list
        List<Contact> contacts = new ContactsLoader(context, false, 0).loadInBackground();
        Map<String, Contact> contactMap = new HashMap<>();
        for (Contact contact: contacts) {
            contact = new ContactLoader(context, contact.getLookupUri()).loadInBackground();
            contactMap.put(contact.getName(), contact);
        }

//        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:contacts.vcf");
        String path = FileUtils.getPath(context, fileUri);
        Log.d(TAG, "path = " + path);

        if (path != null) {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            Contact contact = new Contact();
            contact.setData(new ArrayList<DataItem>());

            Log.d(TAG, "VCard begin ----------------");
            String line;
            boolean begin = false;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, line);
                if (line.equals("BEGIN:VCARD")) {
                    begin = true;
                    contact = new Contact();
                    contact.setData(new ArrayList<DataItem>());
                } else if (line.equals("END:VCARD")) {
                    // read a contact end
                    begin = false;

                    // update or insert to database
                    Contact origin = contactMap.get(contact.getName());
                    if (origin == null) {
                        // insert a new contact
                        try {
                            saveContact(contact.getName(), null, contact.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // name exist
                        // update contact: add new data item
                        List<DataItem> newDataItem = new ArrayList<>(origin.getData());
                        for (DataItem dataItem: contact.getData()) {
                            boolean exist = false;
                            for (DataItem orgDataItem: origin.getData()) {
                                if (orgDataItem.equals(dataItem)) {
                                    exist = true;
                                    break;
                                }
                            }
                            if (!exist) {
                                newDataItem.add(dataItem);
                            }
                        }
                        try {
                            if (newDataItem.size() > origin.getData().size()) {
                                // have new data item
                                origin.setData(newDataItem);
                                updateContact(origin);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (begin) {
                    try {
                        int type = 0;
                        if (line.matches("^N:.*;")) {
                            String[] info = line.split(";");
                            String name = info[1];
                            contact.setName(name);
                        } else if (line.matches("^TEL;.+")) {
                            type = 1;
                        } else if (line.matches("^EMAIL;.+")) {
                            type = 2;
                        }

                        if (type == 1 || type == 2) {
                            String info = line.split(";")[1];
                            String[] item = info.split(":");
                            String label = item[0];
                            String data = item[1];

                            String mimeType;
                            if (type == 1) {
                                mimeType = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
                                // 号码清除’-‘和空格
                                data = data.replaceAll("\\s|-", "");
                            } else if (type == 2) {
                                mimeType = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
                            } else {
                                //not tel or email
                                continue;
                            }

                            int labelType = getLabelType(label, mimeType);

                            ContentValues values = new ContentValues();
                            values.put(ContactsContract.Data.MIMETYPE, mimeType);
                            values.put(ContactsContract.Data.DATA1, data);
                            values.put(ContactsContract.Data.DATA2, labelType);
                            values.put(ContactsContract.Data.DATA3, label);

                            contact.getData().add(DataItem.createFrom(values));

                            Log.d(TAG, "data = " + data + " labelType = " + labelType + " label = " + label);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "VCard end ----------------");


            br.close();
        } else {
            Log.d(TAG, "file not found");
        }

    }

    private int getLabelType(String label, String mimeType) {
        int labelType = 0;
        if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
            for (int i = 1; i < ContactsContract.CommonDataKinds.Phone.TYPE_MMS; i++) {
                if (label.equalsIgnoreCase(String.valueOf(
                        ContactsContract.CommonDataKinds.Phone.
                                getTypeLabel(context.getResources(), i, label)))) {
                    labelType = i;
                    break;
                }
            }
        } else if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
            for (int i = 1; i < ContactsContract.CommonDataKinds.Email.TYPE_MOBILE; i++) {
                if (label.equalsIgnoreCase(String.valueOf(
                        ContactsContract.CommonDataKinds.Email.
                                getTypeLabel(context.getResources(), i, label)))) {
                    labelType = i;
                    break;
                }
            }
        }
        return labelType;
    }

    public List<Group> getGroups() {
        Map<Long, Group> groups = new HashMap<>();
        Map<Long, String> groupTitles = new HashMap<>();

        // get titles
        Cursor groupCursor = context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI,
                new String[]{
                        ContactsContract.Groups._ID,
                        ContactsContract.Groups.TITLE
                }, null, null, null
        );

        if (groupCursor.moveToFirst())
            do {
                long id = groupCursor.getLong(0);
                String title = groupCursor.getString(1);
                groupTitles.put(id, title);
//                Log.d(TAG, "id = " + id + " title = " + title);
            } while (groupCursor.moveToNext());
//        Log.d(TAG, "getGroupsQuery done.");
        groupCursor.close();

        Cursor dataCursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DATA1
                },
                ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null
        );

        if (dataCursor.moveToFirst())
            do {
                long id = dataCursor.getLong(0);
                String strGroupId = dataCursor.getString(1);
                long groupId = Long.valueOf(strGroupId);
//                Log.d(TAG, "id = " + id + " group = " + strGroupId);
                Group group = groups.get(groupId);
                if (group == null) {
                    group = new Group();
                    group.setId(groupId);
                    group.setTitle(groupTitles.get(groupId));
                    if (group.getMembers() == null) {
                        group.setMembers(new ArrayList<Long>());
                    }
                    // add to map
                    groups.put(groupId, group);
                }
                List<Long> contactIds = group.getMembers();
                // add to members list
                contactIds.add(id);
            } while (dataCursor.moveToNext());
        dataCursor.close();
//        Log.d(TAG, "getContactsGroup done.");

        // format result
        List<Group> groupList = new ArrayList<>();
        for (Long groupId: groups.keySet()) {
            groupList.add(groups.get(groupId));
        }
        return groupList;
    }

    public long addGroup(String title) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Groups.TITLE, title);
        Uri uri = context.getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, values);
        Log.d(TAG, "URI = " + uri);
        String[] result = uri.toString().split("/");
        return Long.valueOf(result[result.length-1]);
    }

    public boolean updateGroup(long id, String title) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Groups.TITLE, title);
        return context.getContentResolver().update(ContactsContract.Groups.CONTENT_URI, values,
                ContactsContract.Groups._ID + "=?", new String[] {String.valueOf(id)}) == 1;
    }

    public void deleteGroup(long id) throws RemoteException, OperationApplicationException {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Groups.DELETED, 1);
        values.put(ContactsContract.Groups.DIRTY, 1);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Groups.CONTENT_URI)
                .withSelection(ContactsContract.Groups._ID + "=?", new String[]{String.valueOf(id)})
                .withValues(values)
                .build());
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Groups.CONTENT_URI)
                .withSelection(ContactsContract.Groups._ID + "=?", new String[]{String.valueOf(id)})
                .build());
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=?",
                        new String[] {String.valueOf(id)})
                .build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            throw e;
        }

//        context.getContentResolver().update(ContactsContract.Groups.CONTENT_URI, values,
//                ContactsContract.Groups._ID + "=?", new String[]{String.valueOf(id)});
//        return context.getContentResolver().delete(ContactsContract.Groups.CONTENT_URI,
//                ContactsContract.Groups._ID + "=?", new String[]{String.valueOf(id)}) == 1;
    }

    public List<GroupSummary> getGroupSummary() {
        List<GroupSummary> groupSummaries = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Groups.CONTENT_SUMMARY_URI, null,
                ContactsContract.Groups.DELETED + "=0", null, null);
        if (cursor.moveToFirst())
            do {
                long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
                String title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
                int count = cursor.getInt(cursor.getColumnIndex(ContactsContract.Groups.SUMMARY_COUNT));

                // skip system group
                if (title.equals("My Contacts") || title.equals("Starred in Android")) {
                    continue;
                }

                GroupSummary groupSummary = new GroupSummary(id, title, count);
                groupSummaries.add(groupSummary);
            } while (cursor.moveToNext());
        cursor.close();
        return groupSummaries;
    }

    public List<Contact> getGroupMembers(long groupId) {
        List<Contact> contacts = new ArrayList<>();

        String [] projection = new String[] {
                ContactsContract.Data.RAW_CONTACT_ID,
        };
        String selection = ContactsContract.Data.DATA1
                + "=?" + " and " + ContactsContract.Data.MIMETYPE + "= '"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";
        String args[] = new String[] {String.valueOf(groupId)};
        ContentResolver resolver = context.getContentResolver();
        Cursor dataCursor = resolver.query(ContactsContract.Data.CONTENT_URI,
                projection, selection, args, null);

        Set<Long> contactIdSet = new HashSet<>();

        if (dataCursor.moveToFirst())
            do {
                long rawContactId = dataCursor.getLong(0);

                // get contact id
                Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI,
                        new String[]{ContactsContract.RawContacts.CONTACT_ID},
                        ContactsContract.RawContacts._ID + "=?",
                        new String[]{String.valueOf(rawContactId)}, null);

                if (cursor.moveToFirst()) {
                    long contactId = cursor.getLong(0);

                    // check contact id set
                    if (contactIdSet.contains(contactId)) {
                        continue;
                    }
                    contactIdSet.add(contactId);

                    Log.d(TAG, "contact id = " + contactId);

                    Contact contact = loadContact(contactId);
                    contacts.add(contact);
                }
                cursor.close();

            } while (dataCursor.moveToNext());
        dataCursor.close();

        return contacts;
    }

    private Contact loadContact(long contactId) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                ContactsLoader.COLUMNS,
                ContactsContract.Contacts._ID + "=?",
                new String[]{String.valueOf(contactId)},
                ContactsLoader.sortOrder);
        if (cursor.moveToFirst()) {
            return ContactsLoader.loadContact(cursor);
        } else {
            return null;
        }
    }

    public int[] getContactsStatus() {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactCount = cursor.getCount();
        cursor.close();

        cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=?",
                new String[] {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, null);
        int phoneCount = cursor.getCount();
        cursor.close();

        cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=?",
                new String[] {ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}, null);
        int emailCount = cursor.getCount();
        cursor.close();

        return new int[] {contactCount, phoneCount, emailCount};
    }

    public String getGroupTitle(long id) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                new String[]{ContactsContract.Groups.TITLE},
                ContactsContract.Groups._ID + "=?",
                new String[] {String.valueOf(id)},
                null);
        String title = null;
        if (cursor.moveToFirst()) {
            title = cursor.getString(0);
        }
        cursor.close();

        return title;
    }

    public int addToGroup(long groupId, Long[] contactIds) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String[] projection = new String[] {
//                ContactsContract.Contacts.Entity.CONTACT_ID,
                ContactsContract.Contacts.Entity.RAW_CONTACT_ID,

//                ContactsContract.Contacts.Entity.DATA_ID,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA1,
//                ContactsContract.Data.DATA2,
//                ContactsContract.Data.DATA3,
        };
        for (long contactId: contactIds) {
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                    String.valueOf(contactId));
            uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);

            Cursor cursor = context.getContentResolver().query(uri,
                    projection,
//                    ContactsContract.Data.MIMETYPE +
//                        " = '" + ContactsContract.Groups.CONTENT_ITEM_TYPE + "' ",
                    null,
//                    new String[] {String.valueOf(contactId)},
                    null,
                    null);
            try {
                boolean exist = false;
                String rawContactId = null;
                if (cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                        String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                        rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Entity.RAW_CONTACT_ID));
                        Log.d(TAG, "rawContactId = " + rawContactId + " mimeType = " + mimeType +  " data = " + data);
                        if (data.equals(String.valueOf(groupId)) && mimeType.equals(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)) {
                            exist = true;
                        }
                    } while (cursor.moveToNext());
                }

                if (!exist) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.Data.DATA1, String.valueOf(groupId))
                        .build());
                }
            } finally {
                cursor.close();
            }
        }
        ContentProviderResult[] result = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        return result.length;
    }

    private long getRawContactId(long contactId) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)},
                null);
        if (cursor.moveToFirst()) {
            long result = cursor.getLong(0);
            cursor.close();
            return result;
        } else {
            cursor.close();
            throw new IllegalArgumentException();
        }
    }

    public boolean removeContactFromGroup(long groupId, long contactId) {
        long rawContactId = getRawContactId(contactId);

        int result = context.getContentResolver().delete(ContactsContract.Data.CONTENT_URI,
                ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "' and "
                        + ContactsContract.Data.DATA1 + "=?" + " and "
                        + ContactsContract.Data.RAW_CONTACT_ID + "=?",
                new String[] {String.valueOf(groupId), String.valueOf(rawContactId)});

        return result == 1;
    }
}
