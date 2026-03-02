package com.tandem.interest_service.service;

import com.tandem.interest_service.service.model.TagRequest;
import com.tandem.interest_service.service.model.TagResponse;

import java.util.List;
import java.util.UUID;

public interface TagService {
    TagResponse createTag(String name); // Создает новый тег
    void deleteTag(UUID id); // Удаляет тег по ID
    TagResponse getTag(UUID id); // Получает тег по ID
    List<TagResponse> getAllTags(); // Получает все теги
    List<TagResponse> getDefaultTags(); // Получает дефолтные теги (у которых поле imageUrl не null)
    TagResponse updateTag(UUID id, TagRequest request); // Обновляет тег по ID
    boolean existsByName(String name); // Проверяет существование тега по имени
    List<TagResponse> searchTagsByPrefix(String prefix, int limit); // Поиск тегов по префиксу
}
