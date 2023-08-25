package org.yugoccp.sksamples;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TcdbCli {

    public static OpenAIAsyncClient getClient(List<File> configFileList) {
        try {
            return OpenAIClientProvider.getWithAdditional(configFileList);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        var configFile = new File(args[0]);

        var client = getClient(List.of(configFile));
        var csFunction = new CelebrityStatementSkill(client);

        var reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to TCDB Celebrity Statement builder. Let's start by introducing yourself (type 'exit' to quit):");
        var inputText = reader.readLine();

        while (!inputText.equals("exit")) {
            var result = csFunction.run(inputText);
            System.out.println(result);

            System.out.println("Reply (type 'exit' to quit): ");
            inputText = reader.readLine();
        }
    }
}
