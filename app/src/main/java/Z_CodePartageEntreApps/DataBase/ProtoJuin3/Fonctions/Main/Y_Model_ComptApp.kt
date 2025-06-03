package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main

data class Y_Model_ComptApp(
    val id: Long,
    val dernierFireBaseListening: Long = System.currentTimeMillis(),
    var lenceUpdateDepuitAncien: Boolean = false
)
