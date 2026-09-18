package at.htlleonding.pepper.service;

import at.htlleonding.pepper.model.ChatGPTRequest;
import at.htlleonding.pepper.model.ChatGPTResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class SmallTalkService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    public String chat(String input) {
        String apiKey = System.getenv("CHATGPT_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new ServiceUnavailableException("CHATGPT_API_KEY is not configured");
        }

        ChatGPTRequest request = new ChatGPTRequest(
                MODEL,
                List.of(new ChatGPTRequest.Message("user", input))
        );

        try (Response response = ClientBuilder.newClient()
                .target(API_URL)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .post(Entity.json(request))) {

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                ChatGPTResponse chatResponse = response.readEntity(ChatGPTResponse.class);
                if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
                    return "";
                }
                return chatResponse.getChoices().getFirst().getMessage().getContent();
            }
            throw new ServiceUnavailableException("OpenAI request failed with status " + response.getStatus());
        } catch (ProcessingException e) {
            throw new ServiceUnavailableException("OpenAI request failed");
        }
    }
}
