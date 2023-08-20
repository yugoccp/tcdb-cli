# tcdb-cli

TCDB CLI is a Command Line to help you generate your Celebrity Statement with the help of AI!

## Requirements
- Java 17
- Maven 3
- OpenAI account

## Usage

1. Create a copy of `config.properties.example` and rename to `config.properties`
2. Fill the `config.properties` file with the OpenAI API token and organization ID
3. Run the following command:
```
mvn clean package
java -cp './target/tcdb-cli-0.0.1.jar' org.yugoccp.sksamples.TcdbCli <INPUT_TEXT> -c ./config.properties
```