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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.nightwind.contacts.activity.MainToolbarActivity.PlaceholderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends MainToolbarActivity.PlaceholderFragment {

    private boolean starred = false;

    RecyclerView recyclerView;
    private ArrayList<Contact> contacts;
    private ContactsAdapter adapter;
    private long groupId;
    private View emptyView;
    private boolean choiceMode;

    public ContactsFragment() {
        // Required empty public constructor
    }
    private static final String ARG_STARRED = "starred";
    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_GROUP_CHOICE_MODE = "group_choice_mode";


    public static ContactsFragment newInstance( boolean starred) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_STARRED, starred);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * show group members
     */
    public static ContactsFragment newInstance(long groupId) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ContactsFragment newChoiceModeInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_GROUP_CHOICE_MODE, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_contacts, container, false);
        emptyView = v.findViewById(R.id.emptyView);

        // get args
        starred = getArguments().getBoolean(ARG_STARRED, false);
        groupId = getArguments().getLong(ARG_GROUP_ID, 0);
        choiceMode = getArguments().getBoolean(ARG_GROUP_CHOICE_MODE, false);

        if (choiceMode) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate a menu resource providing context menu items
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_choice_mode, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    adapter.clearSelections();
                }
            });
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contacts = new ArrayList<>();

        loadData();

        adapter = new ContactsAdapter(getActivity(), contacts);
        recyclerView.setAdapter(adapter);

        return  v;
    }

    public void loadData() {

        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Contact>>() {

            @Override
            public Loader<List<Contact>> onCreateLoader(int id, Bundle args) {
                return new ContactsLoader(getActivity(), starred, groupId);
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

    @Override
    protected void reloadData() {
        super.reloadData();
        loadData();
    }


    private void refreshData() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
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
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int position) {
            final Contact contact = contacts.get(position);
            contactViewHolder.bindView(contact);
            if (!choiceMode) {
                //normal mode
                contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ContactActivity.class);
                        intent.putExtra(ContactActivity.ARG_CONTACT_LOOKUP_URI, contact.getLookupUri());
                        startActivity(intent);
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
            } else {
                // choice mode
                contactViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
            }
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        /**
         * for choice mode
         */

        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public void toggleSelection(int pos) {
            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
            }
            else {
                selectedItems.put(pos, true);
            }
            notifyItemChanged(pos);
        }

        public void clearSelections() {
            selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return selectedItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); i++) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        }

    }


    static class ContactViewHolder extends RecyclerView.ViewHolder {

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
