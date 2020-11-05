package com.place.test;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.place.picker.AddressData;
import com.place.picker.Constants;
import com.place.picker.MainActivity;
import com.place.picker.MapType;
import com.place.picker.PlacePicker;
import com.place.picker.PlacePickerListener;

import java.util.ArrayList;

public class MainActivityJava extends AppCompatActivity implements PlacePickerListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public void onPlaceSuccessful(ArrayList<AddressData> addressData) {
        ((TextView) findViewById(R.id.address_data_text_view)).setText(addressData.get(0).toString());
    }

    @Override
    public void onPlaceError(String error) {

    }

    public void openPlacePicker(View view) {
        new PlacePicker.IntentBuilder()
                .setLatLong(32.027047,35.859087)
                .showLatLong(true)
                .setMapType(MapType.NORMAL)
                .setPlaceSearchBar(false)
                //.setMarkerSize(getResources().getDimensionPixelSize(R.dimen.pinSize))
                //.setMarkerImageImageColor(R.color.red)
                .Run(MainActivityJava.this);
    }
}
