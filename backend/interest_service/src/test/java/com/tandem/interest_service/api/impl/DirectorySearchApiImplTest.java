package com.tandem.interest_service.api.impl;

import com.tandem.interest_service.service.DirectoryService;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectorySearchApiImplTest {

    @Mock
    private DirectoryService directoryService;

    private DirectorySearchApiImpl api;

    private UUID userId;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        api = new DirectorySearchApiImpl(directoryService);
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
    }

    @Test
    void searchUsers_returnsOkAndMapsResults() {
        List<UserSearchResponse> hits = List.of(
                UserSearchResponse.builder().userId(userId).displayName("Anna").build()
        );
        when(directoryService.searchUsersByNameFragment("ann", 5)).thenReturn(hits);

        ResponseEntity<?> response = api.searchUsers("ann", 5);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(directoryService).searchUsersByNameFragment("ann", 5);
    }

    @Test
    void searchGroups_delegatesToService() {
        when(directoryService.searchGroupsByNameFragment("club", 10)).thenReturn(List.of());

        api.searchGroups("club", 10);

        verify(directoryService).searchGroupsByNameFragment("club", 10);
    }
}
