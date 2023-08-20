package org.yugoccp.sksamples;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.orchestration.SKContext;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;
import reactor.core.publisher.Mono;

public class CelebrityStatementSkill {
    private final OpenAIAsyncClient client;

    public CelebrityStatementSkill(OpenAIAsyncClient client) {
        this.client = client;
    }

    private Kernel getKernel(OpenAIAsyncClient client) {
        return SKBuilders.kernel()
                .withDefaultAIService(SKBuilders.chatCompletion()
                        .setModelId("gpt-4")
                        .withOpenAIClient(client)
                        .build())
                .build();
    }

    public Mono<SKContext> run(String inputText) {
        Kernel kernel = getKernel(client);
        String semanticFunctionInline = """
            Given that:
            - [people] = the specific people ou want to meet and associate with
            - [this] = it's what you do, what you are or want to be an expert at
            - [that] = it's what [people] want. What is that they want more?
            
            A Celebrity Statement strictly follow the structure: I help [people] to do [this] so they can have/become [that]
            
            Incorporate related topics to [this], use visceral verbs to [that], and generate a meaningful Celebrity Statement from the description below:
         
            {{$input}}
            """;

        var promptConfig = new PromptTemplateConfig(
                new PromptTemplateConfig.CompletionConfigBuilder()
                        .maxTokens(100)
                        .temperature(0.6)
                        .topP(1)
                        .build());

        CompletionSKFunction summarizeFunction = SKBuilders
                .completionFunctions()
                .withKernel(kernel)
                .setPromptTemplateConfig(promptConfig)
                .setPromptTemplate(semanticFunctionInline)
                .build();

        return summarizeFunction.invokeAsync(inputText);

    }
}
