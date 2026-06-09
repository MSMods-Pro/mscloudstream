package com.example.api

import com.example.model.ExtensionRepository
import retrofit2.http.GET
import retrofit2.http.Url

interface RepositoryApi {
    @GET
    suspend fun getRepository(@Url url: String): ExtensionRepository
}
