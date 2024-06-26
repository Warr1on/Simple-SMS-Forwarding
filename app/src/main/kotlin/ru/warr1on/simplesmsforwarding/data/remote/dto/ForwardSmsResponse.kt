package ru.warr1on.simplesmsforwarding.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForwardSmsResponse(

    @SerializedName("result")
    val result: String,

    @SerializedName("result_description")
    val resultDescription: String,

    @SerializedName("number_of_recipients")
    val numberOfRecipients: Int?,

    @SerializedName("recipients")
    val recipients: List<String>?
)
