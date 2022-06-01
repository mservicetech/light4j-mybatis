package com.mservicetech.mybatis.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public interface InputStreamSource {

    InputStream getInputStream() throws IOException;


    boolean exists();


    default boolean isReadable() {
        return exists();
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }


    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;


    long contentLength() throws IOException;


    long lastModified() throws IOException;


    InputStreamSource createRelative(String relativePath) throws IOException;


    String getFilename();

    String getDescription();

}
