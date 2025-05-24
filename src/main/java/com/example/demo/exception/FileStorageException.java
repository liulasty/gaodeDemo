package com.example.demo.exception;

import java.io.IOException;

/**
 * @author lz
 */
public class FileStorageException extends Throwable {

    private String message;
    private IOException exception;

    @Override
    public String getMessage() {
        return message;
    }

    public FileStorageException(String s){
        this.message = s;
    }
    public FileStorageException(String s, IOException ex) {
        this.message = s;
        this.exception = ex;

    }
}