package com.app.messenger.repository.model;

public enum Permission {

    // operations with users
    READ_USER,
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,

    // operations with admin

    READ_ADMIN,
    CREATE_ADMIN,
    UPDATE_ADMIN,
    DELETE_ADMIN,

    // operations with root

    READ_ROOT,
    UPDATE_ROOT
}
