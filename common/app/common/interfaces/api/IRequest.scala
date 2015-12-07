package common.interfaces.api

import com.fasterxml.jackson.databind.JsonNode

trait IRequest {
    def fromJson(jsonNode: JsonNode): Object
    def isValid(): Boolean
}
