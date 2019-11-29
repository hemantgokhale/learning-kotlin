package analyzer

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import java.io.File
import kotlin.random.Random

/**
 * This is a simple program I wrote to get to know Kotlin. Here is what it does:
 *
 * Part 1
 * Read a text file to get a list of first names. Read another text file to get a list of last names.
 * Define a Person as a struct with the following fields: a first name, a last name, and an age.
 * Create a person by randomly selecting a first name from the list of first names, a last name from the list of last
 * names, and an age as a random integer between 1 through 100.
 * Write PeopleCount number of such person objects in JSON format to a text file.
 *
 * Part 2
 * Read the list of people and analyze the data to print the following stats:
 * 1. Number of people in the dataset
 * 2. The count of distinct first names
 * 3. The oldest three people
 * 4. Minimum and maximum ages in the dataset
 * 5. The three most popular last names with their frequencies
*/

/**
 * The keyword "data" adds compiler generated implementations of equals(), hashCode(), toString(), componentN(), and copy()
 * The @Serializable annotation makes it possible to serialize/deserialize to/from JSON, CBOR, and Protobuf
 * https://github.com/Kotlin/kotlinx.serialization
 */
@Serializable
data class Person (val firstName: String, val lastName: String, val age: Int)

fun main() {
    try {
        writePeople(getFirstNames(), getLastNames())
        analyze()
    } catch (e: Throwable) {
        println("*** There was an error ***")
        e.printStackTrace()
    }
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
    val minAge = people.minBy { it.age }?.age ?: 0
    val maxAge = people.maxBy { it.age }?.age ?: 0
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
