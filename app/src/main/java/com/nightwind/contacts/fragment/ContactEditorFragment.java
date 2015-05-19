package com.nightwind.contacts.fragment;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nightwind.contacts.R;
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactLoader;
import com.nightwind.contacts.model.Contacts;
import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.EmailDataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;
import com.nightwind.contacts.widget.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final List<DataItemEntity> phoneItems = new ArrayList<>();
    private final List<DataItemEntity> emailItems = new ArrayList<>();
    private final Set<Long> deleteDataId = new HashSet<>();

    private String originName;

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

        setHasOptionsMenu(true);
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
        phoneItems.add(new DataItemEntity());
        emailItems.add(new DataItemEntity());

        if (mContactLookupUri != null) {
            loadContact();
        }

        adapter = new ContactEditorAdapter(getActivity(), contact);
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void loadContact() {

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Contact>() {
            @Override
            public Loader<Contact> onCreateLoader(int id, Bundle args) {
                return new ContactLoader(getActivity(), mContactLookupUri);
            }

            @Override
            public void onLoadFinished(Loader<Contact> loader, Contact data) {
                List<DataItem> dataItems = contact.getData();
                dataItems.clear();
                dataItems.addAll(data.getData());
//                contact.setData(data.getData());
                contact.setId(data.getId());
                contact.setRawContactId(data.getRawContactId());
                contact.setName(data.getName());
                originName = data.getName();
                contact.setPhotoUri(data.getPhotoUri());
                ((ContactEditorAdapter)adapter).refreshData();
            }

            @Override
            public void onLoaderReset(Loader<Contact> loader) {

            }
        });
    }


    class DataItemEntity {
        public long id;
        public String data;
        public String label;
        public int labelType = 1;
        public int newLabelType = 1;
    }


    private class ContactEditorAdapter extends RecyclerView.Adapter {

        private final Context context;
        private final Contact contact;

        private final int VIEW_TYPE_NAME = 0;
        private final int VIEW_TYPE_PHONE = 1;
        private final int VIEW_TYPE_EMAIL = 2;


        public ContactEditorAdapter(Context context, Contact contact) {
            this.context = context;
            this.contact = contact;
            List<DataItem> dataItems = contact.getData();
            loadEntitiesFromDataItems(dataItems);
        }

        public void loadEntitiesFromDataItems(List<DataItem> dataItems) {

            phoneItems.clear();
            emailItems.clear();
            for (DataItem dataItem: dataItems) {
                if (dataItem.getMimeType().equals(Phone.CONTENT_ITEM_TYPE)) {
//                    phoneItems.add(dataItem);
                    DataItemEntity item = new DataItemEntity();
                    item.id = dataItem.getId();
                    item.data = ((PhoneDataItem)dataItem).getNumber();
                    item.label = ((PhoneDataItem)dataItem).getLabel();
                    item.labelType = ((PhoneDataItem)dataItem).getType();
                    phoneItems.add(item);
                } else if (dataItem.getMimeType().equals(Email.CONTENT_ITEM_TYPE)) {
//                    emailItems.add(dataItem);
                    DataItemEntity item = new DataItemEntity();
                    item.id = dataItem.getId();
                    item.data = ((EmailDataItem)dataItem).getAddress();
                    item.label = ((EmailDataItem)dataItem).getLabel();
                    item.labelType = ((EmailDataItem)dataItem).getType();
                    emailItems.add(item);
                }
            }
            phoneItems.add(new DataItemEntity());
            emailItems.add(new DataItemEntity());
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
            holder.data.setText(contact.getName());
            holder.data.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    contact.setName(String.valueOf(s));
                }
            });
        }

        private void bindRecyclerViewEditor(RecyclerEditorViewHolder holder, int type) {
            holder.recyclerView.setLayoutManager(new FullyLinearLayoutManager(context));
            switch (type) {
                case VIEW_TYPE_PHONE:
                    holder.recyclerView.setAdapter(new recyclerEditorAdapter(context, phoneItems, type));
                    break;
                case VIEW_TYPE_EMAIL:
                    holder.recyclerView.setAdapter(new recyclerEditorAdapter(context, emailItems, type));
                    break;
            }
        }

        private void bindPhoneEditor(SpinnerDataEditorViewHolder holder) {
            holder.typeImage.setImageResource(R.drawable.ic_action_call);
        }


        @Override
        public int getItemCount() {
            // 现只有name，phone，email
            return 3;
        }


        public void refreshData() {

            List<DataItem> dataItems = contact.getData();
            loadEntitiesFromDataItems(dataItems);
            notifyDataSetChanged();
        }

        public boolean isEditMode() {
            return mContactLookupUri != null;
        }

        private class recyclerEditorAdapter extends RecyclerView.Adapter {

            private final int type;
            private Context context;
            private List<DataItemEntity> dataItems;

            public recyclerEditorAdapter(Context context, List<DataItemEntity> dataItems, int type) {
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
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                final DataItemEntity item = dataItems.get(position);
                final SpinnerDataEditorViewHolder viewHolder = (SpinnerDataEditorViewHolder) holder;
                int typeImageResource = 0;
                String textHint = "";
                List<String> spinnerList = new ArrayList<>();
                if (type == VIEW_TYPE_PHONE) {
                    typeImageResource = R.drawable.ic_action_call;
                    textHint = "电话";

                    // add spinner type label
                    for (int i = 0; i <= Phone.TYPE_OTHER; i++) {
                        if (i > 0 || isEditMode() && item.labelType == Phone.TYPE_CUSTOM) {
                            CharSequence label = Phone.getTypeLabel(getResources(), i, item.label);
                            spinnerList.add(String.valueOf(label));
                        }
                    }

                    ((SpinnerDataEditorViewHolder) holder).data.setText(item.data);
                } else if (type == VIEW_TYPE_EMAIL) {
                    typeImageResource = R.drawable.ic_action_mail;
                    textHint = "邮箱";

                    // add spinner type lable
                    for (int i = 0; i <= Email.TYPE_MOBILE; i++) {
                        if (i > 0 || isEditMode() && item.labelType == Email.TYPE_CUSTOM) {
                            CharSequence label = Email.getTypeLabel(getResources(), i, item.label);
                            spinnerList.add(String.valueOf(label));
                        }
                    }

                    ((SpinnerDataEditorViewHolder) holder).data.setText(item.data);
                }

                // set deleteImage action and visible or not
                if (position < getItemCount() - 1) {
                    viewHolder.deleteImage.setImageResource(R.drawable.ic_close);

                } else {
                    viewHolder.deleteImage.setImageDrawable(null);
                }
                viewHolder.deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = dataItems.indexOf(item);
                        dataItems.remove(position);
                        if (item.id != 0) {
                            deleteDataId.add(item.id);
                        }
                        notifyItemRemoved(position);
                        if (position == 0) {
                            notifyItemChanged(0);
                        }
                    }
                });

                // set spinner
                viewHolder.spinner.setAdapter(new ArrayAdapter<>(context, R.layout.item_spinner_text, spinnerList));
                viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (item.labelType == ContactsContract.CommonDataKinds.BaseTypes.TYPE_CUSTOM) {
                            item.newLabelType = position;
                        } else {
                            item.newLabelType = position + 1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (item.labelType == ContactsContract.CommonDataKinds.BaseTypes.TYPE_CUSTOM) {
                    viewHolder.spinner.setSelection(item.labelType);
                } else {
                    viewHolder.spinner.setSelection(item.labelType - 1);
                }

                // set type image
                if (position > 0) {
                    viewHolder.typeImage.setImageDrawable(null);
                } else {
                    viewHolder.typeImage.setImageResource(typeImageResource);
                }

                // set data
                viewHolder.data.setHint(textHint);
                final int finalTypeImageResource = typeImageResource;
                viewHolder.data.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int position = dataItems.indexOf(item);
                        if (position == dataItems.size()-1) {
//                            dataItems.add(DataItem.EMPTY_DATA_ITEM);
                            dataItems.add(new DataItemEntity());
                            notifyItemInserted(position + 1);
                            viewHolder.deleteImage.setImageResource(R.drawable.ic_close);
                            if (position > 0) {
                                viewHolder.typeImage.setImageDrawable(null);
                            } else {
                                viewHolder.typeImage.setImageResource(finalTypeImageResource);
                            }
                        }
                        item.data = String.valueOf(s);
                    }
                });

            }

            @Override
            public int getItemCount() {
                return dataItems.size();
            }

            private class DataEditWatcher implements TextWatcher {

                private final int position;

                public DataEditWatcher(int position) {
                    this.position = position;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (position == dataItems.size()-1) {
//                            dataItems.add(DataItem.EMPTY_DATA_ITEM);
                        dataItems.add(new DataItemEntity());
                        notifyItemInserted(position + 1);
                    }
                    dataItems.get(position).data = String.valueOf(s);
                }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_person_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            //检查是否为空
            if (isEmpty()) {
                Toast.makeText(getActivity(), R.string.warn_contact_can_not_empty,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            String newName = contact.getName();
            if (!newName.equals(originName) && nameIsExist(newName)) {
                Toast.makeText(getActivity(), R.string.name_is_exist,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            try {
                saveContact();
                mContactLookupUri = new Contacts(getActivity()).getLookupKey(newName);
                getActivity().setResult(Activity.RESULT_OK,
                        new Intent().putExtra("lookupKey", mContactLookupUri));
                getActivity().finish();
                Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty() {
        return TextUtils.isEmpty(contact.getName()) || phoneItems.size() == 1 /*&& emailItems.size() == 1*/;
    }

    private boolean nameIsExist(String name) {
        return TextUtils.isEmpty(name) || new Contacts(getActivity()).nameIsExist(name);
    }

    private boolean isInsertingContact() {
        return mContactLookupUri == null;
    }

    private void saveContact() throws RemoteException, OperationApplicationException {

        // 重新生成dataItem
        List<DataItem> dataItems = new ArrayList<>();
        for (DataItemEntity item: phoneItems) {
            if (phoneItems.indexOf(item) == phoneItems.size() -1) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(ContactsContract.Data._ID, item.id);
            cv.put(Phone.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            cv.put(Phone.NUMBER, item.data);
            cv.put(Phone.TYPE, item.newLabelType);
            cv.put(Phone.LABEL, item.label);
            dataItems.add(DataItem.createFrom(cv));
        }

        for (DataItemEntity item: emailItems) {
            if (emailItems.indexOf(item) == emailItems.size() -1) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(ContactsContract.Data._ID, item.id);
            cv.put(Email.MIMETYPE, Email.CONTENT_ITEM_TYPE);
            cv.put(Email.ADDRESS, item.data);
            cv.put(Email.TYPE, item.newLabelType);
            cv.put(Email.LABEL, item.label);
            dataItems.add(DataItem.createFrom(cv));
        }

        //delete data
        for (Long id : deleteDataId) {
            if (id > 0) {
                ContentValues cv = new ContentValues();
                cv.put(ContactsContract.Data._ID, id);
                dataItems.add(DataItem.createFrom(cv));

            }
        }

        contact.setData(dataItems);

        if (mContactLookupUri != null) {
            // update contact
            new Contacts(getActivity()).updateContact(contact);
        } else {
            // insert contact
            new Contacts(getActivity()).saveContact(contact.getName(), null, dataItems);
        }

    }
}
