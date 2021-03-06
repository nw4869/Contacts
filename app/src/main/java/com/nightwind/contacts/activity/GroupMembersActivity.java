package com.nightwind.contacts.activity;

import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactsFragment;
import com.nightwind.contacts.model.Contacts;

public class GroupMembersActivity extends AppCompatActivity {

    public static final String ARG_GROUP_ID = "group_id";
    private static final int REQUEST_CHOICE = 0;
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

        findViewById(R.id.person_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click to add contact to group
                // TODO show contact list, select contacts and then add all to the group
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container, ContactsFragment.newChoiceModeInstance(),
//                                ContactsFragment.class.getSimpleName())
//                        .addToBackStack(ContactsFragment.class.getSimpleName())
//                        .commit();
                Intent intent = new Intent(GroupMembersActivity.this, ContactChoiceActivity.class);
                startActivityForResult(intent, REQUEST_CHOICE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOICE && resultCode == RESULT_OK) {
            Long[] idList = (Long[]) data.getSerializableExtra(ContactChoiceActivity.ARG_CHOICE_ARRAY);
            for (long id: idList) {
                Log.d("GroupMembersActivity", String.valueOf(id));
            }

            int toastRes = R.string.add_success;
            try {
                new Contacts(this).addToGroup(groupId, idList);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                toastRes = R.string.add_failed;
            }

            Toast.makeText(this, toastRes, Toast.LENGTH_SHORT).show();
        }
    }
}
