package com.example.darren.mytrivaiquiz.network

import com.example.darren.mytrivaiquiz.model.Question
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface QuestionApi {
    @GET(value="world.json")
    suspend fun getAllQuestions(): Question
}