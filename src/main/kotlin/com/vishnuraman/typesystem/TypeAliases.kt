package com.vishnuraman.typesystem

// type aliases have to be declared top-level, not nested
typealias Phonebook = Map<String, String>

// type aliases can have generic type arguments
typealias Table<A> = Map<String, A>

// type aliases can be used as field types
class Person(val name: String, val phone: Phonebook)

// type aliases can be used as return types

// example
class Either<out E, out A>
// variance modifiers carry over to the type aliases
typealias ErrorOr<A> = Either<Throwable, A>


object TypeAliases {

    val phonebook: Phonebook = mapOf(
        "Vishnu" to "555-1234",
        "Raman" to "555-3456"
    )

    val theMap: Map<String, String> = phonebook

    val theTable: Table<String> = phonebook

    @JvmStatic
    fun main(args: Array<String>) {

    }
}