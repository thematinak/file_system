package com.example.rest.files.fileRest.service;

public interface MapSupport {

    <FROM, TO> TO map(FROM f, Class<TO> clazz);
}
