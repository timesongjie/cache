package com.bbkmobile.iqoo.cache.redis.serializer;

/**
 * Generic exception indicating a serialization/deserialization error.
 *
 * @author Costin Leau
 */
public class SerializationException extends Exception {

    /**
     * Constructs a new
     * <code>SerializationException</code> instance.
     *
     * @param msg
     * @param cause
     */
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new
     * <code>SerializationException</code> instance.
     *
     * @param msg
     */
    public SerializationException(String msg) {
        super(msg);
    }
}
