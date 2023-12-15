package com.app.messenger.service;

import com.app.messenger.controller.dto.UserImageDto;
import com.app.messenger.repository.model.ImageType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class MultipartFileToUserImageDtoConverter implements Function<MultipartFile, UserImageDto> {
    @Override
    public UserImageDto apply(MultipartFile multipartFile) {
        UserImageDto userImageDto = null;
        List<String> allowedContentTypes = Arrays.stream(ImageType.values()).map(ImageType::getValue).toList();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String imageType = multipartFile.getContentType();
            if (allowedContentTypes.contains(imageType)) {
                try {
                    byte[] data = multipartFile.getBytes();
                    if (data.length != 0) {
                        userImageDto = UserImageDto
                                .builder()
                                .name(multipartFile.getOriginalFilename())
                                .type(multipartFile.getContentType())
                                .data(multipartFile.getBytes())
                                .build();
                    }
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return userImageDto;
    }
}
