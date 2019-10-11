package org.rootservices.otter.translator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.rootservices.otter.translator.exception.*;

import java.io.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * There should be a instance of this class per LegacyRestResource.
 *
 * @param <T> the type to be marshalled to/from json.
 */
public class JsonTranslator<T> {
    private ObjectReader objectReader;
    private ObjectWriter objectWriter;

    // For Specific Exceptions.
    private static final String DUPLICATE_NAME = "key";
    private static final Pattern DUPLICATE_KEY_PATTERN = Pattern.compile("Duplicate field \'(?<" + DUPLICATE_NAME + ">\\w+)\'");
    private static final String DUPLICATE_KEY_MSG = "The key '%s' was duplicated";
    private static final String UNKNOWN_KEY_MSG = "The key '%s' was not expected";
    private static final String INVALID_VALUE_MSG = "The key '%s' had an invalid value";
    private static final String INVALID_PAYLOAD_MSG = "The payload couldn't be parsed";
    private static final String TO_JSON_MSG = "Could not create JSON";

    // For DeserializationException
    private static final String DUPLICATE_KEY_GENERIC_MSG = "Duplicate Key";
    private static final String INVALID_VALUE_GENERIC_MSG = "Invalid Value";
    private static final String UNKNOWN_KEY_GENERIC_MSG = "Unknown Key";
    private static final String INVALID_PAYLOAD_GENERIC_MSG = "Invalid Payload";


    public JsonTranslator(ObjectReader objectReader, ObjectWriter objectWriter, Class<T> type) {
        this.objectReader = objectReader;
        this.objectWriter = objectWriter;
    }

    public T from(byte[] json) throws DeserializationException {
        T entity;

        try{
            entity = fromWithSpecificCause(json);
        } catch (DuplicateKeyException e) {
            throw new DeserializationException(DUPLICATE_KEY_GENERIC_MSG, e.getKey(), null, Reason.DUPLICATE_KEY, e);
        } catch (InvalidValueException e) {
            Optional<String> value = Optional.empty();
            if (e.getValue() != null) {
                value = Optional.of(e.getValue());
            }
            throw new DeserializationException(INVALID_VALUE_GENERIC_MSG, e.getKey(), value, Reason.INVALID_VALUE, e);
        } catch (UnknownKeyException e) {
            throw new DeserializationException(UNKNOWN_KEY_GENERIC_MSG, e.getKey(), null, Reason.UNKNOWN_KEY, e);
        } catch (InvalidPayloadException e) {
            throw new DeserializationException(INVALID_PAYLOAD_GENERIC_MSG, Reason.INVALID_PAYLOAD, e);
        }
        return entity;
    }
    /**
     * Translates json from T. If an issue occurs it throws a specific cause about what happened?
     *
     * @param json json to marshal
     * @return an instance of T
     * @throws InvalidPayloadException unpredicted error occurred
     * @throws DuplicateKeyException a key was repeated
     * @throws UnknownKeyException a key was not expected
     * @throws InvalidValueException key value was incorrect for it's type
     */
    public T fromWithSpecificCause(byte[] json) throws InvalidPayloadException, DuplicateKeyException, UnknownKeyException, InvalidValueException {
        T entity = null;

        try {
            entity = objectReader.readValue(json);
        } catch (JsonParseException e) {
            handleJsonParseException(e);
        } catch (UnrecognizedPropertyException e) {
            String msg = String.format(UNKNOWN_KEY_MSG, e.getPropertyName());
            throw new UnknownKeyException(msg, e, e.getPropertyName());
        } catch (InvalidFormatException e) {
            String key = e.getPath().get(0).getFieldName();
            String msg = String.format(INVALID_VALUE_MSG, key);
            String value = null;
            if (e.getValue() != null) {
                value = e.getValue().toString();
            }
            throw new InvalidValueException(msg, e, key, value);
        } catch (JsonMappingException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        } catch (IOException e) {
            throw new InvalidPayloadException(INVALID_PAYLOAD_MSG, e);
        }
        return entity;
    }

    public byte[] to(T object) throws ToJsonException {
        byte[] out;

        try {
            out = objectWriter.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new ToJsonException(TO_JSON_MSG, e);
        }
        return out;
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
