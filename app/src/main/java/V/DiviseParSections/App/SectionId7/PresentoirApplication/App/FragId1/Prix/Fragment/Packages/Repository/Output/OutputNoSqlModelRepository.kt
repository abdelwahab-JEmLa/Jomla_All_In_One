package com.example.clientjetpack.ID1.Test.Packages.Repository.Output

import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
}
