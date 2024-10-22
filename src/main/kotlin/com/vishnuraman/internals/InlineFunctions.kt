package com.vishnuraman.internals

object InlineFunctions {

    // ecommerce platform
    data class Product(val name: String, var price: Double)

//    fun applyDiscount(products: List<Product>, discountPercentage: Double, operation: (Product) -> Unit) {
//        for (product in products) {
//            product.price *= (1 - discountPercentage / 100)
//            operation(product)
//        }
//    }

    // or

    fun  List<Product>.applyDiscount(discountPercentage: Double, operation: (Product) -> Unit) {
        for (product in this) {
            product.price *= (1 - discountPercentage / 100)
            operation(product)
        }
    }

    inline fun  List<Product>.applyDiscountFast(discountPercentage: Double, operation: (Product) -> Unit) {
        for (product in this) {
            product.price *= (1 - discountPercentage / 100)
            operation(product)
        }
    }

    fun demoDiscounts() {
        val products = listOf(
            Product("Laptop Pro", 1000.0),
            Product("Phone 25 BIG", 500.0),
            Product("Tablet 17 thin", 300.0)
        )

        println("Applying 10% discount")

        products.applyDiscountFast(10.0) { product ->
            println("Discounted price of ${product.name} is ${product.price}")
        }
        // the inline call is rewritten to:
        /*
            for (product in products) {
                product.price *= (1 - 10.0 / 100)
                println("Discounted price of ${product.name} is ${product.price}")
            }
         */

    }

    fun demoPerf() {
        val products = listOf(
            Product("Laptop Pro", 1000.0),
            Product("Phone 25 BIG", 500.0),
            Product("Tablet 17 thin", 300.0)
        )

        var productsReduced = 0

        val startNonInline = System.nanoTime()
        repeat(1000) {
            products.applyDiscount(10.0) { product ->
                //println("Discounted price of ${product.name} is ${product.price}")
                productsReduced += 1
            }
        }

        val durationNonInline = System.nanoTime() - startNonInline

        productsReduced = 0

        val startInline = System.nanoTime()

        repeat(1000) {
            products.applyDiscountFast(10.0) { product ->
                //println("Discounted price of ${product.name} is ${product.price}")
                productsReduced += 1
            }
        }

        val durationInline = System.nanoTime() - startInline

        println("Times")
        println("Non-inline: $durationNonInline")
        println("Inline: $durationInline")
    }

    /*
        1. code rewritten by the compiler with no overhead (function calls, lambda instantiations)
        2. potential performance benefits, best if the functions are small and repeat in your codebase
     */

    // sometimes useful NOT to inline some lambdas
    inline fun performOperation( noinline storeOperation: () -> Unit, executeOperation: () -> Unit) {
        // store noinline lambdas as regular values
        GlobalStore.store(storeOperation)

        // execute the other
        executeOperation()

    }

    object GlobalStore {
        private var storedOperation: (() -> Unit)? = null

        fun store(op: () -> Unit) {
            storedOperation = op
        }

        fun executeStored() {
            storedOperation?.invoke()
        }
    }

    fun demoNoInline() {
        performOperation(
            storeOperation = { println("This op should be called later.") },
            executeOperation = { println("This should be called immediately.") }
        )

        // later
        GlobalStore.executeStored()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        demoNoInline()
    }
}