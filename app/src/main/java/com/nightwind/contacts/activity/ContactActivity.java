package com.nightwind.contacts.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactFragment;
import com.nightwind.contacts.model.Contacts;

public class ContactActivity extends AppCompatActivity {

    public static final String ARG_CONTACT_LOOKUP_URI = "ARG_CONTACT_LOOKUP_URI";
    private String lookupUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回箭头
        getSupportActionBar().setHomeButtonEnabled(true);

        lookupUri = getIntent().getStringExtra(ARG_CONTACT_LOOKUP_URI);

        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_bottom, R.anim.slide_out_top)
                .replace(R.id.container, ContactFragment.newInstance(lookupUri), ContactFragment.class.getSimpleName())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }


}
