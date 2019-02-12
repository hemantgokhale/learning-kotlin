package analyzer

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import java.io.File
import kotlin.random.Random

/**
 * The keyword "data" adds compiler generated implementations of equals(), hashCode(), toString(), componentN(), and copy()
 * The @Serializable annotation makes it possible to serialize/deserialize to/from JSON, CBOR, and Protobuf
 * https://github.com/Kotlin/kotlinx.serialization
 */
@Serializable
data class Person (val firstName: String, val lastName: String, val age: Int)

fun main() {
    writePeople(getFirstNames(), getLastNames())
    analyze()
}

const val PEOPLE_FILE = "data/people.txt"   // The output file
private const val PEOPLE_COUNT = 100       // Number of entries to be generated

fun getFirstNames(): List<String> {
    // useLines makes sure the file is closed, even if there are exceptions
    File("data/firstNames.txt").useLines { lines ->
        return lines
            .map { it.split("\t") }
            .flatMap { sequenceOf(it[1], it[3]) }
            .toList()
    }
}

fun getLastNames(): List<String> {
    File("data/lastNames.txt").useLines { lines ->
        return lines
            .map { it.split(" ") }
            .map { it[0].toLowerCase().capitalize() }
            .toList()
    }
}

fun writePeople(firstNames: List<String>, lastNames: List<String>) {

    File(PEOPLE_FILE).bufferedWriter().use { out ->
        for (i in 1..PEOPLE_COUNT) {
            val firstName = firstNames[Random.nextInt(firstNames.size - 1)]
            val lastName = lastNames[Random.nextInt(lastNames.size -1)]
            val age = (1..100).random()

            out.write(JSON.stringify(Person.serializer(), Person(firstName, lastName, age)))
            out.write(System.lineSeparator())
        }
    }
    println("A dataset with $PEOPLE_COUNT people generated.")
}

fun readPeople(): List<Person> {
    File(PEOPLE_FILE).useLines { lines ->
        return lines.map { JSON.parse(Person.serializer(), it) }.filterNotNull().toList()
    }
}


private fun analyze() {
    val people = readPeople()

    // Distinct first names
    val distinctFirstNameCount = people.distinctBy { it.firstName }.size
    println("\nThere are $distinctFirstNameCount distinct first names.")

    // Min and max ages
    val minAge = people.minBy { it.age }?.age
    val maxAge = people.maxBy { it.age }?.age
    println("Minimum age is $minAge.")
    println("Maximum age is $maxAge.")

    // Sort by age and print the top N
    val topCount = 3
    println("\nTop $topCount oldest people:")
    people.sortedByDescending { it.age }
        .subList(0, topCount)
        .forEach { println(it) }

    // Most popular last names
    println("\nTop $topCount most popular last names with frequency:")
    people.groupBy { it.lastName }
        .mapValues { entry -> entry.value.size }
        .toList()
        .sortedByDescending { it.second }
        .subList(0, topCount)
        .forEach { println(it) }
}
