package org.rootservices.otter.translator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.rootservices.otter.translator.exception.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTranslator<T> {
    private ObjectMapper objectMapper;

    private static final String DUPLICATE_NAME = "key";
    private static final Pattern DUPLICATE_KEY_PATTERN = Pattern.compile("Duplicate field \'(?<" + DUPLICATE_NAME + ">\\w+)\'");
    private static final String DUPLICATE_KEY_MSG = "The key '%s' was duplicated";
    private static final String UNKNOWN_KEY_MSG = "The key '%s' was not expected";
    private static final String INVALID_VALUE_MSG = "The key '%s' had an invalid value";
    private static final String INVALID_PAYLOAD_MSG = "The payload couldn't be parsed";
    private static final String TO_JSON_MSG = "Could not create JSON";

    public JsonTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Translates json merge T.
     * @param json
     * @param clazz
     * @return and instance of T
     * @throws InvalidPayloadException unpredicted error occurred
     * @throws DuplicateKeyException a key was repeated
     * @throws UnknownKeyException a key was not expected
     * @throws InvalidValueException key value was incorrect for it's type
     */
    @SuppressWarnings("unchecked")
    public T from(BufferedReader json, Class clazz) throws InvalidPayloadException, DuplicateKeyException, UnknownKeyException, InvalidValueException {
        T entity = null;

        try {
            entity = (T) objectMapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            handleJsonParseException(e);
        } catch (UnrecognizedPropertyException e) {
            String msg = String.format(UNKNOWN_KEY_MSG, e.getPropertyName());
            throw new UnknownKeyException(msg, e, e.getPropertyName());
        } catch (InvalidFormatException e) {
            String key = e.getPath().get(0).getFieldName();
            String msg = String.format(INVALID_VALUE_MSG, key);
            throw new InvalidValueException(msg, e, key);
        } catch (JsonMappingException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        } catch (IOException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        }
        return entity;
    }


    public T from(BufferedReader json, TypeReference typeReference) throws InvalidPayloadException, DuplicateKeyException, InvalidValueException {
        T entity = null;

        try {
            entity = (T) objectMapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            handleJsonParseException(e);
        } catch (InvalidFormatException e) {
            String key = e.getPath().get(0).getFieldName();
            String msg = String.format(INVALID_VALUE_MSG, key);
            throw new InvalidValueException(msg, e, key);
        } catch (JsonMappingException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        } catch (IOException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        }
        return entity;
    }

    public String to(Object object) throws ToJsonException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ToJsonException(TO_JSON_MSG, e);
        }
    }

    protected void handleJsonParseException(JsonParseException jpe) throws DuplicateKeyException, InvalidPayloadException {

        Optional<String> duplicateKey = getJsonParseExceptionDuplicateKey(jpe);
        if (duplicateKey.isPresent()) {
            String msg = String.format(DUPLICATE_KEY_MSG, duplicateKey.get());
            throw new DuplicateKeyException(msg, jpe, duplicateKey.get());
        }
        throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, jpe);
    }

    protected Optional<String> getJsonParseExceptionDuplicateKey(JsonParseException e) {
        Optional<String> key = Optional.empty();
        Matcher m = DUPLICATE_KEY_PATTERN.matcher(e.getOriginalMessage());
        if (m.matches()) {
            key = Optional.of(m.group(DUPLICATE_NAME));
        }
        return key;
    }
}
