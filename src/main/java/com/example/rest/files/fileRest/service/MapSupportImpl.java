package com.example.rest.files.fileRest.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service("MapSupport")
public class MapSupportImpl implements MapSupport {
    ObjectMapper mapper;

    public MapSupportImpl() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <FROM, TO> TO map(FROM f, Class<TO> clazz) {
        return mapper.convertValue(f, clazz);
    }
}
