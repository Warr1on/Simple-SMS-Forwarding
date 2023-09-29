package ru.warr1on.simplesmsforwarding.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForwardSmsRequest(

    @SerializedName("address")
    val address: String,

    @SerializedName("body")
    val body: String,

    @SerializedName("sender_key")
    val senderKey: String,

    @SerializedName("type_key")
    val messageTypeKey: String
)
