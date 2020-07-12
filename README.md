# ErrorHandler
[![Release](https://jitpack.io/v/abandah/testingTool.svg?style=flat-square)](https://jitpack.io/#abandah/testingTool)
[![Release](https://jitpack.io/v/abandah/testingTool.svg?style=flat-square)]
> Error handling library for Android and Java

Encapsulate error handling logic into objects that adhere to configurable defaults. Then pass them around as parameters or inject them via Web Service. 


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
    implementation 'com.github.abandah:testingTool:0.1.4'
}
```

**Step 3.**  Create new Class extends Application and add Code inside onCreate()

```
public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
            new UCEHandler.Builder(this)
                    .setTrackActivitiesEnabled(false)
                    .setLink("WebService Link here")
                    .build();
}
```
**Step 4.**  Add  android:name=".app" under application in manifist:

```
 <application
        android:name=".app"
        .
        .
        
```
