package com.vishnuraman.builderksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class BuilderProcessor(private val generator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        /*
            1 - find all classes with the @Builder annotation
            2 - generate builder pattern out of every such class
         */

        val classes = resolver.getSymbolsWithAnnotation(builderAnnotationName)
            .filterIsInstance<KSClassDeclaration>()

        classes.forEach { cls ->
            if (cls.validate())
                generateBuilderClass(cls)
        }

        // only those symbols that CANNOT be processed at this time
        return emptyList()
    }

    private fun generateBuilderClass(cls: KSClassDeclaration) {
        // com.vishnuraman.data.Person - data class
        // com.vishnuraman.data.PersonBuilder - generated class
        val className = cls.simpleName.asString()
        val packageName = cls.packageName.asString()
        val properties = cls.getAllProperties().toList()
        val originalFiles = listOfNotNull(cls.containingFile).toTypedArray()

        val builderClassName = "${className}Builder"
        val file = generator.createNewFile( // new file where we are going to generate the code
            Dependencies(false, *originalFiles),
            packageName,
            builderClassName,
        )

        file.bufferedWriter().use { writer ->
            // put in some strings
            writer.write("package $packageName\n\n")
            writer.write("class $builderClassName { \n")

            properties.forEach { prop ->
                val propertyName = prop.simpleName.asString()
                val propertyType = prop.type.resolve()

                writer.write("  private var $propertyName: $propertyType? = null\n") // <- add \n newline or it won't compile
                writer.write("  fun $propertyName(value: $propertyType) = apply { this.$propertyName = value } \n")
            }

            /*
                class PersonBuilder {
                    private var name: String? = null
                    private var age: Int? = null

                    fun name(value: String) = apply { this.name = value }
                    fun age(value: Int) = apply { this.age = value }

                    fun build() {
                        return Person(
                            name ?: throw IllegalArgumentException("name must be provided"),
                            age ?: throw IllegalArgumentException("age must be provided")
                        )
                    }
                }
             */
            writer.write("  fun build(): $className { \n")
            writer.write("    return $className(\n")
            properties.forEachIndexed { index, prop ->
                val propName = prop.simpleName.asString()
                writer.write("      $propName = $propName ?: throw IllegalArgumentException(\"$propName must be provided!\")")
                if (index < properties.size - 1)
                    writer.write(",\n")
                else
                    writer.write("\n")
            }
            writer.write("    )\n")
            writer.write("  }\n")
            writer.write("}\n")
        }
    }

    companion object {
        val builderAnnotationName = "com.vishnuraman.builderksp.Builder"
    }
}