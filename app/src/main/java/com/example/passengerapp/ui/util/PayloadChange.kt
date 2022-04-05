package com.example.passengerapp.ui.util

data class PayloadChange<out T>(
    val oldData: T,
    val newData: T
)

fun <T> createCombinedPayload(payloads: List<PayloadChange<T>>): PayloadChange<T> {
    assert(payloads.isNotEmpty())
    val firstChange = payloads.first()
    val lastChange = payloads.last()

    return PayloadChange(firstChange.oldData, lastChange.newData)
}