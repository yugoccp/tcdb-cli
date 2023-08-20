package org.yugoccp.sksamples;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.connectors.ai.openai.util.OpenAIClientProvider;
import com.microsoft.semantickernel.exceptions.ConfigurationException;
import com.microsoft.semantickernel.orchestration.SKContext;
import picocli.CommandLine;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@CommandLine.Command(name = "tcdb", version = "TCDB CLI 1.0", mixinStandardHelpOptions = true) // |1|
public class TcdbCli implements Runnable {
    private final Logger log = Logger.getLogger(TcdbCli.class.getName());
    @CommandLine.Option(names = {"-c", "--configFile"}, description = "OpenAI configuration file location")
    private File configFile;
    @CommandLine.Parameters(index = "0", description = "User input text")
    private String inputText;

    public static OpenAIAsyncClient getClient(List<File> configFileList) {
        try {
            return OpenAIClientProvider.getWithAdditional(configFileList);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        var csSkill = new CelebrityStatementSkill(getClient(List.of(configFile)));
        Mono<SKContext> result = csSkill.run(inputText);
        log.info(Objects.requireNonNull(result.block()).getResult());
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TcdbCli()).execute(args);
        System.exit(exitCode);
    }
}
