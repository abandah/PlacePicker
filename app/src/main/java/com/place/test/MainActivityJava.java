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
import com.place.picker.PlacePickerListener;

public class MainActivityJava extends AppCompatActivity implements PlacePickerListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.open_place_picker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlacePicker.IntentBuilder()
                        .setLatLong(32.027047,35.859087)
                        .showLatLong(true)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(false)
                        .Run(MainActivityJava.this);
            }
        });
    }


    @Override
    public void onPlaceSuccessful(AddressData addressData) {
        ((TextView) findViewById(R.id.address_data_text_view)).setText(addressData.toString());
    }

    @Override
    public void onPlaceError(String error) {

    }
}
