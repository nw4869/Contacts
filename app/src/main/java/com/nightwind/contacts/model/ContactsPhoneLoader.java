package com.nightwind.contacts.model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by nightwind on 15/5/9.
 */
public class ContactsPhoneLoader extends AsyncTaskLoader<List<Contact>> {
    private final String queryString;

    public ContactsPhoneLoader(Context context, String queryString) {
        super(context);
        this.queryString = queryString;
    }

    @Override
    public List<Contact> loadInBackground() {
        return new Contacts(getContext()).searchContact(queryString);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
