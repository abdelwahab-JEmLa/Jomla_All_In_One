package com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
    fun loadImbriquantData()
}
