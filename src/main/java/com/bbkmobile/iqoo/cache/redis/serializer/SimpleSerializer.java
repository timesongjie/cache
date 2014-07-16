package com.bbkmobile.iqoo.cache.redis.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 * @author lqzhai
 */
public class SimpleSerializer<T> implements RedisSerializer<T> {

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OsgiObjectOutputStream objectOutputStream = null;
        try {

            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(getClass().getSimpleName() + " requires a Serializable payload "
                        + "but received an object of type [" + object.getClass().getName() + "]");
            }
            objectOutputStream = new OsgiObjectOutputStream(outputStream);
//            ClassLoader old = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(BundleClassLoader.getBundleClassLoader(this.getClass()));            
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
//            Thread.currentThread().setContextClassLoader(old);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("fail to serialize object:"+object.toString(),e);
        }finally{
            if( null != objectOutputStream){
                try {
                    objectOutputStream.close();
                } catch (IOException ex) {
                    
                }
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);


        try {
//            ClassLoader old = Thread.currentThread().getContextClassLoader();
//            Thread.currentThread().setContextClassLoader(BundleClassLoader.getBundleClassLoader(this.getClass()));
        	ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            T obj = (T) objectInputStream.readObject();
//            Thread.currentThread().setContextClassLoader(old);
            return obj;
        } catch (Exception ex) {
            throw new SerializationException("Failed to deserialize object type", ex);
        }
    }

    @Override
    public String serializeToSting(Object t) throws SerializationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T deserializeFromString(String str) throws SerializationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
