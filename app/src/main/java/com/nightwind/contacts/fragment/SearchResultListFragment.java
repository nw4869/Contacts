package com.nightwind.contacts.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.contacts.R;
import com.nightwind.contacts.activity.ContactActivity;
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactsPhoneLoader;
import com.nightwind.contacts.model.dataitem.DataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultListFragment extends Fragment {
    private static final String ARG_QUERY_STRING = "param1";

    private String mQueryString;

    private RecyclerView recyclerView;
    private List<ContactPhoneEntity> contactPhones;
    private ResultContactsAdapter adapter;

    class ContactPhoneEntity {
        String name;
        String lookupKey;
        String photoUri;
        String phoneNumber;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param queryString Parameter 1.
     * @return A new instance of fragment SearchResultListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultListFragment newInstance(String queryString) {
        SearchResultListFragment fragment = new SearchResultListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY_STRING, queryString);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchResultListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQueryString = getArguments().getString(ARG_QUERY_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_result_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contactPhones = new ArrayList<>();

        initData(mQueryString);

        adapter = new ResultContactsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return  v;
    }

    private void initData(String queryString) {

        Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY_STRING, queryString);

        getLoaderManager().restartLoader(0, bundle, new LoaderManager.LoaderCallbacks<List<Contact>>() {

            @Override
            public Loader<List<Contact>> onCreateLoader(int id, Bundle args) {
                return new ContactsPhoneLoader(getActivity(), args.getString(ARG_QUERY_STRING));
            }

            @Override
            public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> data) {
                refreshData(data);
            }

            @Override
            public void onLoaderReset(Loader<List<Contact>> loader) {

            }
        });

    }

    private void refreshData(List<Contact> contacts) {
        contactPhones.clear();
        for (Contact contact: contacts) {
            if (contact.getData() != null) {
                for (DataItem dataItem: contact.getData()) {
                    if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        ContactPhoneEntity entity = new ContactPhoneEntity();
                        entity.name = contact.getName();
                        entity.lookupKey = contact.getLookupUri();
                        entity.photoUri = contact.getPhotoUri();
                        entity.phoneNumber = dataItem.getData();
                        contactPhones.add(entity);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    private class ResultContactsAdapter extends RecyclerView.Adapter {
        private final Context context;

        public ResultContactsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_search_result_list, parent, false);
            return new ContactPhoneViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ContactPhoneEntity lastEntity = null;
            if (position > 0) {
                lastEntity = contactPhones.get(position-1);
            }
            final ContactPhoneEntity entity = contactPhones.get(position);
            ContactPhoneViewHolder cpViewHolder = (ContactPhoneViewHolder) holder;

            cpViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ContactActivity.class);
                    intent.putExtra(ContactActivity.ARG_CONTACT_LOOKUP_URI, entity.lookupKey);
                    startActivity(intent);
                }
            });

            if (lastEntity == null || !lastEntity.lookupKey.equals(entity.lookupKey)) {
                // 正常
                cpViewHolder.name.setVisibility(View.VISIBLE);
                cpViewHolder.photo.setVisibility(View.VISIBLE);
                if (entity.photoUri != null) {
                    cpViewHolder.photo.setImageURI(Uri.parse(entity.photoUri));
                } else {
                    cpViewHolder.photo.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                // 属于上一个entity同一个联系人
                cpViewHolder.name.setVisibility(View.GONE);
                cpViewHolder.photo.setVisibility(View.INVISIBLE);
            }

            cpViewHolder.name.setText(entity.name);
            cpViewHolder.phoneNumber.setText(entity.phoneNumber);
        }

        @Override
        public int getItemCount() {
            return contactPhones.size();
        }
    }


    private class ContactPhoneViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView photo;
        public TextView phoneNumber;

        public ContactPhoneViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.data);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            phoneNumber = (TextView) itemView.findViewById(R.id.number);
        }
    }
}
