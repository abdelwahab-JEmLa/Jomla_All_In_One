package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass

inline fun <reified T : Any> getDatas(
    snapshot: DataSnapshot,
    kClass: KClass<T>,
    results: MutableList<T>
) {
    for (childSnap in snapshot.children) {
        try {
            val constructor = kClass.constructors.firstOrNull()
                ?: throw Exception("No constructor found for ${kClass.simpleName}")

            val paramValues = mutableMapOf<String, Any?>()
            var id: Long = 0
            var nom: String = ""

            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                when (paramName) {
                    "id" -> {
                        val idValue = childSnap.child(paramName).getValue(Long::class.java) ?: 0L
                        id = idValue
                    }
                    "nom" -> {
                        val nomValue = childSnap.child(paramName).getValue(String::class.java) ?: ""
                        nom = nomValue
                    }
                }
            }

            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                when (paramName) {
                    "id" -> paramValues[paramName] = id
                    "nom" -> paramValues[paramName] = nom
                    "keyFireBase" -> {
                        paramValues[paramName] = childSnap.key ?: getKeyFireBase(id, nom)
                    }
                    "needUpdate" -> {
                        val childValue = childSnap.child(paramName)
                        paramValues[paramName] = if (childValue.exists()) {
                            childValue.getValue(Boolean::class.java) ?: true
                        } else {
                            true
                        }
                    }
                    else -> {
                        val childValue = childSnap.child(paramName)
                        if (childValue.exists()) {
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] =
                                    childValue.getValue(Long::class.java) ?: 0L
                                String::class -> paramValues[paramName] =
                                    childValue.getValue(String::class.java) ?: ""
                                Boolean::class -> paramValues[paramName] =
                                    childValue.getValue(Boolean::class.java) ?: false
                                Double::class -> paramValues[paramName] =
                                    childValue.getValue(Double::class.java) ?: 0.0
                                Int::class -> paramValues[paramName] =
                                    childValue.getValue(Int::class.java)?.toLong() ?: 0L
                                else -> paramValues[paramName] = null
                            }
                        } else {
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] = 0L
                                String::class -> paramValues[paramName] = ""
                                Boolean::class -> paramValues[paramName] = false
                                Double::class -> paramValues[paramName] = 0.0
                                Int::class -> paramValues[paramName] = 0L
                                else -> paramValues[paramName] = null
                            }
                        }
                    }
                }
            }

            val parameters = constructor.parameters.associateWith { param ->
                val paramName = param.name ?: ""
                paramValues[paramName]
            }

            val instance = constructor.callBy(parameters)
            results.add(instance)

        } catch (e: Exception) {
            // Silently continue processing other items
        }
    }
}
