/*
 *    Copyright (c) 2017-2019 dugang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.dugang.rely.retrofit

import android.util.ArrayMap
import com.dugang.rely.Rely
import com.dugang.rely.common.extension.isNotNull
import com.dugang.rely.retrofit.cookie.CookieJarImpl
import com.dugang.rely.retrofit.cookie.CookieStore
import com.dugang.rely.retrofit.cookie.CookieStoreImpl
import com.dugang.rely.retrofit.intercepter.HeaderInterceptor
import com.dugang.rely.retrofit.intercepter.ParamInterceptor
import com.dugang.rely.retrofit.intercepter.RetryInterceptor
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by dugang on 2017/7/27.网络请求Retrofit构建类
 */
class RetrofitBuilder {
    //请求根路径
    private lateinit var baseUrl: String
    //公共请求头
    private val headers = ArrayMap<String, String>()
    //公共请求参数
    private val params = ArrayMap<String, String>()

    //连接超时时间
    private var connectTimeout: Long = 10
    //读操作超时时间
    private var readTimeOut: Long = 10
    //写操作超时时间
    private var writeTimeout: Long = 10

    //重试次数
    private var retryCount: Int = 1
    //重连间隔
    private var retryDelay: Int = 5
    //缓存路径
    private var cacheFile: File = Rely.appContext.cacheDir
    //缓存大小
    private var cacheSize: Long = 50

    private var cookieStore: CookieStore =
        CookieStoreImpl()

    //Https证书
    private var sslSocketFactory: SSLSocketFactory? = null
    private var x509TrustManager: X509TrustManager? = null

    private var authenticator: Authenticator? = null

    private var interceptors = ArrayList<Interceptor>()
    private var networkInterceptors = ArrayList<Interceptor>()

    /**
     * 请求根路径
     */
    fun baseUrl(baseUrl: String): RetrofitBuilder {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * 添加公共请求头
     */
    fun addHeaders(headers: ArrayMap<String, String>): RetrofitBuilder {
        this.headers.putAll(headers)
        return this
    }

    /**
     * 添加公共请求头
     */
    fun addHeader(key: String, value: String): RetrofitBuilder {
        this.headers[key] = value
        return this
    }

    /**
     * 添加公共请求参数
     */
    fun addParams(params: ArrayMap<String, String>): RetrofitBuilder {
        this.params.putAll(params)
        return this
    }

    /**
     * 添加公共请求参数
     */
    fun addParam(key: String, value: String): RetrofitBuilder {
        this.params[key] = value
        return this
    }

    /**
     * 设置连接超时时间,单位秒,默认10秒
     */
    fun setConnectTimeOut(time: Long): RetrofitBuilder {
        this.connectTimeout = time
        return this
    }

    /**
     * 设置读操作超时时间,单位秒,默认10秒
     */
    fun setReadTimeOut(time: Long): RetrofitBuilder {
        this.readTimeOut = time
        return this
    }

    /**
     * 设置写操作超时时间,单位秒,默认10秒
     */
    fun setWriteTimeOut(time: Long): RetrofitBuilder {
        this.writeTimeout = time
        return this
    }

    /**
     * 设置请求失败重试次数,默认重试一次
     */
    fun setRetryCount(count: Int): RetrofitBuilder {
        this.retryCount = count
        return this
    }

    /**
     * 设置请求失败重连间隔,默认5秒
     */
    fun setRetryDelay(delay: Int): RetrofitBuilder {
        this.retryDelay = delay
        return this
    }

    /**
     * 设置缓存目录
     */
    fun setCacheFile(cacheFile: File): RetrofitBuilder {
        this.cacheFile = cacheFile
        return this
    }

    /**
     * 设置缓存大小,默认50
     */
    fun setCacheSize(cacheSize: Long): RetrofitBuilder {
        this.cacheSize = cacheSize
        return this
    }

    /**
     * 设置cookie管理
     */
    fun setCookieStore(cookieStore: CookieStore): RetrofitBuilder {
        this.cookieStore = cookieStore
        return this
    }

    /**
     * 设置Https连接
     */
    fun setSSLSocketFactory(sslSocketFactory: SSLSocketFactory, x509TrustManager: X509TrustManager): RetrofitBuilder {
        this.sslSocketFactory = sslSocketFactory
        this.x509TrustManager = x509TrustManager
        return this
    }

    /**
     * Token在状态码返回401的情况下的刷新
     */
    fun setAuthenticator(authenticator: Authenticator): RetrofitBuilder {
        this.authenticator = authenticator
        return this
    }

    /**
     * 添加Interceptor
     */
    fun addInterceptor(interceptor: Interceptor): RetrofitBuilder {
        this.interceptors.add(interceptor)
        return this
    }

    /**
     * 添加NetworkInterceptor
     */
    fun addNetworkInterceptor(interceptor: Interceptor): RetrofitBuilder {
        this.networkInterceptors.add(interceptor)
        return this
    }

    fun build(): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        if (Rely.isDebug)
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        okHttpBuilder.addInterceptor(interceptor)

        okHttpBuilder.retryOnConnectionFailure(true)
        //设置连接超时
        okHttpBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
        //设置写超时
        okHttpBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS)
        //设置读超时
        okHttpBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS)
        //设置重连
        okHttpBuilder.addInterceptor(RetryInterceptor(retryCount, retryDelay))
        //设置公共请求头
        okHttpBuilder.addInterceptor(HeaderInterceptor(headers))
        //设置公共请求参数
        okHttpBuilder.addInterceptor(ParamInterceptor(params))
        //设置cookie管理
        okHttpBuilder.cookieJar(CookieJarImpl(cookieStore))
        //设置缓存
        if (!cacheFile.exists()) cacheFile.mkdirs()
        okHttpBuilder.cache(Cache(cacheFile, 1024 * 1024 * cacheSize))

        //设置Https连接
        if (sslSocketFactory.isNotNull() && x509TrustManager.isNotNull())
            okHttpBuilder.sslSocketFactory(sslSocketFactory!!, x509TrustManager!!)

        //设置Authentication
        authenticator?.let { okHttpBuilder.authenticator(it) }

        interceptors.forEach { okHttpBuilder.addInterceptor(it) }
        networkInterceptors.forEach { okHttpBuilder.addNetworkInterceptor(it) }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
