package com.nightwind.contacts.fragment;


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.contacts.R;
import com.nightwind.contacts.activity.MainActivity;
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends MainActivity.PlaceholderFragment {

    RecyclerView recyclerView;
    private ArrayList<ContactEntity> contacts;
    private ContactAdapter adapter;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contacts = new ArrayList<>();

        // init dummy data
        initDummyData();

        adapter = new ContactAdapter(getActivity(), contacts);
        recyclerView.setAdapter(adapter);

        return  v;
    }

    private void initDummyData() {
//        String[] contactNames = getResources().getStringArray(R.array.contactName);
//        for (String contactName: contactNames) {
//            contacts.add(new ContactEntity(contactName, null));
//        }

        ContentResolver resolver = getActivity().getContentResolver();
        String projection[] = new String[] {Phone._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL, Phone.DISPLAY_NAME};

//        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
        new ContactsAsyncQueryHandler(resolver, this).startQuery(0, null, Phone.CONTENT_URI, projection, null, null, null);
    }

    static private class ContactsAsyncQueryHandler extends AsyncQueryHandler {

        private WeakReference<ContactFragment> contactFragment;

        public ContactsAsyncQueryHandler(ContentResolver cr, ContactFragment contactFragment) {
            super(cr);
            this.contactFragment = new WeakReference<>(contactFragment);
        }


        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

            try {
                cursor.moveToFirst();
                do {
                    ContactEntity contact = new ContactEntity();
                    contact.name = (cursor.getString(4));
                    contact.phoneNumber = (cursor.getString(1));
                    contactFragment.get().contacts.add(contact);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
            contactFragment.get().refreshData();
        }

//        protected List<ContactEntity> createContactEntryFromCursor(Cursor cursor) {
//            List<ContactEntity> contacts = new ArrayList<>();
//            if (!cursorIsValid(cursor)) {
//                return new ArrayList<>();
//            }
//            try {
//                cursor.moveToFirst();
//                do {
//                    ContactEntity contact = new ContactEntity();
//                    contact.name = (cursor.getString(4));
//                    contact.phoneNumber = (cursor.getString(1));
//                    contacts.add(contact);
//                } while (cursor.moveToNext());
//            } finally {
//                cursor.close();
//            }
//
//            return contacts;
//        }
//
//        private boolean cursorIsValid(Cursor cursor) {
//            return cursor != null && !cursor.isClosed();
//        }
    }

    private void refreshData() {
        adapter.notifyDataSetChanged();
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

        private Context context;
        private final List<ContactEntity> contacts;

        public ContactAdapter(Context context, List<ContactEntity> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);
            return new ContactViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            contactViewHolder.bindView(contacts.get(i));
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

    }


    private class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView photo;

        private View.OnClickListener onClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public ContactViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            photo = (ImageView) itemView.findViewById(R.id.photo);
        }

        public void bindView(ContactEntity contactEntity) {
            itemView.setOnClickListener(onClickListener);
            name.setText(contactEntity.name);
        }
    }

}
