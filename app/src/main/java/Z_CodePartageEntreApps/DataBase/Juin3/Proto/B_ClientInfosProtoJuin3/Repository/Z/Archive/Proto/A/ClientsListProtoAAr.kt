package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.A

data class ClientsListProtoAAr(
    val vidSu: Long = 0,
    var idClientsSu: Long = 0,
    var nomClientsSu: String = "",
    var bonDuClientsSu: String = "",
    val couleurSu: String = "#FFFFFF", // Default color
    var currentCreditBalance: Double = 0.0, // New field for current credit balance
) {
    constructor() : this(0)
}
