package com.nightwind.contacts;

import android.test.LoaderTestCase;

import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactLoader;
import com.nightwind.contacts.model.ContactsLoader;

import java.util.List;

/**
 * Created by nightwind on 15/4/25.
 */
public class LoaderTest extends LoaderTestCase {
    private static final String TAG = LoaderTest.class.getSimpleName();

    public void testContactsLoader() throws Throwable {
//        List<Contact> contacts = (List<Contact>) getLoaderResultSynchronously(new ContactsLoader(getContext()));

    }
}
