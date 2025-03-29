package faang.school.postservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    public static String getFieldFromJson(String jsonString, String fieldName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error("Invalid format json");
            return "";
        }
        return rootNode.get(fieldName).asText();
    }
}
