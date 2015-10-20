package interfaces.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface IResponse {
    JsonNode toJson();
}
