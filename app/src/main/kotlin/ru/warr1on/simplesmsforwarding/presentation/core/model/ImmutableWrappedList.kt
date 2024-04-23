package ru.warr1on.simplesmsforwarding.presentation.core.model

import androidx.compose.runtime.Immutable

/**
 * An @Immutable-tagged wrapper for lists to use in Compose.
 *
 * Guarantees to the compose compiler that the data in the wrapped list
 * won't change, which helps to avoid unnecessary recompositions.
 *
 * On its own it doesn't actually mean that the data in the list won't change,
 * so it should be the responsibility of the one creating it to make sure that
 * the list won't actually be mutated. To guarantee that, use the constructor
 * method [safeInit], which will copy the list and wrap its copy, instead of
 * [unsafeInit], which will pass the list to the wrapper by reference.
 *
 * The underlying list could be accessed by a [value] property
 * or simply by calling invoke on the wrapper, like "wrapper()"
 *
 * @property value The list wrapped in the immutable-tagged wrapper
 */
@Immutable
class ImmutableWrappedList<T> {

    val value: List<T>

    operator fun invoke(): List<T> {
        return value
    }

    private constructor(wrappedValue: List<T>) {
        value = wrappedValue
    }

    companion object {

        /**
         * Creates a new [ImmutableWrappedList] with the wrapped [value]
         * in a safe way. By using this constructor instead of [unsafeInit],
         * the list is copied and not passed by reference, which guarantees
         * that it won't be changed and the immutability contract with the
         * compose compiler would be strictly adhered to.
         *
         * @param value The list that should be wrapped into [ImmutableWrappedList]
         */
        fun <T> safeInit(value: List<T>): ImmutableWrappedList<T> {
            val copiedList = buildList { addAll(value) }
            return ImmutableWrappedList(copiedList)
        }

        /**
         * Creates a new [ImmutableWrappedList] with the wrapped [value]
         * in an unsafe way. It is unsafe in the sense that the list is
         * passed by reference and there's no guarantee that it wouldn't
         * be changed somewhere outside of the scope of the wrapper.
         *
         * So, it may help with the performance on large data sets, but you should
         * be careful not to break the contract of immutability with the compose compiler,
         * and only use this constructor if you're sure that the list won't change.
         *
         * @param value The list that should be wrapped into [ImmutableWrappedList]
         */
        fun <T> unsafeInit(value: List<T>): ImmutableWrappedList<T> {
            return ImmutableWrappedList(value)
        }
    }
}
