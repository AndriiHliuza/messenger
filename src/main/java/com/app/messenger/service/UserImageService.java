package com.app.messenger.service;

import com.app.messenger.controller.dto.UserImageDto;

public interface UserImageService {
    UserImageDto getUserImage(String uniqueName) throws Exception;
}
