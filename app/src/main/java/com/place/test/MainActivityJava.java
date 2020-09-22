package com.place.test;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.place.picker.AddressData;
import com.place.picker.Constants;
import com.place.picker.MainActivity;
import com.place.picker.MapType;
import com.place.picker.PlacePicker;

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final ApplicationInfo finalApplicationInfo = applicationInfo;
        findViewById(R.id.open_place_picker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(32.027047,35.859087)
                        .showLatLong(true)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(true, finalApplicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
                        .build(MainActivityJava.this);

                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                    ((TextView) findViewById(R.id.address_data_text_view)).setText(addressData.toString());
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
