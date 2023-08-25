package org.yugoccp.sksamples;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.orchestration.ContextVariables;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.textcompletion.CompletionSKFunction;

import java.util.Objects;

public class CelebrityStatementSkill {
    private final CompletionSKFunction myFunction;
    private final Kernel myKernel;
    private String contextHistory = "";

    public CelebrityStatementSkill(OpenAIAsyncClient client) {
        this.myKernel = getKernel(client);
        this.myFunction = buildFunction(myKernel);
    }

    private Kernel getKernel(OpenAIAsyncClient client) {
        return SKBuilders.kernel()
                .withDefaultAIService(SKBuilders.chatCompletion()
                        .setModelId("gpt-4")
                        .withOpenAIClient(client)
                        .build())
                .build();
    }

    public String run(String inputText) {
        var newContext = ContextVariables.builder()
                .withVariable("history", contextHistory)
                .withVariable("input", inputText)
                .build();

        var skContext = myKernel.runAsync(newContext, myFunction);
        var result = Objects.requireNonNull(skContext.block()).getResult();

        contextHistory =  contextHistory + String.format("\\nUser: %s\\nChatBot: %s\\n", inputText, result);

        return result;
    }

    private static CompletionSKFunction buildFunction(Kernel kernel) {
        String semanticFunctionInline = """
            A Celebrity Statement strictly follow the structure: I help [people] to do [this] so they can have/become [that]
            
            Where:
            - [people] = is the specific people ou want to meet, people that you want to become friends, associate with, work together, or do significant things together
            - [this] = it's all the things that you do well, and that you LOVE to do, or that you would like to become, and that can help [people].
            - [that] = it's what [people] strongly want to become or what [people] strongly want to have?
            
            ChatBot is an expert in identifying all the Celebrity Statement components ([people], [this] and [that]), 
            and can have a consistent conversation with User to ask relevant questions to collect information from User
            to generate a meaningful Celebrity Statement.
             
            When ChatBot have enough information to create a Celebrity Statement incorporating related topics to [this], and using visceral verbs to [that], give the result as an answer 
         
            {{$history}}
            User: {{$input}}
            ChatBot: """;

        var promptConfig = new PromptTemplateConfig(
                new PromptTemplateConfig.CompletionConfigBuilder()
                        .maxTokens(1000)
                        .temperature(0.8)
                        .topP(1)
                        .build());

        return SKBuilders
                .completionFunctions()
                .withKernel(kernel)
                .setPromptTemplateConfig(promptConfig)
                .setPromptTemplate(semanticFunctionInline)
                .build();
    }
}
