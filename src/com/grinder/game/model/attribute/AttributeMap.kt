package com.grinder.game.model.attribute

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.grinder.game.model.attribute.value.BooleanValueHolder
import com.grinder.game.model.attribute.value.NumericalValueHolder
import com.grinder.game.model.attribute.value.StringValueHolder
import org.apache.logging.log4j.LogManager
import kotlin.reflect.jvm.jvmName

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2018-12-19
 * @version 1.0
 */
class AttributeMap : HashMap<String, Attribute<AttributeValueHolderTemplate<*>>>(){

    inline fun<reified T : AttributeValueHolderTemplate<*>> get(identifier: String,
                                                                defaultValueProvider: () -> T? = {null}) : T {

        val value = this[identifier]

        if(value == null) {
            val defaultValue = defaultValueProvider.invoke()
            if(defaultValue != null)
                set(identifier, Attribute(defaultValue))
        }

        return this[identifier]!!.valueHolder as T
    }

    inline fun<V, reified T : AttributeValueHolderTemplate<V>> getValue(identifier: String,
                                                                        defaultValueProvider: () -> T? = {null}) : V {
        return get(identifier, defaultValueProvider).value
    }

    fun set(identifier: String, attribute : Attribute<AttributeValueHolderTemplate<*>>){
        this[identifier] = attribute
    }

    fun reset(identifier: String){
        get(identifier)?.valueHolder?.reset()
    }

    fun bool(identifier: String) = boolAttr(identifier, false).value
    fun boolAttr(identifier: String, boolean: Boolean) = get(identifier) {BooleanValueHolder(boolean)}

    fun numInt(identifier: String) = numAttr(identifier, 0).value.toInt()
    fun numAttr(identifier: String, default: Number) = get(identifier) {NumericalValueHolder(default)}
    fun stringAttr(identifier: String, default: String) = get(identifier) {StringValueHolder(default)}

    fun serialize() : JsonObject {

        val serialized = JsonObject()


            ATTRIBUTE_ANNOTATION_FIELDS.forEach { fields ->
                run {
                    try {
                        val serializedAttributes = JsonObject()

                        fields.value
                                .map { it.get(null) as String }
                                .filter { containsKey(it) }
                                .sortedBy { it.length }
                                .forEach { name ->
                                    try {
                                        val value = get(name)!!.valueHolder
                                        if (value.save())
                                            serializedAttributes.add(name, value.serialize())
                                    } catch (exception: Exception){
                                        LOGGER.error("Failed to serialise value for attribute {$name}", exception)
                                    }
                                }

                        serialized.add(fields.key.value.simpleName, serializedAttributes)
                    } catch (exception: Exception){
                        LOGGER.error("Could not save attributes of type {${fields.key.value.simpleName}}", exception)
                    }
                }
            }


        return serialized
    }

    companion object {

        private val LOGGER = LogManager.getLogger(AttributeMap::class.java)!!

        val ATTRIBUTE_ANNOTATION_FIELDS = Attribute::class.java.declaredFields
            .filter { it.isAnnotationPresent(Value::class.java)}
            .groupBy { it.getDeclaredAnnotation(Value::class.java) }

        /**
         * Used to load an [AttributeMap] through [Gson].
         *
         * @param input the [JsonObject] that contains attribute data.
         * @return a new [AttributeMap] from the de-serialized [input].
         */
        fun deserialize(input : JsonObject) : AttributeMap {

            val output = AttributeMap()

            ATTRIBUTE_ANNOTATION_FIELDS.forEach { fields ->
                run {
                    try {
                        val valueHolderClass = fields.key.value
                        val valueHolderName = valueHolderClass.simpleName
                        val unsafeClass = Class.forName(valueHolderClass.jvmName)
                        require(AttributeValueHolderTemplate::class.java.isAssignableFrom(unsafeClass)) {
                            "Class '$valueHolderClass' is not a AttributeValueHolderTemplate"
                        }

                        if(input.has(valueHolderName)){

                            val values = input.get(valueHolderName).asJsonObject
                            fields.value
                                    .map { it.get(null) as String }
                                    .filter { values.has(it) }
                                    .sortedBy { it.length }
                                    .forEach { name ->
                                        try {
                                            val valueHolder = unsafeClass.getDeclaredConstructor().newInstance() as AttributeValueHolderTemplate<*>
                                            valueHolder.deserialize(values.get(name))
                                            output.set(name, Attribute(valueHolder))
                                        } catch (exception: Exception){
                                            LOGGER.error("Failed to de-serialise value for attribute {$name}", exception)
                                        }
                                    }
                        }
                    } catch (exception: Exception){
                        LOGGER.error("Could not load attributes of type {${fields.key.value.simpleName}}", exception)
                    }
                }
            }
            return output
        }
    }
}