package com.nightwind.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.test.AndroidTestCase;
import android.util.Log;

import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactEntity;
import com.nightwind.contacts.model.Contacts;
import com.nightwind.contacts.model.Group;
import com.nightwind.contacts.model.GroupSummary;
import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by nightwind on 15/4/23.
 */
public class ContactTest extends AndroidTestCase
{
    private static final String TAG = "ContactTest";

//    public void testDeleteContact() {
//        long rawContactId = 3;
//        ContentValues values = new ContentValues();
//        values.put(ContactsContract.RawContacts.DELETED, 1);
//        int result = getContext().getContentResolver().update(ContactsContract.RawContacts.CONTENT_URI,
//                values,
//                ContactsContract.RawContacts._ID + "=?",
//                new String[]{String.valueOf(rawContactId)});
//        Log.d(TAG, "result = " + result);
//    }

    public void testDeleteContact1() {
        String lookupKey = "0r1-4329413116";
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        int reuslt = getContext().getContentResolver().delete(uri, null, null);
        Log.d(TAG, "result = " + reuslt);
    }

    public void testSearchContactByPartialName() {
        //args
        String queryName = "a";

        //begin time
        long begin = System.currentTimeMillis();

        new Contacts(getContext()).searchContact(queryName);

        long end = System.currentTimeMillis();

        Log.d(TAG, "time used: " + (end - begin) / 1000.0 + "s");
    }

    public void testSearchContact() {
        String phoneNumber = "1234";
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(lookupUri, null, null, null, null);
        Log.d(TAG, "search begin");
        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
                Log.d(TAG, "number = " + number);

            } while (cursor.moveToNext());
        }
        Log.d(TAG, "search end");
        cursor.close();
    }

    public void testUpdateContact() throws Exception {
        long rawContactId = 2;
        long dataId = 27;
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Contacts.Entity.DATA_ID + "=?", new String[]{String.valueOf(dataId)})
                .withValue(ContactsContract.Data.DATA1, "1234567890")
                .withValue(ContactsContract.Data.DATA2, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .withValue(ContactsContract.Data.DATA3, "my Label")
                .build());
        getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }

    public void testContactSave1() throws Throwable {
        String name = "nw";
        List<DataItem> dataItems = new ArrayList<>();
//        PhoneDataItem phoneDataItem = DataItem.createFrom()
        Contacts contacts = new Contacts(getContext());
    }

    public void testGetAllContact() throws Throwable
    {
//获取联系人信息的Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//获取ContentResolver
        ContentResolver contentResolver = this.getContext().getContentResolver();
//查询数据，返回Cursor
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        while(cursor.moveToNext())
        {
            StringBuilder sb = new StringBuilder();
//获取联系人的ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//获取联系人的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//构造联系人信息
            sb.append("contactId=").append(contactId).append(",Name=").append(name);
//查询电话类型的数据操作
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                    null, null);
            while(phones.moveToNext())
            {
                String phoneNumber = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
//添加Phone的信息
                sb.append(",Phone=").append(phoneNumber);
            }
            phones.close();
//查询Email类型的数据操作
            Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                    null, null);
            while (emails.moveToNext())
            {
                String emailAddress = emails.getString(emails.getColumnIndex(
                        ContactsContract.CommonDataKinds.Email.DATA));
//添加Email的信息
                sb.append(",Email=").append(emailAddress);
            }
            emails.close();
            Log.i(TAG, sb.toString());
        }
        cursor.close();
    }
    public void testInsert()
    {
        ContentValues values = new ContentValues();
//首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        Uri rawContactUri = this.getContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
//获取id
        long rawContactId = ContentUris.parseId(rawContactUri);
//往data表入姓名数据
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId); //添加id
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//添加内容类型（MIMETYPE）
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "凯风自南");//添加名字，添加到first name位置
        this.getContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
//往data表入电话数据
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "13921009789");
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        this.getContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
//往data表入Email数据
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, "kesenhoo@gmail.com");
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        this.getContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }
    public void testSave() throws Throwable
    {
//官方文档位置：reference/android/provider/ContactsContract.RawContacts.html
//建立一个ArrayList存放批量的参数
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
//官方文档位置：reference/android/provider/ContactsContract.Data.html
//withValueBackReference后退引用前面联系人的id
        ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "小明")
                .build());
        ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "13671323809")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "手机号")
                .build());
        ops.add(ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "kesen@gmail.com")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());
        ContentProviderResult[] results = this.getContext().getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY, ops);
        for(ContentProviderResult result : results)
        {
            Log.i(TAG, result.uri.toString());
        }

    }


    public void testStarContact() {
        String lookupKey = "0r11-4F314D4F";
        boolean ok = new Contacts(getContext()).setStarred(lookupKey, true);
        assertTrue(ok);
    }

    public void testDeleteAllContact() throws RemoteException, OperationApplicationException {
        new Contacts(getContext()).deleteAllContacts();

    }

    public void testGetGroups() {
        Cursor groupCursor = getContext().getContentResolver().query(
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
                Log.d(TAG, "id = " + id + " title = " + title);
            } while (groupCursor.moveToNext());
        Log.d(TAG, "getGroups done.");

        groupCursor.close();
    }

    public void testGetContactsGroup() {

        Map<String, List<Long>> groups = new HashMap<>();
        Map<Long, String> groupTitles = new HashMap<>();

        // get titles
        Cursor groupCursor = getContext().getContentResolver().query(
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
                Log.d(TAG, "id = " + id + " title = " + title);
            } while (groupCursor.moveToNext());
        Log.d(TAG, "getGroupsQuery done.");
        groupCursor.close();

        Cursor dataCursor = getContext().getContentResolver().query(
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
                String group = dataCursor.getString(1);
                Log.d(TAG, "id = " + id + " group = " + group);
                List<Long> contactIds = groups.get(group);
                if (contactIds == null) {
                    contactIds = new ArrayList<>();
                    groups.put(group, contactIds);
                }
                contactIds.add(id);
            } while (dataCursor.moveToNext());
        dataCursor.close();


        Log.d(TAG, "getContactsGroup done.");
    }

    public void testGroup1() {
        List<Group> groups = new Contacts(getContext()).getGroups();
        for (Group group: groups) {
            Log.d(TAG, "id = " + group.getId() + " title = " + group.getTitle() + " size = " + group.getMembers().size());
        }
        Log.d(TAG, "done.");
    }

    public void testAddGroup() {
        long result = new Contacts(getContext()).addGroup("world");
        Log.d(TAG, "add group result = " + result);
    }

    public void testGroupsSummary() {
        List<GroupSummary> groups = new Contacts(getContext()).getGroupSummary();
        for (GroupSummary group: groups) {
            Log.d(TAG, "id = " + group.getId() + " title = " + group.getTitle() + " size = " + group.getCount());
        }
        Log.d(TAG, "done.");
    }

    public void testExport() throws IOException {
        new Contacts(getContext()).exportContacts();
    }

    public void testGroupsMembers() {
        new Contacts(getContext()).getGroupMembers(6);
    }


    public void testMyExport() throws IOException {
        new Contacts(getContext()).myExportContacts();
    }
}