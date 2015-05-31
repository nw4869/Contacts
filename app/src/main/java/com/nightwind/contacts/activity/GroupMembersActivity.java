package com.nightwind.contacts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactsFragment;
import com.nightwind.contacts.model.Contacts;

public class GroupMembersActivity extends AppCompatActivity {

    public static final String ARG_GROUP_ID = "group_id";
    private long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        groupId = getIntent().getLongExtra(ARG_GROUP_ID, 0);
        String groupTitle = new Contacts(this).getGroupTitle(groupId);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ContactsFragment.newInstance(groupId))
                .commit();
    }
}
