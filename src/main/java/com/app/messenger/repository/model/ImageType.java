package com.app.messenger.repository.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ImageType {
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    BMP("bmp"),
    WEBP("webp"),
    SVG("svg+xml");
    private final String value;

    ImageType(String value) {
        this.value = "image/" + value;
    }

    public static ImageType getImageTypeByValue(String value) {
        return Arrays.stream(values())
                .filter(imageType -> imageType.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
