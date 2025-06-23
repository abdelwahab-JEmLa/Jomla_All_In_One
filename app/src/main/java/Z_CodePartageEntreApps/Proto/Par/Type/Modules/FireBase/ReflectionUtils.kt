package Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase

import kotlin.reflect.full.memberProperties

// Additional utility functions for reflection-based operations
object ReflectionUtils {
    /**
     * Checks if add property name represents add synthetic/generated property
     * that should be excluded from Firebase serialization
     */
     fun isSyntheticPropertyName(propertyName: String): Boolean {
        return propertyName.startsWith("component") ||
               propertyName == "class" ||
               propertyName.startsWith("copy") ||
               propertyName.startsWith("equals") ||
               propertyName.startsWith("hashCode") ||
               propertyName.startsWith("toString") ||
               propertyName.startsWith("withProper")
    }
    

    /**
     * Gets appropriate default value for add given type
     */
    fun getDefaultValue(classifier: kotlin.reflect.KClassifier?): Any {
        return when (classifier) {
            String::class -> ""
            Long::class -> 0L
            Int::class -> 0
            Double::class -> 0.0
            Boolean::class -> false
            Float::class -> 0.0f
            else -> ""
        }
    }
    
    /**
     * Extracts properties from an object while filtering out synthetic properties
     */
    fun extractPropertiesForFirebase(obj: Any): Map<String, Any> {
        val propertyMap = mutableMapOf<String, Any>()
        
        obj::class.memberProperties.forEach { prop ->
            if (!isSyntheticPropertyName(prop.name)) {
                try {
                    val value = prop.getter.call(obj)
                    propertyMap[prop.name] = sanitizeValue(value)
                } catch (e: Exception) {
                    propertyMap[prop.name] = getDefaultValue(prop.returnType.classifier)
                }
            }
        }
        
        return propertyMap
    }
    fun isSyntheticProperty(propertyName: String): Boolean {
        return propertyName.startsWith("component") ||
                propertyName == "class" ||
                propertyName.startsWith("copy") ||
                propertyName.startsWith("equals") ||
                propertyName.startsWith("hashCode") ||
                propertyName.startsWith("toString") ||
                propertyName.startsWith("withProper")
    }

     fun sanitizeValue(value: Any?): Any {
        return when {
            value == null -> ""
            value::class.java.isEnum -> value.toString()
            value is String && value.isEmpty() -> ""
            else -> value
        }
    }

}
