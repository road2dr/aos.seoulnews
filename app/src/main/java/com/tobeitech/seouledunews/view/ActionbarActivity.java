package com.tobeitech.seouledunews.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tobeitech.seouledunews.R;

/**
 * Created by LocalUser0 on 2017-06-08.
 */

public abstract class ActionbarActivity extends AppCompatActivity {

    private ViewGroup container;
    ImageButton imageButton_ActionbarMenu;
    ImageButton imageButton_ActionbarSearch;
    View.OnClickListener mClickListener2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_actionbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        TextView titleView = (TextView) findViewById(R.id.textView_ActionbarTiltle);
        titleView.setText("서울교육뉴스");
        imageButton_ActionbarMenu = (ImageButton) findViewById(R.id.imageButton_ActionbarMenu);
        imageButton_ActionbarMenu.setVisibility(View.VISIBLE);
        imageButton_ActionbarSearch = (ImageButton) findViewById(R.id.ImageButton_ActionbarSearch);
        imageButton_ActionbarSearch.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        container = (ViewGroup) findViewById(R.id.container);
    }

    protected void putContentView(int resId) {
        container.addView(getLayoutInflater().inflate(resId, null));
    }

    protected void putContentView(int resId, View.OnClickListener listener) {
        this.mClickListener2 = listener;
        imageButton_ActionbarSearch.setOnClickListener(mClickListener2);
        imageButton_ActionbarMenu.setOnClickListener(mClickListener2); //imageButton_ActionbarMenu.setOnClickListener(clickListener);

        container.addView(getLayoutInflater().inflate(resId, null));
    }
}
