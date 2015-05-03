package com.nightwind.contacts.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.contacts.R;
import com.nightwind.contacts.activity.ContactActivity;
import com.nightwind.contacts.activity.MainToolbarActivity;
import com.nightwind.contacts.activity.PersonAddActivity;
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactsLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends MainToolbarActivity.PlaceholderFragment {

    RecyclerView recyclerView;
    private ArrayList<Contact> contacts;
    private ContactsAdapter adapter;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contacts = new ArrayList<>();

        initData();

        adapter = new ContactsAdapter(getActivity(), contacts);
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

    private class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

        private Context context;
        private final List<Contact> contacts;

        public ContactsAdapter(Context context, List<Contact> contacts) {
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
            final Contact contact = contacts.get(i);
            contactViewHolder.bindView(contact);
            contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getFragmentManager().beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_bottom, R.anim.slide_out_top)
//                            .add(R.id.container, ContactFragment.newInstance(contact.getLookupUri()), ContactFragment.class.getSimpleName())
//                            .addToBackStack(null)
//                            .commit();
                    Intent intent = new Intent(getActivity(), ContactActivity.class);
                    intent.putExtra(ContactActivity.ARG_CONTACT_LOOKUP_URI, contact.getLookupUri());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, 0);
                }
            });
            contactViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new AlertDialog.Builder(getActivity()).setMessage(R.string.person_edit)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(), PersonAddActivity.class);
                                    intent.putExtra(PersonAddActivity.ARG_CONTACT_LOOKUP_URI,
                                            contact.getLookupUri());
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

    }


    private class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView photo;


        public ContactViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.data);
            photo = (ImageView) itemView.findViewById(R.id.photo);
        }

        public void bindView(Contact contact) {
            name.setText(contact.getName());
            if (contact.getPhotoUri() != null) {
                photo.setImageURI(Uri.parse(contact.getPhotoUri()));
            } else {
                photo.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

}
