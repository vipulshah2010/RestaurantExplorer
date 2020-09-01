package com.dott.restaurantexplorer.api

import com.dott.restaurantexplorer.BuildConfig
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Base class
 */
abstract class QueryBuilder {

    private val CATEGORY_ID = "4d4b7105d754a06374d81259"
    private val DATE = dateFormat.format(Date())
    private val LIMIT = "50"
    private val INTENT = "browse"

    private val baseQueryParams by lazy {
        mapOf(
            "client_id" to BuildConfig.CLIENT_ID,
            "client_secret" to BuildConfig.CLIENT_SECRET
        )
    }

    fun build(): Map<String, String> {
        val queryParams = HashMap(baseQueryParams)
        queryParams["categoryId"] = CATEGORY_ID
        queryParams["v"] = DATE
        queryParams["limit"] = LIMIT
        queryParams["intent"] = INTENT
        putQueryParams(queryParams)
        return queryParams
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
    }

    abstract fun putQueryParams(queryParams: MutableMap<String, String>)
}
