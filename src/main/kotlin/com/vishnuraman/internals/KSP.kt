package com.vishnuraman.internals

import com.vishnuraman.builderksp.Builder

@Builder
data class Person(val name: String, val age: Int)

@Builder
data class Pet(val name: String, val nickname: String)


object KSP {

    /*
        - analyze source code and generate
        - new source code
        - compile
        - access methods/functionality at COMPILE TIME
     */

    // Use-case: generate builder patterns for data classes
    /*
        PersonBuilder
            .name("Vishnu")
            .age(99)
            .property(value)
            ...
            .build()
     */




    // module 1 - symbol definitions (annotations)
    // module 2 - KSP logic for generating the source
    // module 3 - source + the place where the generated source will be created
    @JvmStatic
    fun main(args: Array<String>) {
        val masterYoda = PersonBuilder().name("Master Yoda").age(800).build()
        val garfield = PetBuilder().name("Garfield").nickname("Chubby Boi").build()
        println(masterYoda)
        println(garfield)
    }
}