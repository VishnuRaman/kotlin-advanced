package com.vishnuraman.internals

object ReifiedTypes {

    // does not work
//    fun <T> filterByType(list: List<Any>): List<T> {
//        return list.filter { it is T }.map{ it as T}
//    }

    // JVM has type erasure
    // Java has this notion of backward compatibility guarantee
    // Generics were added in Java 5 (2004)
    // Java pre-5
    // List thing = new ArrayList()
    // Java 5
    // List<String> thing = new ArrayList<String>()
    // type erasure
    // List thing = ArrayList()

    // solution is inline fun + reified type
    // inline fun rewrites every function call to the implementation of the function
    inline fun <reified T> List<Any>.filterByType(): List<T> {
        return this.filter { it is T }.map { it as T }
    }


    data class Person(val name: String, val age: Int)
    data class Car(val make: String, val model: String)

    @JvmStatic
    fun main(args: Array<String>) {
        val mixedList: List<Any> = listOf(
            Person("Alice", 30),
            Car("Toyota", "Camry"),
            Car("Honda", "Civic"),
            Person("Vishnu", 32),
            "String",
            42
        )

        val people:List<Person> = mixedList.filterByType()
        // rewritten to:
        // mixedList.filter { it is Person }.map { it as Person } which is legal, can be performed at runtime

        val cars: List<Car> = mixedList.filterByType()
        val strings: List<String> = mixedList.filterByType()
        val numbers: List<Int> = mixedList.filterByType()

        println("Persons: $people")
        println("Cars: $cars")
        println("Strings: $strings")
        println("Numbers: $numbers")

    }


}