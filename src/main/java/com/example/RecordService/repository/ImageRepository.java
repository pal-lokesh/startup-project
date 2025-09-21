package com.example.RecordService.repository;

import com.example.RecordService.model.Image;
import java.util.List;

public interface ImageRepository {
    Image save(Image image);
    Image findByImageId(String imageId);
    List<Image> findByThemeId(String themeId);
    List<Image> findByPrimary(boolean isPrimary);
    List<Image> findAll();
    boolean existsByImageId(String imageId);
    Image update(Image image);
    boolean delete(String imageId);
    long count();
}
