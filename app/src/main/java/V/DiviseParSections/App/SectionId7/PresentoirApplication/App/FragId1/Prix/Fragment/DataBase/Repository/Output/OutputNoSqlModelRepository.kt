package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Output

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.OutputNoSqlModel
import kotlinx.coroutines.flow.StateFlow

interface OutputNoSqlModelRepository {
    val dataFlow: StateFlow<OutputNoSqlModel>
    
    fun loadImbriquantData()
}
