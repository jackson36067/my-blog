package com.jackson.service;

import com.jackson.entity.Result;

public interface UserBlockService {
    Result doBlock(Long userBlockId);

    Result unBlock(Long userBlockId);

    Result getBlockList();
}
