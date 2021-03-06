package com.place.picker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_place_picker.*
import java.util.*
import kotlin.collections.ArrayList

class PlacePickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        fun setPlacePickerListener(pPListener: PlacePickerListener) = apply {
            placePickerListener = pPListener
        }

        private lateinit var placePickerListener: PlacePickerListener
        private const val TAG = "PlacePickerActivity"
    }

    private lateinit var map: GoogleMap
    private lateinit var placeAutocomplete: AutocompleteSupportFragment
    private var googleApiKey: String? = null
    private var searchBarEnable: Boolean = false
    private lateinit var markerImage: ImageView
    private lateinit var markerShadowImage: ImageView
    private lateinit var placeSelectedFab: FloatingActionButton
    private lateinit var myLocationFab: FloatingActionButton
    private lateinit var placeNameTextView: TextView
    private lateinit var placeAddressTextView: TextView
    private lateinit var infoLayout: FrameLayout
    private lateinit var placeCoordinatesTextView: TextView
    private lateinit var placeProgressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private var latitude = Constants.DEFAULT_LATITUDE
    private var longitude = Constants.DEFAULT_LONGITUDE
    private var initLatitude = Constants.DEFAULT_LATITUDE
    private var initLongitude = Constants.DEFAULT_LONGITUDE
    private var showLatLong = true
    private var zoom = Constants.DEFAULT_ZOOM
    private var addressRequired: Boolean = true
    private var shortAddress = ""
    private var fullAddress = ""
    private var hideMarkerShadow = false
    private var markerDrawableRes: Int = -1
    private var markerColorRes: Int = -1
    private var fabColorRes: Int = -1
    private var primaryTextColorRes: Int = -1
    private var secondaryTextColorRes: Int = -1
    private var bottomViewColorRes: Int = -1
    private var mapRawResourceStyleRes: Int = -1
    private var addresses: List<Address>? = null
    private var mapType: MapType = MapType.NORMAL
    private var onlyCoordinates: Boolean = false
    private var hideLocationButton: Boolean = false
    private var disableMarkerAnimation: Boolean = false
    private var MarkerSize: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)
        getIntentData()

        setToolbar();


        if (searchBarEnable) {
            showSearchBar()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        bindViews()
        placeCoordinatesTextView.visibility = if (showLatLong) View.VISIBLE else View.GONE

        placeSelectedFab.setOnClickListener {
            if (onlyCoordinates) {
                sendOnlyCoordinates()
            } else {
                if (addresses != null) {
                    val addressData = AddressData(latitude, longitude, addresses,
                            "https://maps.google.com/maps/api/staticmap?" +
                                    "center=" + latitude + "," + longitude +
                                    "&" +
                                    "zoom=15" +
                                    "&" +
                                    "size=200x200" +
                                    "&" +
                                    "&markers=" + latitude + "," + longitude +
                                    "&" +
                                    "sensor=false" +
                                    "&" +
                                    "key=" + googleApiKey);
                    //  val returnIntent = Intent()
                    //    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
                    // setResult(RESULT_OK, returnIntent)
                    var list : ArrayList<AddressData> = ArrayList();
                    list.add(addressData)
                    val addressData2 = AddressData(initLatitude, initLongitude, addresses,
                            "https://maps.google.com/maps/api/staticmap?" +
                                    "center=" + initLatitude + "," + initLongitude +
                                    "&" +
                                    "zoom=15" +
                                    "&" +
                                    "size=200x200" +
                                    "&" +
                                    "&markers=" + initLatitude + "," + longitude +
                                    "&"+ initLongitude +
                                    "sensor=false" +
                                    "&" +
                                    "key=" + googleApiKey);
                    list.add(addressData2)
                    placePickerListener.onPlaceSuccessful(list)
                    finish()
                } else {
                    if (!addressRequired) {
                        sendOnlyCoordinates()
                    } else {
                        Toast.makeText(this@PlacePickerActivity, R.string.no_address, Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        }

        myLocationFab.setOnClickListener {
            if (this::map.isInitialized) {
                map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                LatLng(initLatitude, initLongitude),
                                zoom
                        )
                )
            }
        }
        setIntentCustomization()
    }

    private fun setToolbar() {
        if (supportActionBar == null) {
            toolbar = findViewById<Toolbar>(R.id.toolbar)
            //     toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            setSupportActionBar(toolbar)
            /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          finish();
                      }
                  });*/
            toolbar.setVisibility(View.VISIBLE)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

    }

    private fun showSearchBar() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, googleApiKey!!)
        }

        findViewById<CardView>(R.id.search_bar_card_view).visibility = View.VISIBLE
        placeAutocomplete = supportFragmentManager.findFragmentById(R.id.place_autocomplete)
                as AutocompleteSupportFragment

        placeAutocomplete.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS,
                        Place.Field.ADDRESS_COMPONENTS
                )
        )
        placeAutocomplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                latitude = place.latLng!!.latitude
                longitude = place.latLng!!.longitude
                setAddress(latitude, longitude)

                map.clear()
                map.setOnMapLoadedCallback {
                    setPlaceDetails(latitude, longitude, shortAddress, fullAddress)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
                }
            }

            override fun onError(error: Status) {
                Log.d(TAG, error.toString())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        placePickerListener.onPlaceError("Closed");
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun bindViews() {
        markerImage = findViewById(R.id.marker_image_view)
        markerShadowImage = findViewById(R.id.marker_shadow_image_view)
        placeSelectedFab = findViewById(R.id.place_chosen_button)
        myLocationFab = findViewById(R.id.my_location_button)
        placeNameTextView = findViewById(R.id.text_view_place_name)
        placeAddressTextView = findViewById(R.id.text_view_place_address)
        placeCoordinatesTextView = findViewById(R.id.text_view_place_coordinates)
        infoLayout = findViewById(R.id.info_layout)
        placeProgressBar = findViewById(R.id.progress_bar_place)
    }

    private fun sendOnlyCoordinates() {
        val addressData = AddressData(latitude, longitude, null,
                "https://maps.google.com/maps/api/staticmap?" +
                        "center=" + latitude + "," + longitude +
                        "&" +
                        "zoom=15" +
                        "&" +
                        "size=200x200" +
                        "&" +
                        "&markers=" + latitude + "," + longitude +
                        "&" +
                        "sensor=false" +
                        "&" +
                        "key=" + googleApiKey)
        //val returnIntent = Intent()
        //returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
        //setResult(RESULT_OK, returnIntent)
        var list : ArrayList<AddressData> = ArrayList();
        list.add(addressData)
        val addressData2 = AddressData(initLatitude, initLongitude, addresses,
                "https://maps.google.com/maps/api/staticmap?" +
                        "center=" + initLatitude + "," + initLongitude +
                        "&" +
                        "zoom=15" +
                        "&" +
                        "size=200x200" +
                        "&" +
                        "&markers=" + initLatitude + "," + longitude +
                        "&"+ initLongitude +
                        "sensor=false" +
                        "&" +
                        "key=" + googleApiKey);
        list.add(addressData2)
        placePickerListener.onPlaceSuccessful(list)
        finish()
    }

    private fun getIntentData() {
        latitude = intent.getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, Constants.DEFAULT_LATITUDE)
        longitude = intent.getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, Constants.DEFAULT_LONGITUDE)
        initLatitude = latitude
        initLongitude = longitude
        showLatLong = intent.getBooleanExtra(Constants.SHOW_LAT_LONG_INTENT, false)
        addressRequired = intent.getBooleanExtra(Constants.ADDRESS_REQUIRED_INTENT, true)
        hideMarkerShadow = intent.getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false)
        zoom = intent.getFloatExtra(Constants.INITIAL_ZOOM_INTENT, Constants.DEFAULT_ZOOM)
        markerDrawableRes = intent.getIntExtra(Constants.MARKER_DRAWABLE_RES_INTENT, -1)
        markerColorRes = intent.getIntExtra(Constants.MARKER_COLOR_RES_INTENT, -1)
        fabColorRes = intent.getIntExtra(Constants.FAB_COLOR_RES_INTENT, -1)
        primaryTextColorRes = intent.getIntExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, -1)
        secondaryTextColorRes = intent.getIntExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, -1)
        bottomViewColorRes = intent.getIntExtra(Constants.BOTTOM_VIEW_COLOR_RES_INTENT, -1)
        mapRawResourceStyleRes = intent.getIntExtra(Constants.MAP_RAW_STYLE_RES_INTENT, -1)
        mapType = intent.getSerializableExtra(Constants.MAP_TYPE_INTENT) as MapType
        onlyCoordinates = intent.getBooleanExtra(Constants.ONLY_COORDINATES_INTENT, false)
        googleApiKey = intent.getStringExtra(Constants.GOOGLE_API_KEY)
        googleApiKey = intent.getStringExtra(Constants.GOOGLE_API_KEY)
        MarkerSize = intent.getIntExtra(Constants.MarkerSize, 0)
        if (googleApiKey == null || googleApiKey.equals("")) {
            //val returnIntent = Intent()
            // returnIntent.putExtra("Error", )
            //setResult(RESULT_CANCELED, returnIntent)
            placePickerListener.onPlaceError(getString(R.string.PleaseEnterAPIKEY))
            finish()
        }
        searchBarEnable = intent.getBooleanExtra(Constants.SEARCH_BAR_ENABLE, false)
        hideLocationButton = intent.getBooleanExtra(Constants.HIDE_LOCATION_BUTTON, false)
        disableMarkerAnimation = intent.getBooleanExtra(Constants.DISABLE_MARKER_ANIMATION, false)
    }

    private fun setIntentCustomization() {
        markerShadowImage.visibility = if (hideMarkerShadow) View.GONE else View.VISIBLE
        if (markerColorRes != -1) {
            markerImage.setColorFilter(ContextCompat.getColor(this, markerColorRes))
        }
        if (markerDrawableRes != -1) {
            markerImage.setImageDrawable(ContextCompat.getDrawable(this, markerDrawableRes))
        }
        if (fabColorRes != -1) {
            placeSelectedFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes))
            myLocationFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes))
        }
        if (primaryTextColorRes != -1) {
            placeNameTextView.setTextColor(ContextCompat.getColor(this, primaryTextColorRes))
        }
        if (secondaryTextColorRes != -1) {
            placeAddressTextView.setTextColor(ContextCompat.getColor(this, secondaryTextColorRes))
        }
        if (bottomViewColorRes != -1) {
            infoLayout.setBackgroundColor(ContextCompat.getColor(this, bottomViewColorRes))
        }
        myLocationFab.visibility = if (hideLocationButton) View.GONE else View.GONE
        if (MarkerSize > 0) {
            val params: ViewGroup.LayoutParams = markerImage.getLayoutParams() as ViewGroup.LayoutParams
            params.width = MarkerSize
            markerImage.setLayoutParams(params);
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnCameraMoveStartedListener {
            if (markerImage.translationY == 0f && !disableMarkerAnimation) {
                markerImage.animate()
                        .translationY(-75f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(250)
                        .start()
            }
        }

        map.setOnCameraIdleListener {
            if (!disableMarkerAnimation) {
                markerImage.animate()
                        .translationY(0f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(250)
                        .start()
            }

            showLoadingBottomDetails()
            val latLng = map.cameraPosition.target
            latitude = latLng.latitude
            longitude = latLng.longitude
            AsyncTask.execute() {
                getAddressForLocation()
                runOnUiThread { setPlaceDetails(latitude, longitude, shortAddress, fullAddress) }
            }
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        if (mapRawResourceStyleRes != -1) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapRawResourceStyleRes))
        }
        map.mapType = when (mapType) {
            MapType.NORMAL -> GoogleMap.MAP_TYPE_NORMAL
            MapType.SATELLITE -> GoogleMap.MAP_TYPE_SATELLITE
            MapType.HYBRID -> GoogleMap.MAP_TYPE_HYBRID
            MapType.TERRAIN -> GoogleMap.MAP_TYPE_TERRAIN
            MapType.NONE -> GoogleMap.MAP_TYPE_NONE
            else -> GoogleMap.MAP_TYPE_NORMAL
        }
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun showLoadingBottomDetails() {
        placeNameTextView.text = ""
        placeAddressTextView.text = ""
        placeCoordinatesTextView.text = ""
        placeProgressBar.visibility = View.VISIBLE
        place_chosen_button.visibility = View.INVISIBLE
    }

    private fun setPlaceDetails(
            latitude: Double,
            longitude: Double,
            shortAddress: String,
            fullAddress: String
    ) {

        if (latitude == -1.0 || longitude == -1.0) {
            placeNameTextView.text = ""
            placeAddressTextView.text = ""
            placeProgressBar.visibility = View.VISIBLE
            place_chosen_button.visibility = View.INVISIBLE
            return
        }
        placeProgressBar.visibility = View.INVISIBLE
        place_chosen_button.visibility = View.VISIBLE

        placeNameTextView.text = if (shortAddress.isEmpty()) "Dropped Pin" else shortAddress
        placeAddressTextView.text = fullAddress
        placeCoordinatesTextView.text = Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(longitude, Location.FORMAT_DEGREES)
    }

    private fun getAddressForLocation() {
        setAddress(latitude, longitude)
    }

    private fun setAddress(
            latitude: Double,
            longitude: Double
    ) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            this.addresses = addresses
            return if (addresses != null && addresses.size != 0) {
                fullAddress = addresses[0].getAddressLine(
                        0
                ) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                shortAddress = generateFinalAddress(fullAddress).trim()
            } else {
                shortAddress = ""
                fullAddress = ""
            }
        } catch (e: Exception) {
            //Time Out in getting address
            Log.e(TAG, e.message.toString())
            shortAddress = ""
            fullAddress = ""
            addresses = null
        }
    }

    private fun generateFinalAddress(
            address: String
    ): String {
        val s = address.split(",")
        return if (s.size >= 3) s[1] + "," + s[2] else if (s.size == 2) s[1] else s[0]
    }
}
