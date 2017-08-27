package com.vsevolod.carbontracker.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.Model.Singleton;

/**
 * Help screen
 */

public class AboutScreen extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);

    private Singleton singleton = Singleton.getInstance();
    private static final String SINGLETON_KEY = "singleton";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        getSingleton(getIntent());
        setUpActionBar();
        setUpFullScreen();
        setCourseWeb();
    }

    private void setCourseWeb() {
        TextView text = (TextView) findViewById(R.id.hyperlink);
        String html = "<a href = 'https://www.sfu.ca/computing.html'>Home Page</a>";
        CharSequence charSequence = Html.fromHtml(html);
        text.setText(charSequence);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setUpFullScreen() {
        if(Build.VERSION.SDK_INT <19){
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        }
        else {
            View higherView = getWindow().getDecorView();
            int fullScreen = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            higherView.setSystemUiVisibility(fullScreen);
        }
    }

    private void setUpActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.title_about);
        //up button /back button for action bar
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void getSingleton(Intent intent) {
        Gson gson = new Gson();
        String json = intent.getStringExtra(SINGLETON_KEY);
        singleton = gson.fromJson(json, singleton.getClass());
        preferenceManager.saveObject(SINGLETON_KEY, singleton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
