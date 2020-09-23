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
    implementation 'com.github.abandah:PlacePicker:0.0.3'
}
```

**Step 3.**  Codeing

```
       new PlacePicker.IntentBuilder()
                        .setLatLong(32.027047,35.859087)
                        .showLatLong(true)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(false)
                        .Run(MainActivityJava.this);
```
Implement  PlacePickerListener to activity

```
    @Override
    public void onPlaceSuccessful(AddressData addressData) {
    }

    @Override
    public void onPlaceError(String error) {

    }
```
**Step 4.**  in manifist add API KEY meta Data:

```
 <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="API KEY"/>
        
```
