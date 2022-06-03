package com.example.darren.mytrivaiquiz.repository

import android.util.Log
import com.example.darren.mytrivaiquiz.data.DataOrException
import com.example.darren.mytrivaiquiz.model.QuestionItem
import com.example.darren.mytrivaiquiz.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: QuestionApi
){
    private val dataOrException
            = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()
    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>,Boolean,Exception>{
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()){
                dataOrException.loading = false
            }

        } catch (exception: Exception){
            dataOrException.e = exception
            Log.d("TAG", "getAllQuestions${dataOrException.e!!.localizedMessage}")
        }
        return dataOrException
    }
}