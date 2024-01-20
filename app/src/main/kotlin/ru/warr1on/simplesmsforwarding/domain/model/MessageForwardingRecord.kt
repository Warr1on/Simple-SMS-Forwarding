package ru.warr1on.simplesmsforwarding.domain.model

data class MessageForwardingRecord(
    val id: String,
    val messageAddress: String,
    val messageBody: String,
    val isFulfilled: Boolean,
    val resultDescription: String
)
