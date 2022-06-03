package com.example.darren.mytrivaiquiz.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darren.mytrivaiquiz.data.DataOrException
import com.example.darren.mytrivaiquiz.model.QuestionItem
import com.example.darren.mytrivaiquiz.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel()
{
    val data: MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>>
            = mutableStateOf(DataOrException(null, true, Exception("")))

    init {
        getAllQuestions()
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if (data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }
}