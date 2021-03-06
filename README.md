![](https://img.shields.io/badge/platform-Android-lightgrey.svg) ![](https://img.shields.io/badge/language-Kotlin-orange.svg) [![](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![](https://jitpack.io/v/timordu/Rely.svg)](https://jitpack.io/#timordu/Rely)
# Rely [说明文档](https://github.com/timordu/Rely/wiki)

一款基于Kotlin开发的Android开发框架



Download
--------

1. 在项目根目录下的`build.gradle`添加JitPack仓库

```kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在app目录下的`build.gradle`文件添加依赖

```kotlin
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
apply plugin: 'kotlin-android-extensions'


dependencies {
    //添加全部依赖
	implementation "com.github.timordu:Rely:$rely"
	//添加公共库和核心库
	//implementation "com.github.timordu.Rely:rely-common:$rely"
	//implementation "com.github.timordu.Rely:rely-core:$rely"
}
```

3. 配置项目

   - 新增Application继承自BaseApp, 然后在`AndroidManifest.xml`文件中注册
   - 修改AppTheme继承自BaseAppTheme(可选)

```kotlin
class MyApp : BaseApp() {
   override val isDebugModel: Boolean get() = BuildConfig.DEBUG
}

<style name="AppTheme" parent="@style/BaseAppTheme">

</style>
```

Todo List
---------

1. 多媒体功能的基类(拍照, 选择照片, 拍摄视频, 选择文件,图片压缩,视频压缩)
2. 图片预览优化
3. 文件选择器,支持单选和多选  √


Copyright
---------

   Copyright (c) 2017-2019 dugang

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.