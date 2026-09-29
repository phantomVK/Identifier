Identifier
=========

[![](https://jitpack.io/v/phantomVK/Identifier.svg)](https://jitpack.io/#phantomVK/Identifier) [![license](https://img.shields.io/badge/License-Apache2.0-brightgreen)](https://github.com/phantomVK/SlideBack/blob/master/LICENSE)

[中文版README](./README_CN.md)


简介
-----------

安卓开放匿名设备标识符，[设备兼容性列表](./COMPATIBILITY_LIST.md)

```text
# Information
- Ver: v0.8.26_9b7e227_debug
- Manufacturer: OnePlus, Brand: OnePlus
- Model: KB2000, Device: OnePlus8T
- Release: Android 14 (SDK_INT: 34)
- Display: KB2000_14.0.0.602(CN01)
- Incremental: R.1983865_1_2

# Result:
 * oaid: 85308E3D0E7C460000B39738D507BD4Ab2a66a8bfc942a3fdf61c856e1f38ac0
 * aaid: E72DD59E5382420000497925A6624CC910d2461ae6f547cd490cee4be85b52cb
 * vaid: null
 * gaid: abbb733d-0000-4680-9664-a458d4a2437d
```

下载
-----------
配置 __Gradle__ 的 __JitPack__ 下载依赖源。

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

使用
-------

在app的 Application.class 初始化 IdentifierManager。本初始化逻辑不耗时，可以放心在主线程执行。

```kotlin
class Application : android.app.Application() {
  override fun onCreate() {
    super.onCreate()

    IdentifierManager.Builder(applicationContext)
      .setDebug(false)
      .setExecutor { Thread(it).start() } // 可选: 设置自定义ThreadPoolExecutor
      .setLogger(LoggerImpl())
      .setMergeRequests(false) // 可选：合并多个请求，默认关闭
      .build()
  }
}
```

如何获取标识符

```kotlin
val consumer = object : Consumer {
  override fun onSuccess(result: IdentifierResult) {}
  override fun onError(msg: String, throwable: Throwable?) {}
}

IdentifierManager.build()
  .enableAsyncCallback(false) // 可选：在异步线程执行结果回调，默认为关闭
  .enableExperimental(false)
  .enableMemCache(false)
  .enableVerifyLimitAdTracking(false)
  .setIdConfig(
    IdConfig(
      isAaidEnabled = false,
      isVaidEnabled = false,
      isGoogleEnabled = false // 可选: 使用GoogleAdsId作为备选，默认关闭
    )
  )
  .setMemoryConfig(MemoryConfig(false))
  .setPrivacyAcceptedListener { true } // 可选: 是否已接受隐私协议，默认值为true
  .subscribe(consumer)
```

返回结果示例

```
 * oaid: c220daa990000000 // 如果只有gaid可用，则Oaid为空
 * aaid: e7deddda-0000-0000-9ffb-a00003c05e56
 * vaid: cb82b86710000000
 * gaid: aa1819fe-0000-0000-b484-c00008457221
```

R8 / Proguard
--------
特定规则已内置到aar，并在R8编译过程自动应用

兼容性要求
--------
发布的 AAR 与以下消费端最低工具链兼容:

| 消费端 | 最低版本 |
|-------|---------|
| Android Gradle Plugin | 4.0 |
| Gradle | 6.1.1 |
| JDK | 8 |
| Kotlin (若使用) | 1.6 |
| Android compileSdk | 建议 21+ |

AAR 标记 `@kotlin.Metadata(mv=[1,6,0])`，不发布 `.module` 元数据，老版本 Gradle / R8 消费方可以正常解析与打 dex。

许可证
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