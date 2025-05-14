package com.eldirohmanur.photogram.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

suspend fun <T, I> Iterable<T>.mapAsync(
    dispatch: Dispatch,
    mapper: suspend (T) -> I
): List<I> {
    return withContext(dispatch.default) {
        map {
            async { mapper(it) }
        }
    }.awaitAll()
}