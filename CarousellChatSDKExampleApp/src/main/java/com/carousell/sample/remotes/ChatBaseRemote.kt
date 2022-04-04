package com.carousell.sample.remotes

import com.carousell.chat.remotes.common.Constants
import com.carousell.chat.remotes.interceptors.ChatTokenInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

open class ChatBaseRemote {
    protected open fun <T> create(baseUrl: String, clazz: Class<T>): T {
        val gson = provideGson(false)
        val httpBuilder = provideOkHttpClientConfig()
        val okHttpClient = setInterceptors(arrayListOf(), httpBuilder).build()
        return provideRetrofit(baseUrl, gson, okHttpClient).create(clazz)
    }

    private fun provideGson(requireDate: Boolean): Gson {
        val gson = GsonBuilder()
        if (requireDate) {
            gson.setDateFormat(Constants.API_DATE_FORMAT)
        }
        return gson.create()
    }

    private fun provideRetrofit(baseUrl: String, gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun provideOkHttpClientConfig(): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(Constants.CONNECT_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(Constants.READ_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(Constants.WRITE_TIMEOUT_IN_SEC, TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(provideLoggingInterceptor())
        return okHttpClientBuilder
    }

    private fun setInterceptors(
        interceptorList: List<Interceptor>,
        okHttpClientBuilder: OkHttpClient.Builder
    ): OkHttpClient.Builder {
        for (interceptor in interceptorList) {
            okHttpClientBuilder.addInterceptor(interceptor)
        }
        return okHttpClientBuilder
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}