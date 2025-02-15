package com.example.logincommunityapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val profileImage: String = "",
    val idText: String = "",
    val content: String = "",
    val heartCount: String = "",
    val answerCount: String = ""
): Parcelable
