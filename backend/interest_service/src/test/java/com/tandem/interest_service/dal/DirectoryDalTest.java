package com.tandem.interest_service.dal;

import com.tandem.interest_service.dao.DirectoryDao;
import com.tandem.interest_service.dao.model.GroupDirectoryEntity;
import com.tandem.interest_service.dao.model.UserDirectoryEntity;
import com.tandem.interest_service.dal.impl.DirectoryDalImpl;
import com.tandem.interest_service.service.model.response.GroupSearchResponse;
import com.tandem.interest_service.service.model.response.UserSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryDalTest {

    @Mock
    private DirectoryDao directoryDao;

    private DirectoryDal directoryDal;

    private UUID userId;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        directoryDal = new DirectoryDalImpl(directoryDao);
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
    }

    @Test
    void upsertUser_delegatesToDao() {
        directoryDal.upsertUser(userId, "Alice");

        verify(directoryDao).upsertUser(userId, "Alice");
    }

    @Test
    void upsertGroup_delegatesToDao() {
        directoryDal.upsertGroup(groupId, "Readers");

        verify(directoryDao).upsertGroup(groupId, "Readers");
    }

    @Test
    void searchUsersByNameFragment_mapsEntities() {
        List<UserDirectoryEntity> entities = Arrays.asList(
                UserDirectoryEntity.builder().userId(userId).displayName("Alice Bee").build(),
                UserDirectoryEntity.builder().userId(UUID.randomUUID()).displayName("Malice").build()
        );
        when(directoryDao.searchUsersByNameFragment("ali", 10)).thenReturn(entities);

        List<UserSearchResponse> result = directoryDal.searchUsersByNameFragment("ali", 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getDisplayName()).isEqualTo("Alice Bee");
        verify(directoryDao).searchUsersByNameFragment("ali", 10);
    }

    @Test
    void searchGroupsByNameFragment_returnsEmptyWhenDaoReturnsEmpty() {
        when(directoryDao.searchGroupsByNameFragment("zzz", 10)).thenReturn(Collections.emptyList());

        List<GroupSearchResponse> result = directoryDal.searchGroupsByNameFragment("zzz", 10);

        assertThat(result).isEmpty();
    }
}
