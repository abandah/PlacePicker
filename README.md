# android-example

Example Android project that works with jitpack.io

https://jitpack.io/#jitpack/android-example/1.0.1

Add it to your build.gradle with:
```gradle
dependencies {
    compile 'com.github.jitpack:android-example:1.0.1@aar'
}
```

# Adding maven plugin

To enable installing into local maven repository and JitPack you need to add the 'android-maven' plugin:

1. Add `classpath 'com.github.dcendents:android-maven-plugin:1.2'` to root build.gradle under buildscripts
2. Add `apply plugin: 'android-maven'` to the library/build.gradle

After these changes you should be able to run:

    gradle install
    
    
