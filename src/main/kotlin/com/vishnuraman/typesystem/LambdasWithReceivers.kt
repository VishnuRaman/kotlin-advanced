package com.vishnuraman.typesystem

object LambdasWithReceivers {

    // create a behaviour
    // option 1 - 00 way
    data class Person(val name: String, val age: Int) {
        fun greet() = "Hello, My name is $name"
    }

    // option 2 (procedural way) - create a function that takes a person
    fun greet(p: Person) = "Hello, My name is ${p.name}"

    // option 3 - extension method (Kotlin/Scala)
    fun Person.greetExt() = "Hello, My name is $name"

    // option 4 - function value (lambda)
    val greetFun = { p: Person -> "Hi, my name is ${p.name}" }

    // option 5 - lambda with receiver (an "extension lambda")
    val personFunRec: Person.() -> String = { "Hi, my name is $name" }
    //                ^^^^^^^^^ RECEIVER => gives us the access to the `this` reference

    val simpleLambda: () -> String = { "Kotlin" }

    // APIs that look "baked into Kotlin" aka DSL (Domain Specific Language)
    // examples: Ktor, Arrow, coroutines

    // mini - "library" for JSON serialization
    // { "name": "vishnu", "age": 32 }
    // support numbers (ints), strings, JSON objects
    sealed interface JsonValue
    data class JsonNumber(val value: Int): JsonValue {
        override fun toString(): String = value.toString()
    }
    data class JsonString(val value: String): JsonValue {
        override fun toString(): String = "\"$value\""
    }
    data class JsonObject(val attributes: Map<String, JsonValue>): JsonValue {
        override fun toString(): String =
            attributes.toList().joinToString(",","{","}") { pair -> "\"${pair.first}\": ${pair.second}"}
    }

    // "mutable builder" of a JsonObject

    class JSONScope {
        private var props: MutableMap<String, JsonValue> = mutableMapOf()

        fun toValue(): JsonValue = JsonObject(props)

        // "not so nice API"
        fun addString(name: String, value: String) {
            props[name] = JsonString(value)
        }

        fun addInt(name: String, value: Int) {
            props[name] = JsonNumber(value)
        }

        fun addValue(name: String, value: JsonValue) {
            props[name] = value
        }

        // "nice API"
        infix fun String.to(value: String) { // "name" to "Daniel"
            props[this] = JsonString(value)
        }

        infix fun String.to(value: Int) { // "age" to 12
            props[this] = JsonNumber(value)
        }

        infix fun String.to(value: JsonValue) { // "credentials" to ...
            props[this] = value
        }
    }

    fun jsonNotSoNice(init: (JSONScope) -> Unit): JsonValue {
        val obj = JSONScope()
        init(obj)
        return obj.toValue()
    }

    fun json(init: JSONScope.() -> Unit): JsonValue {
        val obj = JSONScope()
        obj.init()
        return obj.toValue()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val jsonObj = JsonObject(
            mapOf(
                "user" to JsonObject(
                    mapOf(
                        "name" to JsonString("Daniel"),
                        "age" to JsonNumber(99)
                    )
                ),
                "credentials" to JsonObject(
                    mapOf(
                        "type" to JsonString("password"),
                        "value" to JsonString("rockthejvm")
                    )
                )
            )
        )

        val jsonObj_v2 = jsonNotSoNice { j ->
            j.addValue("user", jsonNotSoNice { j2 ->
                j2.addString("name", "Vishnu")
                j2.addInt("age", 99)
            })
            j.addValue("credentials", jsonNotSoNice { j2 ->
                j2.addString("type", "password")
                j2.addString("value", "rockthejvm")
            })
        }


        val jsonObj_3 = json {
            "user" to json {
                "name" to "Vishnu"
                "age" to 99
            }
            "credentials" to json {
                "type" to "password"
                "value" to "rockthejvm"
            }
        }


        println(jsonObj_3)


    }
}