package com.bbkmobile.iqoo.cache.redis.serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author lqzhai
 */
public class OsgiObjectOutputStream extends ObjectOutputStream{
    public OsgiObjectOutputStream()throws IOException, SecurityException {
        super();
    }
    
    public OsgiObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
}
