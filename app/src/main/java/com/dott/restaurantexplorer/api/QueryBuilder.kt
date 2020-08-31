package com.dott.restaurantexplorer.api

import com.dott.restaurantexplorer.BuildConfig
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

abstract class QueryBuilder {
    private val baseQueryParams by lazy {
        mapOf(
            "client_id" to BuildConfig.CLIENT_ID,
            "client_secret" to BuildConfig.CLIENT_SECRET
        )
    }

    fun build(): Map<String, String> {
        val queryParams = HashMap(baseQueryParams)
        queryParams["categoryId"] = "4d4b7105d754a06374d81259"
        queryParams["v"] = dateFormat.format(Date())
        queryParams["limit"] = "50"
        queryParams["intent"] = "browse"
        putQueryParams(queryParams)
        return queryParams
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
    }

    abstract fun putQueryParams(queryParams: MutableMap<String, String>)
}
