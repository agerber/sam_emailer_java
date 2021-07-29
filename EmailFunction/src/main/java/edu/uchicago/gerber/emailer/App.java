package edu.uchicago.gerber.emailer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Message message = null;
        try {
             message = objectMapper.readValue(input.getBody(), Message.class);
        } catch (JsonProcessingException e) {
            return response
                    .withBody(String.format("{%s}", e.getMessage()))
                    .withStatusCode(400);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("subject", message.getSubject());
        jsonObject.put("body", message.getBody());
        jsonObject.put("email", message.getEmail());


        return response
                .withStatusCode(200)
                .withBody(jsonObject.toString());


    }


}
