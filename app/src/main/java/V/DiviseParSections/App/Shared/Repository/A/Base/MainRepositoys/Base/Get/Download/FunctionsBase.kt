package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download

object FunctionsBase {
    inline fun Long?.ifNotNullOrZero(block: () -> Unit) {
        if (this != null && this != 0L) block()
    }

    inline fun String?.ifNotNullOrEmpty(block: () -> Unit) {
        if (!this.isNullOrEmpty()) block()
    }


    inline fun Boolean.ifTrue(block: () -> Unit) {
        if (this) block()
    }

    inline fun Boolean.ifFalse(block: () -> Unit) {
        if (!this) block()
    }

    fun String?.empty_If_Null(value: String = ""): String {
        return this ?: value
    }
}
