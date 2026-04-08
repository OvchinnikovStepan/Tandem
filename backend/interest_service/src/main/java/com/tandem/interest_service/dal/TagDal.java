package com.tandem.interest_service.dal;

import com.tandem.interest_service.service.model.request.TagRequest;
import com.tandem.interest_service.service.model.response.TagResponse;
import java.util.List;
import java.util.UUID;

public interface TagDal {
    TagResponse insert(TagRequest tagRequest);
    void delete(UUID id);
    TagResponse get(UUID id);
    List<TagResponse> getAll();
    List<TagResponse> getDefault();
    TagResponse update(UUID id, TagRequest tagRequest);
    TagResponse getByName(String name);
    List<TagResponse> searchByNamePrefix(String prefix, int limit);
}
