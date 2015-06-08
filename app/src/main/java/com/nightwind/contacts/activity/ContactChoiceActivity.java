package com.nightwind.contacts.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactsFragment;

import java.util.List;

public class ContactChoiceActivity extends AppCompatActivity implements ContactsFragment.ChoiceCallbacks{

    public static final String ARG_CHOICE_ARRAY = "choice_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_choice);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ContactsFragment.newChoiceModeInstance())
                .commit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_choice, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChoiceFinish(List<Long> idList) {
        Intent intent = new Intent();
        Long[] rawIds = idList.toArray(new Long[idList.size()]);
        intent.putExtra(ARG_CHOICE_ARRAY, rawIds);
        setResult(RESULT_OK, intent);
        finish();
    }
}
