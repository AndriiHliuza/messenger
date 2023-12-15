package com.app.messenger.service;

public interface Modifier<DTO, ENTITY> {
    ENTITY modify(DTO dto, ENTITY entity) throws Exception;
}
