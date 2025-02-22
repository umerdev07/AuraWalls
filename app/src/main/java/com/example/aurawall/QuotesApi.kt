package com.example.quotegeneratorapp

import retrofit2.Response
import retrofit2.http.GET

interface QuotesApi {
    @GET("random")
    suspend fun getRandomQuotes(): Response<List<QuoteModelItem>>
}