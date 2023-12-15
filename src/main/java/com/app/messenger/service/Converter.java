package com.app.messenger.service;

public interface Converter<DTO, ENTITY> {
    DTO toDto(ENTITY entity) throws Exception;
    ENTITY toEntity(DTO dto) throws Exception;
}
