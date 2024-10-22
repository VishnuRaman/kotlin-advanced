package com.vishnuraman.internals

import java.util.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*
import kotlin.reflect.full.*

object ReflectionBasics {

    // reflection = inspects and invokes functionality dynamically at runtime
    data class Person(val name: String, val age: Int) {
        var favouriteMovie: String = "The Wailing"

        fun fillInTaxForm(authority: String) = "[$name] Death and taxes ... Filling in tax form for $authority"

    }

    /*
        UI
            select method:
                - fillInTaxForm
                - downloadPersonalData
                ... (passed as a String)
     */

    val personClass: KClass<*> = Person::class // <- reflective call


    @JvmStatic
    fun main(args: Array<String>) {
        // class reference => class name, methods, properties, ...
        println("-----------Class basic info-----------")
        println("Class name: ${personClass.simpleName}")
        println("JVM Name: ${personClass.jvmName}") // JVM Name: com.vishnuraman.internals.ReflectionBasics$Person $ means Person inside something
        println("Qualified Name: ${personClass.qualifiedName}")

        // class type
        println("Class is final: ${personClass.isFinal}")

        // visibility
        println("access modifier: ${personClass.visibility}")

        // inspect properties at RUNTIME
        val properties = personClass.declaredMemberProperties
        println("-----------Class properties-----------")
        properties.forEach { prop ->
            println("Name: ${prop.name}, type: ${prop.returnType.classifier}, is nullable: ${prop.returnType.isMarkedNullable}")
        }

        // refer to a particular property (AT RUNTIME) on an instance
        val vishnu = Person("Vishnu", 32)

        println("-----------Vishnu's properties-----------")
        val vishnusProperties = properties.map { prop ->
            "${prop.name} -> ${prop.call(vishnu)}" // vishnu.$prop
        }

        vishnusProperties.forEach { println(it) }

        // can mutate properties dynamically
        val favMovieProp = properties.find { it.name.lowercase(Locale.getDefault()).contains("movie") }
        if (favMovieProp != null && favMovieProp is KMutableProperty<*>)
            favMovieProp.setter.call(vishnu, "Dune")
        println("After mutation: ${vishnu.favouriteMovie}")

        // inspect functions
        println("-----------Class functions-----------")
        val functions = personClass.declaredFunctions
        functions.forEach { fn ->
            val fnName = fn.name
            val params = fn.parameters
            val returnType = fn.returnType
            println("Function $fnName: ${params.joinToString(",") { it.type.toString() }} -> $returnType")
        }

        // call a function
        // contains "tax"
        val taxFunc = functions.find { it.name.lowercase(Locale.getDefault()).contains("tax") }
        if (taxFunc != null) {
            // first arg is ALWAYS the instance on which you want to call it
            println(taxFunc.call(vishnu, "THE ROYALTY"))
        }
        // inspect and invoke constructors
        println("----------- Class functions -----------")
        val primaryConstructor = personClass.primaryConstructor
        if (primaryConstructor != null) {
            // dynamically instantiate instances
            val newPerson = primaryConstructor.call("Jane", 22)
            println("new person instantiated: $newPerson")
            // can invoke functions with a map with all the args
            val params = primaryConstructor.parameters
            val newPerson_v2 = primaryConstructor.callBy(mapOf(
                params[0] to "Jane",
                params[1] to 22
            ))
            println("new person instantiated via parameter map: $newPerson_v2")
        }

        // inspect and use companions
        println("----------- Class companion object -----------")
        val companionType = personClass.companionObject // can inspect props/methods of the comp object
        val companionObject = personClass.companionObjectInstance
        companionType?.declaredMemberProperties?.forEach { property ->
            println("Name: ${property.name}, type: ${property.returnType}, value: ${property.call(companionObject)}")
        } ?: println("Nothing (companion is null)")
    }
}