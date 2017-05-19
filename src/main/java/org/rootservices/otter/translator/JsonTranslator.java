package org.rootservices.otter.translator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.rootservices.otter.translator.exception.DuplicateKeyException;
import org.rootservices.otter.translator.exception.InvalidPayloadException;
import org.rootservices.otter.translator.exception.InvalidValueException;
import org.rootservices.otter.translator.exception.UnknownKeyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTranslator<T> {
    private ObjectMapper objectMapper;

    private static final String DUPLICATE_NAME = "key";
    private static final Pattern DUPLICATE_KEY_PATTERN = Pattern.compile("Duplicate field \'(?<" + DUPLICATE_NAME + ">\\w+)\'");
    private static final String DUPLICATE_KEY_MSG = "There was a duplicate key in the payload";
    private static final String UNKNOWN_KEY_MSG = "There was a unknown key in the payload";
    private static final String INVALID_VALUE_MSG = "There was a invalid value in the payload";
    private static final String INVALID_PAYLOAD_MSG = "The payload couldn't be parsed";

    public JsonTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Translates json to T.
     * @param json
     * @param clazz
     * @return and instance of T
     * @throws InvalidPayloadException
     * @throws DuplicateKeyException
     * @throws UnknownKeyException
     * @throws InvalidValueException
     */
    @SuppressWarnings("unchecked")
    public T from(BufferedReader json, Class clazz) throws InvalidPayloadException, DuplicateKeyException, UnknownKeyException, InvalidValueException {
        T thing = null;

        try {
            thing = (T) objectMapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            handleJsonParseException(e);
        } catch (UnrecognizedPropertyException e) {
            throw new UnknownKeyException(UNKNOWN_KEY_MSG, e, e.getPropertyName());
        } catch (InvalidFormatException e) {
            throw new InvalidValueException(INVALID_VALUE_MSG, e, e.getPath().get(0).getFieldName());
        } catch (JsonMappingException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        } catch (IOException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        }
        return thing;
    }

    protected void handleJsonParseException(JsonParseException jpe) throws DuplicateKeyException, InvalidPayloadException {

        Optional<String> duplicateKey = getJsonParseExceptionDuplicateKey(jpe);
        if (duplicateKey.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_KEY_MSG, jpe, duplicateKey.get());
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
