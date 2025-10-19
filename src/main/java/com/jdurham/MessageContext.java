package com.jdurham;

import com.fasterxml.jackson.databind.JsonNode;

public record MessageContext(String src, String dest, JsonNode requestBody) {
}
