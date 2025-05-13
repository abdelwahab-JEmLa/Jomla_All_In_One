package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Modules.Log.logHErartchiDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first

suspend fun testID2(viewModel: TarificationViewModel) {
    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "logHErartchiDataBase"
    )
}
