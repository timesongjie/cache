package com.bbkmobile.iqoo.cache.redis.serializer;

import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JavaType;

/**
 * {@link RedisSerializer} that can read and write JSON using <a
 * href="http://jackson.codehaus.org/">Jackson's</a> {@link ObjectMapper}.
 *
 * <p>This converter can be used to bind to typed beans, or untyped
 * {@link java.util.HashMap HashMap} instances.
 *
 * <b>Note:</b>Null objects are serialized as empty arrays and vice versa.
 *
 * @author Costin Leau
 */
public class JacksonJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final JavaType javaType;
    private ObjectMapper objectMapper = new ObjectMapper();

    public JacksonJsonRedisSerializer(Class<T> type) {
        this.javaType = TypeFactory.defaultInstance().constructType(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return (T) this.objectMapper.readValue(bytes, 0, bytes.length, javaType);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }
        try {
            return this.objectMapper.writeValueAsBytes(t);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets the {@code ObjectMapper} for this view. If not set, a default
     * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take
     * further control of the JSON serialization process. For example, an
     * extended {@link org.codehaus.jackson.map.SerializerFactory} can be
     * configured that provides custom serializers for specific types. The other
     * option for refining the serialization process is to use Jackson's
     * provided annotations on the types to be serialized, in which case a
     * custom-configured ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Returns the Jackson {@link JavaType} for the specific class.
     *
     * <p>Default implementation returns
     * {@link TypeFactory#type(java.lang.reflect.Type)}, but this can be
     * overridden in subclasses, to allow for custom generic collection
     * handling. For instance:
     * <pre class="code">
     * protected JavaType getJavaType(Class&lt;?&gt; clazz) { if
     * (List.class.isAssignableFrom(clazz)) { return
     * TypeFactory.collectionType(ArrayList.class, MyBean.class); } else {
     * return super.getJavaType(clazz); } }
     * </pre>
     *
     * @param clazz the class to return the java type for
     * @return the java type
     */
    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }

    @Override
    public String serializeToSting(T t) throws SerializationException {
        if (t == null) {
            return null;
        }
        try {
            return this.objectMapper.writeValueAsString(t);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public T deserializeFromString(String str) throws SerializationException {
        if (null == str || "".equals(str.trim())) {
            return null;
        }
        try {
            return (T)this.objectMapper.readValue(str, javaType);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
}