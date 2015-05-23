package com.nightwind.contacts.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactEditorFragment;

public class PersonAddActivity extends AppCompatActivity {

    public static final String ARG_CONTACT_LOOKUP_URI = "ARG_CONTACT_LOOKUP_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        getSupportActionBar().setHomeButtonEnabled(true);//设置点击有效

        String lookupUri = null;
        Intent intent = getIntent();
        if (intent != null) {
            lookupUri = intent.getStringExtra(ARG_CONTACT_LOOKUP_URI);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ContactEditorFragment.newInstance(lookupUri))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
