package com.sergi.tfg_app.data.remote.api

import com.sergi.tfg_app.data.remote.dto.CreateCvResponse
import com.sergi.tfg_app.data.remote.dto.ImprovedCvResponse
import com.sergi.tfg_app.data.remote.dto.ScraperStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CvApi {

    @Multipart
    @POST("cvs/base/create/")
    suspend fun createBaseCv(
        @Part pdfFile: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("position") position: RequestBody
    ): Response<CreateCvResponse>

    @GET("scraper/{scraperId}/status/")
    suspend fun getScraperStatus(
        @Path("scraperId") scraperId: String
    ): Response<ScraperStatusResponse>

    @GET("cvs/improved/{cvId}/")
    suspend fun getImprovedCv(
        @Path("cvId") cvId: String
    ): Response<ImprovedCvResponse>
}
