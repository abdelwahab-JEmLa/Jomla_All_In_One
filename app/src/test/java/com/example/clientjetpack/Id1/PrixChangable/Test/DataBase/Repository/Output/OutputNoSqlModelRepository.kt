package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Output

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining operations for accessing and managing output NoSQL model data
 */
interface OutputNoSqlModelRepository {
    /**
     * StateFlow providing access to the imbriquant data model
     */
    val imbriquantFlow: StateFlow<OutputNoSqlModel>
    
    /**
     * Forces a reload of imbriquant data
     */
    fun loadImbriquantData()
}
