package com.sergi.tfg_app.data.remote.api

import retrofit2.Response
import retrofit2.http.DELETE

interface UserApi {

    @DELETE("users/delete/")
    suspend fun deleteAccount(): Response<Unit>
}
