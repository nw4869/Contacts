package com.nightwind.contacts.fragment;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.nightwind.contacts.R;
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;
import com.nightwind.contacts.widget.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nightwind.contacts.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactEditorFragment extends Fragment {

    private static final String ARG_CONTACT_LOOKUP_URI = "ARG_CONTACT_LOOKUP_URI";

    private String mContactLookupUri;

    private RecyclerView recyclerView;
    private Contact contact;
    private RecyclerView.Adapter adapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lookupUri Parameter 1.
     * @return A new instance of fragment ContactEditorFragment.
     */
    public static ContactEditorFragment newInstance(String lookupUri) {
        ContactEditorFragment fragment = new ContactEditorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_LOOKUP_URI, lookupUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContactLookupUri = getArguments().getString(ARG_CONTACT_LOOKUP_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContactLookupUri == null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.person_add);
        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.person_edit);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_editor, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contact = new Contact();
        contact.setData(new ArrayList<DataItem>());

        loadContact();

        adapter = new ContactEditorAdapter(getActivity(), contact);
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void loadContact() {

    }


    private class ContactEditorAdapter extends RecyclerView.Adapter {

        private final Context context;
        private final List<DataItem> phoneItems = new ArrayList<>();
        private final List<DataItem> emailItems = new ArrayList<>();
        private final Contact contact;

        private final int VIEW_TYPE_NAME = 0;
        private final int VIEW_TYPE_PHONE = 1;
        private final int VIEW_TYPE_EMAIL = 2;

        public ContactEditorAdapter(Context context, Contact contact) {
            this.context = context;
            this.contact = contact;
            List<DataItem> dataItems = contact.getData();
            for (DataItem dataItem: dataItems) {
                if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Phone.MIMETYPE)) {
                    phoneItems.add(dataItem);
                } else if (dataItem.getMimeType().equals(ContactsContract.CommonDataKinds.Email.MIMETYPE)) {
                    emailItems.add(dataItem);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            RecyclerView.ViewHolder vh;
            switch (viewType) {
                case VIEW_TYPE_NAME:
                    vh = new NormalEditorViewHolder(inflater.inflate(R.layout.item_editor_name, parent, false));
                    break;
                default:
//                    vh = new SpinnerDataEditorViewHolder(inflater.inflate(R.layout.item_editor_data, parent, false));
                    vh = new RecyclerEditorViewHolder(inflater.inflate(R.layout.recyclerview, parent, false));
                    break;
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_NAME:
                    bindNameEditor((NormalEditorViewHolder) holder);
                    break;
                case VIEW_TYPE_PHONE:
                case VIEW_TYPE_EMAIL:
                    bindRecyclerViewEditor((RecyclerEditorViewHolder) holder, holder.getItemViewType());
                    break;

            }
        }

        private void bindNameEditor(NormalEditorViewHolder holder) {

        }

        private void bindRecyclerViewEditor(RecyclerEditorViewHolder holder, int type) {
            holder.recyclerView.setLayoutManager(new FullyLinearLayoutManager(context));
//            switch (type) {
//                case VIEW_TYPE_PHONE:
//                    break;
//            }
            holder.recyclerView.setAdapter(new recyclerEditorAdapter(context, phoneItems, type));
        }

        private void bindPhoneEditor(SpinnerDataEditorViewHolder holder) {
            holder.typeImage.setImageResource(R.drawable.ic_action_call);
        }


        @Override
        public int getItemCount() {
            // 现只有name，phone，email
            return 3;
        }

        private class recyclerEditorAdapter extends RecyclerView.Adapter {

            private final int type;
            private Context context;
            private List<DataItem> dataItems;

            public recyclerEditorAdapter(Context context, List<DataItem> dataItems, int type) {
                this.context = context;
                this.dataItems = dataItems;
                this.type = type;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_editor_data, parent, false);
                return new SpinnerDataEditorViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SpinnerDataEditorViewHolder viewHolder = (SpinnerDataEditorViewHolder) holder;
                int typeImageResource = 0;
                String textHint = "";
                if (type == VIEW_TYPE_PHONE) {
                    typeImageResource = R.drawable.ic_action_call;
                    textHint = "电话";
                } else if (type == VIEW_TYPE_EMAIL) {
                    typeImageResource = R.drawable.ic_action_mail;
                    textHint = "邮箱";
                }
                viewHolder.typeImage.setImageResource(typeImageResource);
                viewHolder.data.setHint(textHint);
                List<String> spinnerList = new ArrayList<>();
                spinnerList.add("hello");
                spinnerList.add("world");
                viewHolder.spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerList));
            }

            @Override
            public int getItemCount() {
                return dataItems.size() + 2;
            }
        }
    }

    private class NormalEditorViewHolder extends RecyclerView.ViewHolder {

        public ImageView typeImage;
        public EditText data;
        public ImageView deleteImage;

        public NormalEditorViewHolder(View itemView) {
            super(itemView);
            typeImage = (ImageView) itemView.findViewById(R.id.typeImage);
            data = (EditText) itemView.findViewById(R.id.data);
            deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
        }
    }

    private class SpinnerDataEditorViewHolder extends NormalEditorViewHolder {

        public Spinner spinner;

        public SpinnerDataEditorViewHolder(View itemView) {
            super(itemView);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
        }
    }

    private class RecyclerEditorViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recyclerView;

        public RecyclerEditorViewHolder(View itemView) {
            super(itemView);
//            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            recyclerView = (RecyclerView) itemView;
        }
    }
}
