package com.nightwind.contacts.fragment;

import android.app.Activity;
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
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CONTACT_LOOKUP_URI = "ARG_CONTACT_LOOKUP_URI";

    private String mContactLookupUri;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private Contact contact;
//    private List<DataItem> dataItems = new ArrayList<>();
    private ContactAdapter adapter;
    private GestureDetector mGestureDetector;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contactLookupUri Parameter 1.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String contactLookupUri) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_LOOKUP_URI, contactLookupUri);
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
            mContactLookupUri = getArguments().getString(ARG_CONTACT_LOOKUP_URI);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contact = new Contact();
        contact.setData(new ArrayList<DataItem>());

        loadContact();

        adapter = new ContactAdapter(getActivity(), contact);
        recyclerView.setAdapter(adapter);

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
            if (viewType == VIEW_TYPE_PHOTO) {
                View view = LayoutInflater.from(context).inflate(R.layout.photo_image, parent, false);
                viewHolder = new PhotoViewHolder(view);
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
                    Log.d("ContactFragment" , "loadPhoto = " + contact.getPhotoUri());
                } else {
                    Log.d("ContactFragment", "no photo");
                }
            } else {
                DataItem dataItem = dataItems.get(position - 1);
                DataItemViewHolder viewHolder = (DataItemViewHolder) holder;
                if (dataItem instanceof PhoneDataItem) {
                    final PhoneDataItem phoneDataItem = (PhoneDataItem) dataItem;
                    String label = phoneDataItem.getLabel();
                    int type = phoneDataItem.getType();
                    CharSequence displayLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label);
                    viewHolder.label.setText(displayLabel);
                    viewHolder.data.setText(phoneDataItem.getNumber());
                    viewHolder.typeImage.setImageResource(R.drawable.ic_action_call);
                    viewHolder.actionImage.setImageResource(R.drawable.ic_action_sms);
                    viewHolder.actionImage.setVisibility(View.VISIBLE);
                    viewHolder.actionImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("smsto:" + phoneDataItem.getNumber());
                            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
//                            it.putExtra("sms_body", "TheSMS text");
                            startActivity(it);
                        }
                    });
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneDataItem.getNumber()));
                            startActivity(intent);
                        }
                    });
                } else if (dataItem instanceof EmailDataItem) {
                    final EmailDataItem emailDataItem = (EmailDataItem) dataItem;
                    String label = emailDataItem.getLabel();
                    int type = emailDataItem.getType();
                    CharSequence displayLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), type, label);
                    viewHolder.label.setText(displayLabel);
                    viewHolder.data.setText(emailDataItem.getAddress());
                    viewHolder.typeImage.setImageResource(R.drawable.ic_action_mail);
                    viewHolder.actionImage.setVisibility(View.GONE);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(Intent.ACTION_SEND);   //发送邮件使用ACTION_SEND
                            intent.setType("plain/text");                   //设置类型
                            intent.putExtra(Intent.EXTRA_EMAIL, emailDataItem.getAddress());
                            startActivity(intent);
                        }
                    });
                }
                if (position >= 2 && dataItems.get(position - 2).getMimeType().equals(dataItem.getMimeType())) {
                    viewHolder.typeImage.setImageDrawable(null);
                }
            }
        }

        @Override
        public int getItemCount() {
            return dataItems.size() + 1;
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

    public void onPullDown(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        System.out.println("x = " + recyclerView.getX());
//        System.out.println("y = " + recyclerView.getY());
//        System.out.println("scroll x = " + recyclerView.getScrollX());
//        System.out.println("scroll y = " + recyclerView.getScrollY());
        View topView = recyclerView.getChildAt(0);
        int lastOffset = topView.getTop();
        int lastPosition = recyclerView.getLayoutManager().getPosition(topView);
//        System.out.println("lastOffset = " + lastOffset + " lastPosition = " + lastPosition);
//        System.out.println("e1.x = " + e1.getX() + " e1.y = " + e1.getY() + " e2.x = " + e2.getX() + " e2.y = " + e2.getY());
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
