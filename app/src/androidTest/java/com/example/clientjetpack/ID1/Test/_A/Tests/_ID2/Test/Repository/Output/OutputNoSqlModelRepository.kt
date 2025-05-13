package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Output

import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
}
