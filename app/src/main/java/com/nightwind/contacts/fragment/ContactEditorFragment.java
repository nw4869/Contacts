package com.nightwind.contacts.fragment;


import android.os.Bundle;
import android.app.Fragment;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_editor, container, false);
    }


}
