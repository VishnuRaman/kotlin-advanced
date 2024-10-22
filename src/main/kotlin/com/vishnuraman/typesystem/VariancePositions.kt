package com.vishnuraman.typesystem


/*
    Rule of thumb: how to determine variance
    - if the type produces (outputs) values of type A, then covariant
    - if the type consumes (accepts) values of type A, then contravariant
    - if neither, then invariant

    trait List<out A> // covariant
    trait Semigroup<A> // invariant
    trait Vet<in A> // contravariant
    trait Set<in A> // contravariant
    trait Option[+A] // covariant
    trait Functor<F[_], out A> // covariant
    trait Monad<M[_], out A> // covariant
    trait Applicative<F[_], out A> // covariant
    trait Foldable<F[_], out A> // covariant
    trait Traversable<F[_], out A> // covariant
    trait Filterable<F[_], out A> // covariant
    trait SemigroupK[F[_], out A] // invariant
 */
object VariancePositions {

    abstract class Animal
    class Dog: Animal()
    class Cat: Animal()
    class Crocodile: Animal()

    // out = Covariant, in = Contravariant, nothing = Invariant

//    THIS IS ILLEGAL
//    class Vet<in T> (val favAnimal: A) {
//        fun treat(animal: T) {
//            println("Treat $animal")
//        }
//    }

    /*
        assume that this was legal
        class Vet<in A> (val favAnimal : A)

        val garfield = Cat()
        val lassie = Dog()
        val theVet: Vet<Animal> = Vet<Animal>(garfield)
        // contravariance
        val dogVet: Vet<Dog> = theVet // Vet<supertype of Dog>
        val favAnimal = dogVet.favAnimal // guaranteed to be a Dog, is actually a Cat

        types of properties (val or var) are in "out" (aka covariant) position
        "in" types cannot be placed in "out" positions
     */

    // class MutableContainer<out A>(var contents: A) // var properties are ALSO in contravariant ("in") position

    /*
        class Liquid
        class Water: Liquid()
        class Gasoline: Liquid()

        val container: MutableContainer<Liquid> = MutableContainer<Water>(Water())
        container.contents = Gasoline() // guarantee that I can write any Liquid inside, but have to keep it to Water

        types of vars specifically are in "in" position (aka contravariant)
        => must be INVARIANT
     */



    /*
        class LList<out A> {
          ILLEGAL
            fun add(a: A): LList<A> = TODO()
        }
        val myList: LList<Animal> = LList<Dog>()
        val newList = myList.add(Crocodile()) // guaranteed to be able to add any animal, BUT have to guarantee just Dogs

        method argument types are in "in" position (aka contravariant)
        => cannot use covariant types in method args
     */


    /*  assume this compiles:
        class Vet<in A> {
            fun rescueAnimal(): A = TODO() // return type is in "out" position (aka covariant)
        }

        val myVet: Vet<Animal> = object: Vet<Animal>() {
            override fun rescueAnimal(): Animal = Cat()
        }

        val dogVet: Vet<Dog> = myVet // legal because of contravariance
        val rescueDog = dogVet.rescueAnimal() // guaranteed to return a Dog, but is actually a Cat

        method return types are in "out" (aka covariant) position
     */

    /*
        solve variance positions problems
     */
    // 1 - consume elements in a covariant type
    abstract class LList<out A>
    data object EmptyLList: LList<Nothing>()
    data class Cons<out A> (val head: A, val tail: LList<A>): LList<A>()

    // how do we add an element?
    // [lassie, hachi, laika].add(togo) => List<Dog>
    // [lassie, hachi, laika].add(garfield) => [lassie, hachi, laika, garfield] => List<Animal>
    // [lassie, hachi, laika].add(45) => [lassie, hachi, laika, 45] => List<Any>
    // solution = widening the type
    // SOLUTION = widening the type
    fun<B, A: B> LList<A>.add(elem: B): LList<B> = Cons(elem, this)

    // 2 - return elements in a contravariant type
    // solution = narrowing the type
    //fun<A, B: A> LList<B>.rescueAnimal(): A = TODO()

    abstract class Vehicle
    open class Car: Vehicle()
    class Supercar: Car()

    class Garage<in A> {
        fun <B:A>repair(elem: B): B = elem
    }



    @JvmStatic
    fun main(args: Array<String>) {
        val myList: LList<Dog> = EmptyLList
        val dogs = myList.add(Dog()) // List<Dog>
        val animals = dogs.add(Cat()) // List<Animal>

        val repairShop : Garage<Car> = Garage<Vehicle>() // contravariance
        val myBeatUpVW = Car()
        val damagedFerrari = Supercar()

        val freshCar = repairShop.repair(myBeatUpVW) // Car
        val freshFerrari = repairShop.repair(damagedFerrari) // SuperCar

    }
}