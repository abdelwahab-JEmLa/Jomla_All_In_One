package com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.InputSqlGroupeRepositorysImp
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Output.OutputNoSqlModelRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
    private val tarificationDataBaseFacileEntreRepositoryImp:
    InputSqlGroupeRepositorysImp.TarificationDataBaseFacileEntreRepositoryImp
): ViewModel(){
    private val outputRepository = OutputNoSqlModelRepositoryImp(tarificationDataBaseFacileEntreRepositoryImp)
    private val _imbriquantFlow = MutableStateFlow(OutputNoSqlModel(emptyList()))
    val imbriquantFlow: StateFlow<OutputNoSqlModel> = _imbriquantFlow.asStateFlow()

    init {
        observeTarificationData()
    }

    private fun observeTarificationData() {
        viewModelScope.launch {
            outputRepository.imbriquantFlow.collectLatest { data ->
                _imbriquantFlow.value = data
            }
        }
    }
}
