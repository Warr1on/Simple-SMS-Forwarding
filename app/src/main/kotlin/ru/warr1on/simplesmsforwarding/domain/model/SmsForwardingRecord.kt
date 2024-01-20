package ru.warr1on.simplesmsforwarding.domain.model

data class SmsForwardingRecord(
    val id: String,
    val messageAddress: String,
    val messageBody: String,
    val isFulfilled: Boolean,
    val resultDescription: String
)
