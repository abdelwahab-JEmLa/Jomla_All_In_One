package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass

inline fun <reified T : Any> mapSnapshotToObjects(
    snapshot: DataSnapshot,
    kClass: KClass<T>
): List<T> {
    val results = mutableListOf<T>()

    for (childSnap in snapshot.children) {
        try {
            val constructor = kClass.constructors.firstOrNull()
                ?: throw Exception("No constructor found for ${kClass.simpleName}")

            // Create a map to hold parameter values
            val paramValues = mutableMapOf<String, Any?>()

            // Get id and nom for keyFireBase generation - collect these first
            var id: Long = 0
            var nom: String = ""

            // First pass: collect id and nom
            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                if (paramName == "id") {
                    val idValue = childSnap.child(paramName).getValue(Long::class.java) ?: 0L
                    id = idValue
                } else if (paramName == "nom") {
                    val nomValue = childSnap.child(paramName).getValue(String::class.java) ?: ""
                    nom = nomValue
                }
            }

            // Second pass: collect all values including keyFireBase
            for (param in constructor.parameters) {
                val paramName = param.name ?: continue

                when (paramName) {
                    "id" -> {
                        paramValues[paramName] = id
                    }

                    "nom" -> {
                        paramValues[paramName] = nom
                    }

                    "keyFireBase" -> {
                        // Now we can safely generate keyFireBase with id and nom
                        paramValues[paramName] = childSnap.key ?: getKeyFireBase(id, nom)
                    }

                    else -> {
                        val childValue = childSnap.child(paramName)
                        if (childValue.exists()) {
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] =
                                    childValue.getValue(Long::class.java)

                                String::class -> paramValues[paramName] =
                                    childValue.getValue(String::class.java)

                                Boolean::class -> paramValues[paramName] =
                                    childValue.getValue(Boolean::class.java)

                                Double::class -> paramValues[paramName] =
                                    childValue.getValue(Double::class.java)

                                Int::class -> paramValues[paramName] =
                                    childValue.getValue(Int::class.java)?.toLong()

                                else -> paramValues[paramName] = null
                            }
                        } else {
                            // Provide default values for missing parameters
                            when (param.type.classifier) {
                                Long::class -> paramValues[paramName] = 0L
                                String::class -> paramValues[paramName] = ""
                                Boolean::class -> paramValues[paramName] = false
                                Double::class -> paramValues[paramName] = 0.0
                                Int::class -> paramValues[paramName] = 0
                                else -> paramValues[paramName] = null
                            }
                        }
                    }
                }
            }

            // Map the values to constructor parameters
            val parameters = constructor.parameters.associateWith { param ->
                val paramName = param.name ?: ""
                paramValues[paramName]
            }

            val instance = constructor.callBy(parameters)
            results.add(instance)
        } catch (e: Exception) {
            // Enhanced error logging
            println("Error mapping ${kClass.simpleName}: ${e.message}")
            println("Failed to map child with key: ${childSnap.key}")
            e.printStackTrace()
        }
    }

    return results
}
