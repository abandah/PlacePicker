# PlacePicker
[![Release](https://jitpack.io/v/abandah/PlacePicker.svg?style=flat-square)](https://jitpack.io/#abandah/PlacePicker)

> Place Picker library for Android and Java

Encapsulate Place Picking logic into objects that adhere to configurable defaults.


Building with JitPack
=====

If you are using Gradle to get a GitHub project into your build, you will need to:

**Step 1.** Add the JitPack maven repository to the list of repositories:

```
gradle
   allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }

    }
}
```

**Step 2.**  Add the dependency information:

```
dependencies {
    implementation 'com.github.abandah:PlacePicker:0.0.2'
}
```

**Step 3.**  Codeing

```
         Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(32.027047,35.859087)
                        .showLatLong(true)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(true, apikey)
                        .build(MainActivityJava.this);

                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
}
```
```

public static String getAppKey(){
        Context activity = App.getContext();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
    }
```
```
  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                } catch (Exception e) {
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```
**Step 4.**  in manifist add API KEY meta Data:

```
 <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="API KEY"/>
        
```
