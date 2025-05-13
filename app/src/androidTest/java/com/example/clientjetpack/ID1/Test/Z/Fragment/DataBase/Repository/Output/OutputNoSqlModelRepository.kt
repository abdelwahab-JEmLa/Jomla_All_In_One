package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Output

import com.example.clientjetpack.ID1.Test._ID1.Test.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
}
