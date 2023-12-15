package com.app.messenger.service;

import com.app.messenger.repository.model.ImageType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ImageTypeConverter implements AttributeConverter<ImageType, String> {
    @Override
    public String convertToDatabaseColumn(ImageType attribute) {
        return attribute.name().toLowerCase();
    }

    @Override
    public ImageType convertToEntityAttribute(String dbData) {
        return ImageType.valueOf(dbData.toUpperCase());
    }
}
