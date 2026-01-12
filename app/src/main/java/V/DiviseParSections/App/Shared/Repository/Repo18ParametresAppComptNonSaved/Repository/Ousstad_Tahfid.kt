package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

enum class Ousstad_Tahfid(
    val ayam_tadriss: String = "dimanch/jeudi",
    val nom_arab: String = "",
    val key: String = ""
) {
    Abdelwahab_Osstad(
        "dimanch/jeudi",
        "عبدالوهاب حمنيش" ,
        M18CentralParametresOfAllApps.get_Default().abdelwahabTravailleChezGros_KeyId

    ),
    Non_Defini_Actuellemen(
        "dimanch/jeudi",
        "غير محدد حاليا",
        "Non_Defini_Actuellemen"
    ),
    Kissm_Intikali(
        "dimanch/jeudi",
        "قسم انتقالي" ,
        "Kissm_Intikali"

    ),
    Amine_Madrassa(
        "dimanch/jeudi",
        "أمين" ,
        "Amine_Madrassa"

    )
    ;
}
