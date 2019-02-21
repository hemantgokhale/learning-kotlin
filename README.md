For a description what this code does, see the comment at the top of the file src/main/kotlin/analyzer/DataAnalyzer.kt

Here are the instructions for getting, compiling, and running this program. 
They assume that a recent version of java is installed and on the path on your machine.

Clone the project:
```
git clone https://github.com/hemantgokhale/learning-kotlin.git
```
The code is located in src/main/kotlin/analyzer

To build the project run the following command. If you don't have gradle installed, 
this command will first install gradle and then build the project. The first build will be slow.
Subsequent builds will be get faster.:
```
cd learning-kotlin
./gradlew build
```

Run the code with this command:
```
java -jar build/libs/learning-kotlin-1.0-SNAPSHOT.jar
```
