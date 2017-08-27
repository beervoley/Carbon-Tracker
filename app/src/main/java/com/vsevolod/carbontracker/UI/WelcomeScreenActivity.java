package com.vsevolod.carbontracker.UI;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.vsevolod.carbontracker.Model.PreferenceManager;
import com.vsevolod.carbontracker.R;


/**
 * WelcomeScreen activity
 */

public class WelcomeScreenActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager = new PreferenceManager(this);


    private final String JOURNEYS_KEY = "journeys";
    private final String JOURNEY_KEY = "journey";
    private static final String SINGLETON_KEY = "singleton";
    private static final String MAIN_MENU_KEY = "main menu";
    private static final int MAIN_MENU_REQUEST_CODE_FOOTPRINT = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_activity);
        setAnimation();
        setupMainMenuButton();
        setTextStyle();
        setUpFullScreen();
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

    private void setTextStyle() {
        TextView textview = (TextView)findViewById(R.id.txt_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Regular.ttf");
        textview.setTypeface(typeface);
    }
    private void setupMainMenuButton() {
        Button btn = (Button) findViewById(R.id.btn_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //animation for welcome screen
    private void setAnimation() {
        //shimmer effect on title
        ShimmerFrameLayout container = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        container.setDuration(2800);
        container.setBaseAlpha(0.4f);
        container.setDropoff(0.4f);
        container.setRepeatMode(ObjectAnimator.REVERSE);
        container.startShimmerAnimation();

        Button btn = (Button) findViewById(R.id.btn_main);
        ImageView image3 = (ImageView) findViewById(R.id.imageCloud2);
        ImageView image4 = (ImageView) findViewById(R.id.imageCloud3);
        ImageView imageCar = (ImageView) findViewById(R.id.imageCar);
        btn.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        imageCar.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);
        Animation transRight = AnimationUtils.loadAnimation(this, R.anim.translate_car);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.translate_cloud);
        image3.startAnimation(translate);
        image4.startAnimation(translate);
        imageCar.startAnimation(transRight);
        btn.startAnimation(transRight);

    }
}
