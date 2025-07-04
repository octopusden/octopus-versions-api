package org.octopusden.releng.versions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentVersionFormatTest {

    @Test
    void testCreate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ComponentVersionFormat componentVersionFormat = ComponentVersionFormat.create("1", "2", "3", "4", "5", true);
        String componentVersionFormatString = objectMapper.writeValueAsString(componentVersionFormat);
        assertEquals(componentVersionFormat, objectMapper.readValue(componentVersionFormatString, ComponentVersionFormat.class));

        componentVersionFormat = ComponentVersionFormat.create("1", "2", "3", "4", null, false);
        componentVersionFormatString = objectMapper.writeValueAsString(componentVersionFormat);
        assertEquals(componentVersionFormat, objectMapper.readValue(componentVersionFormatString, ComponentVersionFormat.class));

    }
}