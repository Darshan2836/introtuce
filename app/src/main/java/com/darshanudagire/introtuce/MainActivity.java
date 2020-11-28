package com.darshanudagire.introtuce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Users.OnFragmentInteractionListener,Enroll.OnFragmentInteractionListener{


    @BindView(R.id.mainToolbar_id)
    Toolbar toolbar;
    @BindView(R.id.tabLayout_id)
    TabLayout tabLayout;
    @BindView(R.id.viewPager_id)
    ViewPager viewPager;


    //adapter
    private PagerAdapter pagerAdapter;
    private static final int WRITE_PERMISSION = 99;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        requestWritePermission();

        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Enroll"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new com.darshanudagire.introtuce.adapter.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "Write Permission Granted");
            }else {
                Log.d("permission", "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
//                requestWritePermission();
                finish();
            }
        }
    }

    private void requestWritePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
            }
        }
    }
}