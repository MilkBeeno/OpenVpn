package com.milk.open.net.api

import com.milk.open.data.ApiResponse
import com.milk.open.data.AppConfigModel
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {

    @POST("/v1/app/mobile/conf")
    suspend fun getAppConfig(@Body appConfigBody: com.milk.open.data.body.AppConfigRequestModel): ApiResponse<AppConfigModel>
}