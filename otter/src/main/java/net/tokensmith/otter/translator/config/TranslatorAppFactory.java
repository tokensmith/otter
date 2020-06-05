package net.tokensmith.otter.translator.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.tokensmith.otter.translator.JsonTranslator;

import java.util.Objects;

public class TranslatorAppFactory {

    private static ObjectMapper objectMapper;
    private static ObjectReader objectReader;
    private static ObjectWriter objectWriter;

    /**
     * Make a JsonTranslator
     * It must be used exclusively for {@code Class<T> clazz}
     *
     * @param clazz Class to be serialized
     * @param <T> Type to be serialized
     * @return instance of a JsonTranslator intended for T
     */
    public <T> JsonTranslator<T> jsonTranslator(Class<T> clazz) {
        return new JsonTranslator<T>(
                objectReader().forType(clazz), objectWriter(), clazz
        );
    }

    public ObjectReader objectReader() {
        if (Objects.isNull(objectReader)) {
            objectReader = objectMapper().reader();
        }
        return objectReader;
    }

    public ObjectWriter objectWriter() {
        if (Objects.isNull(objectWriter)) {
            objectWriter = objectMapper().writer();
        }
        return objectWriter;
    }

    public ObjectMapper objectMapper() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper()
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategy.SNAKE_CASE
                    )
                    .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }
}
