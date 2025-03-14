Identifier
=========

[![](https://jitpack.io/v/phantomVK/Identifier.svg)](https://jitpack.io/#phantomVK/Identifier) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

[中文README](./README_CN.md)

Summary
-----------

The Open Anonymous Device Identifier for Android. More infomation see [Compatibility list](./COMPATIBILITY_LIST.md)

<img src="./static/sample.png" alt="png" width="480" height="349" style="display: inline;"/>

Download
-----------
Download the dependency from __JitPack__ using __Gradle__.

```groovy
// build.gradle(Project)
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

// build.gradle(:app)
dependencies {
    implementation "com.github.phantomVK:Identifier:latest.release@aar"
}
```

Usage
-------

Add following code to app's Application.class to init IdentifierManager. Those code takes no time to execute, keep it run on the main thread is fine.

```kotlin
class Application : android.app.Application() {
  override fun onCreate() {
    super.onCreate()

    IdentifierManager.Builder(applicationContext)
      .setDebug(false)
      .setExecutor { Thread(it).start() } // optional: setup custom ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .build()
  }
}
```

How to query the latest oaid:

```kotlin
val consumer = object : Consumer {
  override fun onSuccess(result: IdentifierResult) {}
  override fun onError(msg: String, throwable: Throwable?) {}
}

IdentifierManager.build()
  .enableAsyncCallback(false) // optional: invoke result callback in worker thread, default is false
  .enableExperimental(false)
  .enableMemCache(false)
  .enableVerifyLimitAdTracking(false)
  .setIdConfig(
    IdConfig(
      isAaidEnabled = false,
      isVaidEnabled = false,
      isGoogleEnabled = false // optional: use GoogleAdsId as backup, default is false
    )
  )
  .setMemoryConfig(MemoryConfig(false))
  .subscribe(consumer)
```

License
--------

```
Copyright 2024 WenKang Tan(phantomVK)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```