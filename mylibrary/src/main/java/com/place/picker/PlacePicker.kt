package com.place.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment


class PlacePicker {

    class Picture {
        //Foo.Companion.a();
        companion object {
            @JvmStatic
            fun getPictureLink(latitude: Double, longitude: Double, API: String): String {
                return "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=200x200&sensor=false&key=" + API;

            }

            fun getAppKey(activity: Context): String? {
                val applicationInfo: ApplicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);

                return applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
            }
        }

    }

    class IntentBuilder {

        private var markerSize: Int = 0
        private lateinit var activity: Activity
        private var showLatLong: Boolean = false
        private var latitude: Double = Constants.DEFAULT_LATITUDE
        private var longitude: Double = Constants.DEFAULT_LONGITUDE
        private var zoom: Float = Constants.DEFAULT_ZOOM
        private var addressRequired: Boolean = true
        private var hideMarkerShadow: Boolean = false
        private var markerDrawableRes: Int = -1
        private var markerImageColorRes: Int = -1
        private var fabBackgroundColorRes: Int = -1
        private var primaryTextColorRes: Int = -1
        private var secondaryTextColorRes: Int = -1
        private var bottomViewColorRes: Int = -1
        private var mapRawResourceStyleRes: Int = -1
        private var mapType: MapType = MapType.NORMAL
        private var onlyCoordinates: Boolean = false
        private var hideLocation: Boolean = false
        private var googleApiKey: String? = null
        private var searchBarEnable: Boolean = false
        private var disableMarkerAnimation: Boolean = false

        fun showLatLong(showLatLong: Boolean) = apply { this.showLatLong = showLatLong }

        fun setLatLong(
                latitude: Double,
                longitude: Double
        ) = apply {
            if (latitude == -1.0 || longitude == -1.0) {
                this.latitude = 32.027047
                this.longitude = 35.859087
            } else {
                this.latitude = latitude
                this.longitude = longitude
            }
        }

        fun setPlaceSearchBar(value: Boolean, googleApiKey: String? = null) = apply {
            this.searchBarEnable = value
        }

        fun setPlaceSearchBar(value: Boolean) = apply {
            this.searchBarEnable = value
        }

        fun setMapZoom(zoom: Float) = apply { this.zoom = zoom }

        fun setAddressRequired(addressRequired: Boolean) =
                apply { this.addressRequired = addressRequired }

        fun hideMarkerShadow(hideMarkerShadow: Boolean) =
                apply { this.hideMarkerShadow = hideMarkerShadow }

        fun setMarkerDrawable(@DrawableRes markerDrawableRes: Int) =
                apply { this.markerDrawableRes = markerDrawableRes }

        fun setMarkerImageImageColor(@ColorRes markerImageColorRes: Int) =
                apply { this.markerImageColorRes = markerImageColorRes }

        fun setFabColor(@ColorRes fabBackgroundColor: Int) =
                apply { this.fabBackgroundColorRes = fabBackgroundColor }

        fun setPrimaryTextColor(@ColorRes primaryTextColor: Int) =
                apply { this.primaryTextColorRes = primaryTextColor }

        fun setSecondaryTextColor(@ColorRes secondaryTextColorRes: Int) =
                apply { this.secondaryTextColorRes = secondaryTextColorRes }

        fun setBottomViewColor(@ColorRes bottomViewColor: Int) =
                apply { this.bottomViewColorRes = bottomViewColor }

        fun setMapRawResourceStyle(@RawRes mapRawResourceStyleRes: Int) =
                apply { this.mapRawResourceStyleRes = mapRawResourceStyleRes }

        fun setMapType(mapType: MapType) = apply { this.mapType = mapType }

        fun onlyCoordinates(onlyCoordinates: Boolean) =
                apply { this.onlyCoordinates = onlyCoordinates }

        fun hideLocationButton(hideLocation: Boolean) = apply { this.hideLocation = hideLocation }

        fun disableMarkerAnimation(disableMarkerAnimation: Boolean) =
                apply { this.disableMarkerAnimation = disableMarkerAnimation }

        fun setMarkerSize(wigth : Int) = apply {
            this.markerSize = wigth
            Log.e("TAGTAG","TAGTAG")
        }
        fun build(activity: Activity): Intent {
            this.activity = activity
            val intent = Intent(activity, PlacePickerActivity::class.java)
            intent.putExtra(Constants.ADDRESS_REQUIRED_INTENT, addressRequired)
            intent.putExtra(Constants.SHOW_LAT_LONG_INTENT, showLatLong)
            intent.putExtra(Constants.INITIAL_LATITUDE_INTENT, latitude)
            intent.putExtra(Constants.INITIAL_LONGITUDE_INTENT, longitude)
            intent.putExtra(Constants.INITIAL_ZOOM_INTENT, zoom)
            intent.putExtra(Constants.HIDE_MARKER_SHADOW_INTENT, hideMarkerShadow)
            intent.putExtra(Constants.MARKER_DRAWABLE_RES_INTENT, markerDrawableRes)
            intent.putExtra(Constants.MARKER_COLOR_RES_INTENT, markerImageColorRes)
            intent.putExtra(Constants.FAB_COLOR_RES_INTENT, fabBackgroundColorRes)
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, primaryTextColorRes)
            intent.putExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, secondaryTextColorRes)
            intent.putExtra(Constants.BOTTOM_VIEW_COLOR_RES_INTENT, bottomViewColorRes)
            intent.putExtra(Constants.MAP_RAW_STYLE_RES_INTENT, mapRawResourceStyleRes)
            intent.putExtra(Constants.MAP_TYPE_INTENT, mapType)
            intent.putExtra(Constants.ONLY_COORDINATES_INTENT, onlyCoordinates)
            intent.putExtra(Constants.GOOGLE_API_KEY, Util.getAppKey(activity))
            intent.putExtra(Constants.SEARCH_BAR_ENABLE, searchBarEnable)
            intent.putExtra(Constants.HIDE_LOCATION_BUTTON, hideLocation)
            intent.putExtra(Constants.DISABLE_MARKER_ANIMATION, disableMarkerAnimation)
            intent.putExtra(Constants.MarkerSize, markerSize)

            return intent
        }

        fun Run (context : PlacePickerListener){
            if (context is Activity) {
                Run(context as Activity)
            }else if (context is Fragment){
                Run(context as Fragment)
            }else{
                context.onPlaceError("Error on attach")
            }
        }
        private fun Run(activity: Activity) {
            val i: Intent = build(activity);
            googleApiKey = i.getStringExtra(Constants.GOOGLE_API_KEY);
            if (googleApiKey == null || googleApiKey.equals("")) {
                (activity as PlacePickerListener ).onPlaceError("API key not found.  Check that <meta-data android:name=\"com.google.android.geo.API_KEY\" android:value=\"your API key\"/>")
                return
            }
            activity.startActivity(i);
            PlacePickerActivity.setPlacePickerListener(activity as PlacePickerListener)
        }

        private fun Run(fragment: Fragment) {
            this.activity = fragment.activity!!
            val i: Intent = build(activity);
            googleApiKey = i.getStringExtra(Constants.GOOGLE_API_KEY);
            if (googleApiKey == null || googleApiKey.equals("")) {
                (fragment as PlacePickerListener ).onPlaceError("API key not found.  Check that <meta-data android:name=\"com.google.android.geo.API_KEY\" android:value=\"your API key\"/>")
                return
            }
            fragment.startActivity(i);
            PlacePickerActivity.setPlacePickerListener(fragment as PlacePickerListener)
        }

    }

}
