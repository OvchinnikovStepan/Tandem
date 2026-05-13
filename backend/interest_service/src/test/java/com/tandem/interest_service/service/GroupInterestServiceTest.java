package com.tandem.interest_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.interest_service.dal.GroupTagDal;
import com.tandem.interest_service.integration.model.GroupCreatedEvent;
import com.tandem.interest_service.service.exception.GroupInterestNotFoundException;
import com.tandem.interest_service.service.impl.GroupInterestServiceImpl;
import com.tandem.interest_service.service.model.request.GroupInterestRequest;
import com.tandem.interest_service.service.model.response.GroupInterestResponse;
import com.tandem.interest_service.service.model.response.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupInterestServiceTest {

    @Mock
    private GroupTagDal groupTagDal;

    @Mock
    private ObjectMapper objectMapper;

    private GroupInterestService groupInterestService;

    private UUID groupId;
    private UUID tagId;
    private GroupInterestRequest request;
    private GroupInterestResponse response;

    @BeforeEach
    void setUp() {
        groupInterestService = new GroupInterestServiceImpl(groupTagDal, objectMapper);

        groupId = UUID.randomUUID();
        tagId = UUID.randomUUID();

        request = GroupInterestRequest.builder()
                .groupId(groupId)
                .tagId(tagId)
                .build();

        response = GroupInterestResponse.builder()
                .id(UUID.randomUUID())
                .groupId(groupId)
                .tag(TagResponse.builder().id(tagId).name("Java").build())
                .build();
    }

    // addGroupInterest
    @Test
    void addGroupInterest_Success() {
        when(groupTagDal.insert(List.of(request))).thenReturn(List.of(response));

        List<GroupInterestResponse> result = groupInterestService.addGroupInterest(List.of(request));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        verify(groupTagDal).insert(List.of(request));
    }

    @Test
    void addGroupInterest_ReturnsEmptyList_WhenRequestIsEmpty() {
        List<GroupInterestResponse> result = groupInterestService.addGroupInterest(List.of());

        assertThat(result).isEmpty();
        verify(groupTagDal, never()).insert(any());
    }

    @Test
    void addGroupInterest_ThrowsException_WhenIdsAreNull() {
        GroupInterestRequest invalidRequest = GroupInterestRequest.builder().build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupInterestService.addGroupInterest(List.of(invalidRequest)));

        assertThat(exception.getMessage()).contains("cannot be null");
    }

    // removeGroupInterest
    @Test
    void removeGroupInterest_Success() {
        when(groupTagDal.getGroupInterest(groupId, tagId)).thenReturn(response);

        groupInterestService.removeGroupInterest(request);

        verify(groupTagDal).getGroupInterest(groupId, tagId);
        verify(groupTagDal).delete(response.getId());
    }

    @Test
    void removeGroupInterest_ThrowsException_WhenIdsAreNull() {
        GroupInterestRequest invalidRequest = GroupInterestRequest.builder().build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> groupInterestService.removeGroupInterest(invalidRequest));

        assertThat(exception.getMessage()).contains("cannot be null");
    }

    @Test
    void removeGroupInterest_ThrowsNotFoundException_WhenDalThrowsException() {
        when(groupTagDal.getGroupInterest(groupId, tagId)).thenThrow(new RuntimeException("Not found"));

        assertThrows(GroupInterestNotFoundException.class,
                () -> groupInterestService.removeGroupInterest(request));
    }

    // getGroupInterests
    @Test
    void getGroupInterests_Success() {
        when(groupTagDal.getGroupInterests(groupId)).thenReturn(List.of(response));

        List<GroupInterestResponse> result = groupInterestService.getGroupInterests(groupId);

        assertThat(result).hasSize(1);
        verify(groupTagDal).getGroupInterests(groupId);
    }

    @Test
    void getGroupInterests_ThrowsException_WhenGroupIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> groupInterestService.getGroupInterests(null));
    }

    // parseToGroupInterestRequest
    @Test
    void parseToGroupInterestRequest_Success() throws Exception {
        String jsonMessage = "{\"groupId\":\"" + groupId + "\",\"interestTags\":\"[Java, Spring]\"}";

        GroupCreatedEvent event = GroupCreatedEvent.builder()
                .groupId(groupId.toString())
                .interestTags("[Java, Spring]")
                .build();

        TagResponse tagJava = TagResponse.builder().id(tagId).name("Java").build();

        when(objectMapper.readValue(jsonMessage, GroupCreatedEvent.class)).thenReturn(event);
        when(groupTagDal.findTagByName("Java")).thenReturn(tagJava);
        when(groupTagDal.findTagByName("Spring")).thenReturn(null); // Один тег найден, другой нет

        List<GroupInterestRequest> result = groupInterestService.parseToGroupInterestRequest(jsonMessage);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(groupId);
        assertThat(result.get(0).getTagId()).isEqualTo(tagId);
    }

    @Test
    void parseToGroupInterestRequest_ReturnsEmptyList_WhenJsonInvalid() throws Exception {
        String invalidJson = "{bad_json}";

        when(objectMapper.readValue(invalidJson, GroupCreatedEvent.class))
                .thenThrow(new RuntimeException("Parse error"));

        List<GroupInterestRequest> result = groupInterestService.parseToGroupInterestRequest(invalidJson);

        assertThat(result).isEmpty();
    }
}