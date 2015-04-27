package com.nightwind.contacts.fragment;


import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.Loader;
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
import com.nightwind.contacts.model.ContactsLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends MainActivity.PlaceholderFragment {

    RecyclerView recyclerView;
    private ArrayList<Contact> contacts;
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

        initData();

        adapter = new ContactAdapter(getActivity(), contacts);
        recyclerView.setAdapter(adapter);

        return  v;
    }

    private void initData() {
//        ContentResolver resolver = getActivity().getContentResolver();
//        String projection[] = new String[] {Phone._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL, Phone.DISPLAY_NAME};
////        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
//        new ContactsAsyncQueryHandler(resolver, this).startQuery(0, null, Phone.CONTENT_URI, projection, null, null, null);

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<List<Contact>>() {

            @Override
            public Loader<List<Contact>> onCreateLoader(int id, Bundle args) {
                return new ContactsLoader(getActivity());
            }

            @Override
            public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> data) {
                contacts.clear();
                contacts.addAll(data);
                refreshData();
            }

            @Override
            public void onLoaderReset(Loader<List<Contact>> loader) {

            }
        });
    }


//    static private class ContactsAsyncQueryHandler extends AsyncQueryHandler {
//
//        private WeakReference<ContactFragment> contactFragment;
//
//        public ContactsAsyncQueryHandler(ContentResolver cr, ContactFragment contactFragment) {
//            super(cr);
//            this.contactFragment = new WeakReference<>(contactFragment);
//        }
//
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            super.onQueryComplete(token, cookie, cursor);
//
//            try {
//                cursor.moveToFirst();
//                do {
//                    Contact contact = new Contact();
//                    contact.name = (cursor.getString(4));
//                    contact.phoneNumber = (cursor.getString(1));
//                    contactFragment.get().contacts.add(contact);
//                } while (cursor.moveToNext());
//            } finally {
//                cursor.close();
//            }
//            contactFragment.get().refreshData();
//        }
//
//    }

    private void refreshData() {
        adapter.notifyDataSetChanged();
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

        private Context context;
        private final List<Contact> contacts;

        public ContactAdapter(Context context, List<Contact> contacts) {
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

        public void bindView(Contact contact) {
            itemView.setOnClickListener(onClickListener);
            name.setText(contact.getName());
            if (contact.getPhotoUri() != null) {
                photo.setImageURI(Uri.parse(contact.getPhotoUri()));
            }
        }
    }

}
