package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Output

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
    
    fun loadImbriquantData()
}
