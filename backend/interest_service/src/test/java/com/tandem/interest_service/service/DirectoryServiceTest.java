package com.tandem.interest_service.service;

import com.tandem.interest_service.dal.DirectoryDal;
import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.integration.model.OnboardingCompletedEvent;
import com.tandem.interest_service.service.impl.DirectoryServiceImpl;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    @Mock
    private DirectoryDal directoryDal;

    private DirectoryService directoryService;

    private UUID userId;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        directoryService = new DirectoryServiceImpl(directoryDal);
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
    }

    @Test
    void syncUserFromOnboarding_upsertsWhenUsernamePresent() {
        OnboardingCompletedEvent event = OnboardingCompletedEvent.builder()
                .userId(userId.toString())
                .data(Map.of(
                        "interests", List.of("gaming"),
                        "username", "  Neo "
                ))
                .build();

        directoryService.syncUserFromOnboarding(event);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(directoryDal).upsertUser(eq(userId), nameCaptor.capture());
        assertThat(nameCaptor.getValue()).isEqualTo("Neo");
    }

    @Test
    void syncUserFromOnboarding_skipsUpsertWhenNoUsername() {
        OnboardingCompletedEvent event = OnboardingCompletedEvent.builder()
                .userId(userId.toString())
                .data(Map.of("interests", List.of("gaming")))
                .build();

        directoryService.syncUserFromOnboarding(event);

        verify(directoryDal, never()).upsertUser(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void syncGroupFromEvent_upsertsWhenNamePresent() {
        GroupCreatedEvent event = GroupCreatedEvent.builder()
                .groupId(groupId.toString())
                .groupName(" Science Club ")
                .interestTags("[]")
                .build();

        directoryService.syncGroupFromEvent(event);

        verify(directoryDal).upsertGroup(groupId, "Science Club");
    }

    @Test
    void syncGroupFromEvent_skipsWhenNameBlank() {
        GroupCreatedEvent event = GroupCreatedEvent.builder()
                .groupId(groupId.toString())
                .groupName("   ")
                .interestTags("[]")
                .build();

        directoryService.syncGroupFromEvent(event);

        verify(directoryDal, never()).upsertGroup(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void searchUsers_returnsEmptyWhenSearchBlank() {
        List<UserSearchResponse> result = directoryService.searchUsersByNameFragment("   ", 10);

        assertThat(result).isEmpty();
        verify(directoryDal, never()).searchUsersByNameFragment(org.mockito.ArgumentMatchers.any(), anyInt());
    }

    @Test
    void searchUsers_capsLimitAt50() {
        when(directoryDal.searchUsersByNameFragment(eq("ab"), eq(50)))
                .thenReturn(Collections.emptyList());

        directoryService.searchUsersByNameFragment("ab", 500);

        verify(directoryDal).searchUsersByNameFragment("ab", 50);
    }

    @Test
    void searchGroups_respectsEffectiveLimit() {
        List<GroupSearchResponse> expected = List.of(
                GroupSearchResponse.builder().groupId(groupId).name("Books").build()
        );
        when(directoryDal.searchGroupsByNameFragment("bo", 10)).thenReturn(expected);

        List<GroupSearchResponse> result = directoryService.searchGroupsByNameFragment("bo", 10);

        assertThat(result).isEqualTo(expected);
    }
}
