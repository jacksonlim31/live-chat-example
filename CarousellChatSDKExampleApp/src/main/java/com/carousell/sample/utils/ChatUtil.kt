package com.carousell.sample.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Process
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.carousell.sample.R
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.google.gson.stream.MalformedJsonException
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

object ChatUtil {
    private const val prefName: String = "CHAT_AUTH"

    fun isDestroyed(context: Context?): Boolean {
        if (null == context) {
            return true
        }
        if (context is Activity) {
            val activity = context
            return if (activity.isFinishing) {
                true
            } else Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed
        }
        return false
    }

    fun loadUrlImage(context: Context?, imageView: ImageView, imgURL: String) {
        context?.let { it ->
            if (!isDestroyed(it)) {
                val requestOptions: RequestOptions = RequestOptions().centerCrop()

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    requestOptions.placeholder(R.drawable.loading_placeholder)
                }

                loadImageWithOptions(it, imgURL, requestOptions, imageView)
            }
        }
    }

    fun loadImageWithOptions(context: Context?, imageString: String, requestOptions: RequestOptions, imageView: ImageView) {
        context?.let { it ->
            if (!isDestroyed(it)) {
                Glide.with(it)
                    .load(imageString)
                    .apply(requestOptions)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(Build.VERSION.SDK_INT < 21)
                    .into(imageView)
            }
        }
    }

    fun saveToPreferences(context: Context, key: String, value: String) {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun getPreferences(context: Context, key: String): String? {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getString(key, "")
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T> {
        val newList = ArrayList<T>()
        for (element in list) {
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }
        return newList
    }

    fun hideSoftKeyboard(activity: Activity?) {
        try {
            if (activity == null) return
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) imm.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            ) else activity.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
        } catch (nullError: NullPointerException) {
        } catch (ignore: Exception) {
        }
    }

    fun showSoftKeyboard(activity: Activity?) {
        try {
            if (activity == null) return
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) imm.showSoftInput(activity.currentFocus, 0)
        } catch (nullError: NullPointerException) {
        } catch (ignore: Exception) {
        }
    }

    @Throws(Exception::class)
    fun saveClassInSharedPreferences(context: Context, sharedPrefsKey: String, data: Any) {
        val gson = Gson()
        val json: String = gson.toJson(data)
        val saveClassThread = Thread {
            try {
                saveToPreferences(context, sharedPrefsKey, json)
            } catch (e: Exception) {
               throw e
            }
        }
        saveClassThread.priority = Process.THREAD_PRIORITY_BACKGROUND
        saveClassThread.start()
    }

    @Throws(JsonSyntaxException::class, MalformedJsonException::class)
    fun <T> retrieveClassInSharedPreferences(context: Context, sharedPrefsKey: String, type: Class<T>?, defaultValue: String?): T {
        val gson = Gson()
        val json = getPreferences(context, sharedPrefsKey)
        return try {
            if (json.isNullOrEmpty()) {
                val reader = JsonReader(StringReader("{}"))
                gson.fromJson(reader, type)
            } else {
                val reader = JsonReader(StringReader(json))
                reader.isLenient = true
                gson.fromJson(reader, type)
            }
        } catch (me: MalformedJsonException) {
            throw MalformedJsonException(me)
        } catch (e: java.lang.Exception) {
            val reader = JsonReader(StringReader(json))
            reader.isLenient = true
            gson.fromJson(reader, type)
        }
    }

    fun clear(context: Context, prefKey: String) {
        context.getSharedPreferences(prefKey, Context.MODE_PRIVATE).edit()
            .clear()
            .apply()
    }

}