package interfaces.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface IRequest {
    Object fromJson(JsonNode jsonNode);
    boolean isValid();
}
