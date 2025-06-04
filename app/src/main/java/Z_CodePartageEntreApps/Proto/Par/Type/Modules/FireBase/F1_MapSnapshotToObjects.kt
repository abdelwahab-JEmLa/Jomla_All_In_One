package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import Z_CodePartageEntreApps.Model.getKeyFireBase
import com.google.firebase.database.DataSnapshot
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters



inline fun <reified T : Any> getDatasFixed(
    snapshot: DataSnapshot,
    results: MutableList<T>
) {
    for (childSnap in snapshot.children) {
        try {
            val mappedObject = mapSnapshotToDynamicObject<T>(childSnap)
            mappedObject?.let { results.add(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

inline fun <reified T : Any> mapSnapshotToDynamicObject(childSnap: DataSnapshot): T? {
    return try {
        when (T::class) {
            A_ProduitInfos::class -> mapToProduitInfosDynamic(childSnap) as? T
            D_TarificationInfos::class -> mapToTarificationInfosDynamic(childSnap) as? T
            else -> null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun mapToTarificationInfosDynamic(childSnap: DataSnapshot): D_TarificationInfos? {
    return try {
        val constructor = D_TarificationInfos::class.primaryConstructor ?: return null
        val args = mutableMapOf<String, Any?>()

        constructor.valueParameters.forEach { param ->
            val paramName = param.name ?: return@forEach // FIXED: Safe handling of nullable parameter name
            val value = when (paramName) {
                "typeTarificationEnumT2Correspond" -> {
                    val enumString = childSnap.child(paramName).getValue(String::class.java) ?: "PRIX_BASE"
                    // FIXED: Using paramName variable instead of param.name property
                    try {
                        TypeTarificationEnumT2.valueOf(enumString)
                    } catch (e: IllegalArgumentException) {
                        TypeTarificationEnumT2.PRIX_BASE
                    }
                }
                else -> getValueWithDefault(childSnap, paramName, param.type)
            }
            args[paramName] = value
        }

        // Special handling for computed fields
        val id = args["id"] as? Long ?: 0L
        val nom = args["nom"] as? String ?: ""
        val keyFireBase = childSnap.key ?: getKeyFireBase(id, nom)

        args["keyFireBase"] = keyFireBase
        args["timestamps"] = childSnap.child("timestamps").getValue(Long::class.java) ?: System.currentTimeMillis()
        args["needUpdate"] = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: true

        createInstanceFromMap<D_TarificationInfos>(constructor, args)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getDefaultValue(classifier: KClass<*>?, isNullable: Boolean): Any? {
    if (isNullable) return null

    return when (classifier) {
        String::class -> ""
        Long::class -> 0L
        Int::class -> 0
        Double::class -> 0.0
        Boolean::class -> false
        Float::class -> 0.0f
        else -> {
            if (classifier?.java?.isEnum == true) {
                // Return first enum value as default
                classifier.java.enumConstants?.firstOrNull()
            } else {
                null
            }
        }
    }
}

inline fun <reified T : Any> createInstanceFromMap(
    constructor: kotlin.reflect.KFunction<T>,
    args: Map<String, Any?>
): T {
    val orderedArgs = constructor.valueParameters.map { param ->
        args[param.name] ?: getDefaultValue(
            param.type.classifier as? KClass<*>,
            param.type.isMarkedNullable
        )
    }.toTypedArray()

    return constructor.call(*orderedArgs)
}


fun getValueWithDefault(childSnap: DataSnapshot, fieldName: String, type: KType): Any? {
    return try {
        val classifier = type.classifier as? KClass<*>
        val isNullable = type.isMarkedNullable

        val value = when (classifier) {
            String::class -> childSnap.child(fieldName).getValue(String::class.java)
            Long::class -> childSnap.child(fieldName).getValue(Long::class.java)
            Int::class -> childSnap.child(fieldName).getValue(Int::class.java)
            Double::class -> childSnap.child(fieldName).getValue(Double::class.java)
            Boolean::class -> childSnap.child(fieldName).getValue(Boolean::class.java)
            Float::class -> childSnap.child(fieldName).getValue(Float::class.java)
            else -> {
                // Handle enums and other custom types
                if (classifier?.java?.isEnum == true) {
                    val enumString = childSnap.child(fieldName).getValue(String::class.java)
                    if (enumString != null) {
                        try {
                            // FIXED: Proper enum casting
                            val enumClass = classifier.java as Class<out Enum<*>>
                            enumClass.enumConstants?.find { it.name == enumString }
                        } catch (e: Exception) {
                            getDefaultValue(classifier, isNullable)
                        }
                    } else {
                        getDefaultValue(classifier, isNullable)
                    }
                } else {
                    childSnap.child(fieldName).value
                }
            }
        }

        // Return value or default if null and not nullable
        if (value == null && !isNullable) {
            getDefaultValue(classifier, false)
        } else {
            value
        }
    } catch (e: Exception) {
        val classifier = type.classifier as? KClass<*>
        getDefaultValue(classifier, type.isMarkedNullable)
    }
}

fun mapToProduitInfosDynamic(childSnap: DataSnapshot): A_ProduitInfos? {
    return try {
        val constructor = A_ProduitInfos::class.primaryConstructor ?: return null
        val args = mutableMapOf<String, Any?>()

        constructor.valueParameters.forEach { param ->
            val paramName = param.name ?: return@forEach // FIXED: Safe handling of nullable parameter name
            val value = getValueWithDefault(childSnap, paramName, param.type)
            args[paramName] = value
        }

        // Special handling for computed fields
        val id = args["idArticle"] as? Long ?: 0L
        val nomArticleFinale = args["nomArticleFinale"] as? String ?: ""
        val keyFireBase = childSnap.key ?: getKeyFireBase(id, nomArticleFinale)

        args["keyFireBase"] = keyFireBase
        args["timestamps"] = childSnap.child("timestamps").getValue(Long::class.java) ?: System.currentTimeMillis()
        args["needUpdate"] = childSnap.child("needUpdate").getValue(Boolean::class.java) ?: true

        createInstanceFromMap<A_ProduitInfos>(constructor, args)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
