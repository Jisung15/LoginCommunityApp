package com.example.logincommunityapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesUtil {
    private const val NAME = "listName"
    private const val LIST = "itemListData"

    // 메인 화면 리스트 저장
    fun saveItemList(context: Context, itemList: List<Item>) {
        val shared = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val editor = shared.edit()
        val json = Gson().toJson(itemList)
        editor.putString(LIST, json)
        editor.apply()
    }

    // 메인 화면 리스트 불러오기
    fun getItemList(context: Context): MutableList<Item> {
        val shared = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val json = shared.getString(LIST, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Item>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}