package com.nightwind.contacts.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.nightwind.contacts.model.Contact;
import com.nightwind.contacts.model.ContactLoader;
import com.nightwind.contacts.model.dataitem.DataItem;
import com.nightwind.contacts.model.dataitem.EmailDataItem;
import com.nightwind.contacts.model.dataitem.PhoneDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private Contact contact;
//    private List<DataItem> dataItems = new ArrayList<>();
    private ContactAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadContact();

        recyclerView.setAdapter(new ContactAdapter(getActivity(), contact));

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final List<DataItem> dataItems;
        private final int VIEW_TYPE_PHOTO = 1;
        private final int VIEW_TYPE_NORMAL = 0;
        private final Contact contact;

        public ContactAdapter(Context context, Contact contact) {
            this.context = context;
            this.contact = contact;
            this.dataItems = contact.getData();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == VIEW_TYPE_NORMAL) {
                View view = LayoutInflater.from(context).inflate(R.layout.photo_image, parent, false);
                viewHolder = new DataItemViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_dataitem, parent, false);
                viewHolder = new DataItemViewHolder(view);
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_PHOTO;
            } else {
                return VIEW_TYPE_NORMAL;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == VIEW_TYPE_PHOTO) {
                PhotoViewHolder viewHolder = (PhotoViewHolder) holder;
                if (contact.getPhotoUri() != null) {
                    viewHolder.imageView.setImageURI(Uri.parse(contact.getPhotoUri()));
                }
            } else {
                DataItem dataItem = dataItems.get(position - 1);
                DataItemViewHolder viewHolder = (DataItemViewHolder) holder;
                if (dataItem instanceof PhoneDataItem) {
                    PhoneDataItem phoneDataItem = (PhoneDataItem) dataItem;
                    viewHolder.label.setText(phoneDataItem.getLabel());
                    viewHolder.data.setText(phoneDataItem.getNumber());
                } else if (dataItem instanceof EmailDataItem) {
                    EmailDataItem emailDataItem = (EmailDataItem) dataItem;
                    viewHolder.label.setText(emailDataItem.getLabel());
                    viewHolder.data.setText(emailDataItem.getAddress());
                }
            }
        }

        @Override
        public int getItemCount() {
            return dataItems.size();
        }
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo);
        }
    }

    private class DataItemViewHolder extends RecyclerView.ViewHolder{
        public final ImageView typeImage;
        public final TextView data;
        public final TextView label;
        public final ImageView actionImage;

        public DataItemViewHolder(View itemView) {
            super(itemView);
            typeImage = (ImageView) itemView.findViewById(R.id.typeImage);
            data = (TextView) itemView.findViewById(R.id.data);
            label = (TextView) itemView.findViewById(R.id.label);
            actionImage = (ImageView) itemView.findViewById(R.id.actionImage);
        }
    }


    private void loadContact() {
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Contact>() {
            @Override
            public Loader<Contact> onCreateLoader(int id, Bundle args) {
                return new ContactLoader(getActivity());
            }

            @Override
            public void onLoadFinished(Loader<Contact> loader, Contact data) {
                List<DataItem> dataItems = contact.getData();
//                dataItems.clear();
//                dataItems.addAll(data.getData());
                contact.setData(data.getData());
                contact.setName(data.getName());
                contact.setPhotoUri(data.getPhotoUri());
                refreshData();
            }

            @Override
            public void onLoaderReset(Loader<Contact> loader) {

            }
        });
    }

    private void refreshData() {
        adapter.notifyDataSetChanged();
    }
}
