package com.nightwind.contacts;

import android.annotation.TargetApi;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;

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

    public void testReadVCard() {
        
    }

}
