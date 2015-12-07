package common.interfaces.api

import com.fasterxml.jackson.databind.JsonNode

trait IResponse {
    def toJson(): JsonNode
}
