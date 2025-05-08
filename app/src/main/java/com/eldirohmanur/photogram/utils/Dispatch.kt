package com.eldirohmanur.photogram.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class Dispatch(
    val io: CoroutineDispatcher = Dispatchers.IO,
    val main: CoroutineDispatcher = Dispatchers.Main,
    val default: CoroutineDispatcher = Dispatchers.Default
)
