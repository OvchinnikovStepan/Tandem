package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.api.DirectorySearchApi;
import com.tandem.interest_service.api.mapper.DirectorySearchApiMapper;
import com.tandem.interest_service.api.model.response.GroupSearchResponseJson;
import com.tandem.interest_service.api.model.response.UserSearchResponseJson;
import com.tandem.interest_service.service.DirectoryService;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DirectorySearchApiImpl implements DirectorySearchApi {

    private final DirectoryService directoryService;

    @Override
    public ResponseEntity<List<UserSearchResponseJson>> searchUsers(String search, int limit) {
        List<UserSearchResponse> hits = directoryService.searchUsersByNameFragment(search, limit);

        List<UserSearchResponseJson> response = hits.stream()
                .map(DirectorySearchApiMapper::toUserSearchJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GroupSearchResponseJson>> searchGroups(String search, int limit) {
        List<GroupSearchResponse> hits = directoryService.searchGroupsByNameFragment(search, limit);

        List<GroupSearchResponseJson> response = hits.stream()
                .map(DirectorySearchApiMapper::toGroupSearchJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
