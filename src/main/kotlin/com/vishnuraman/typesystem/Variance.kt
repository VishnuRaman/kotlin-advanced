package com.vishnuraman.typesystem

object Variance {

    abstract class Pet
    class Dog(val name: String) : Pet()
    class Cat(val name: String) : Pet()

    // Dog extends Pet => List<Dog> "extends" List<Pet> ?
    // Variance question for the List type: A extends B => List<A> extends List<B> ?
    // yes => List is a COVARIANT TYPE
    // Dog is a Pet => List<Dog> is a List<Pet>

    val lassie = Dog("lassie")
    val fido = Dog("fido")
    val hachi = Dog("hachi")
    val myDogs: List<Dog> = listOf(lassie, fido, hachi)
    val myPets: List<Pet> = myDogs

    // COVARIANT generic types
    class MyList<out A> // out A means that A is a COVARIANT type
    val aListOfPets: MyList<Pet> = MyList<Cat>()  // legal

    // no - INVARIANT
    interface Combiner<A> { // semigroup
        fun combine(a: A, b: A): A
    }

    // java standard library - all Java generics are invariant
    // val aJavaList: java.util.List<Pet> = java.util.ArrayList<Dog>() // type mismatch

    // HELL NO - CONTRAVARIANCE
    // Dog is a Pet, then Thing<Pet> is a Thing<Dog>
    // Vet<Pet> is a Vet<Dog>

    class Vet<in A> {
        fun heal(pet: A) = true
    }

    val myVet: Vet<Dog> = Vet<Pet>()

    // covariant types "produce" or "get" elements => "output" elements
    // contravariant types "consume" or "acts on" elements => "input" elements

    /*
        Rule of thumb, how to decide variance:
        - if it "outputs" elements => COVARIANT (out)
        - if it "consumes" elements => CONTRAVARIANT (in)
        - otherwise, INVARIANT (no modifier)
     */

    /*
        Exercise: add variance modifiers
     */

    class RandomGenerator<out A>
    class MyOption<out A> // holds at most one item
    class JSONSerializer<in A> // turns values of type A into JSONs
    interface MyFunction<in A, out B> // takes a value of type A and returns a B

    /*
        Exercise
        1. add variance modifiers where appropriate
        2. EmptyList should be empty regardless of the type - can you make it an object?
        3. add an "add" method to the generic list type
            fun add(element: A): LList<A>
     */
    abstract class LList<out A> { // "produces" elements, I also want to "consume" by adding to the list
        abstract fun head(): A // first item in the list
        abstract fun tail(): LList<A> // the rest of the list without the head
        //abstract fun add(element: @UnsafeVariance A): LList<A> = TODO()
    }

    fun <B, A:B> LList<A>.add(elem: B): LList<B> =
        Cons(elem, this)

    data object EmptyList: LList<Nothing>() { // is a subtype of ALL POSSIBLE LISTS!
        override fun head(): Nothing = throw NoSuchElementException()
        override fun tail(): LList<Nothing> = throw NoSuchElementException()
    }

    data class Cons<out A>(val h: A, val t: LList<A>): LList<A>() {
        override fun head(): A = h

        override fun tail(): LList<A> = t

    }

    val myLlPets = EmptyList
    val myStrings = EmptyList

    @JvmStatic
    fun main(args: Array<String>) {
        val d =  Dog("v")
    }
}