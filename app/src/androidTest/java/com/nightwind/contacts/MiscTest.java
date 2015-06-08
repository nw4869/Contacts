package com.nightwind.contacts;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.test.AndroidTestCase;
import android.util.Log;

import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.Contacts;
import com.nightwind.contacts.model.dataitem.DataItem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nightwind on 15/6/6.
 */
public class MiscTest extends AndroidTestCase {

    private static final String TAG = MiscTest.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void testFilePath() {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/1110-3806%3Acontacts.vcf");
        final String docId = DocumentsContract.getDocumentId(uri);
        Log.d(TAG, "docId = " + docId);
        final String[] divide = docId.split(":");
        final String type = divide[0];
        if ("primary".equals(type)) {
            String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
            Log.d(TAG, "path = " + path);
        } else {
            Log.d(TAG, "A O");
        }
    }

    public void testReadVCard() throws IOException {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3Acontacts.vcf");
        new Contacts(getContext()).importContacts(uri);
    }

}
