package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.MatchingDal;
import com.tandem.interest_service.service.MatchingService;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
import com.tandem.interest_service.service.model.response.GroupMatchingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {
    private final MatchingDal matchingDal ;

    @Override
    public List<UserMatchingResponse> getMatchingUsers(
            UUID userId, int limit, int minMatchCount){

        Map<UUID, Integer> matchingData = matchingDal.getUsersWithCommonTagsCount(userId, minMatchCount);

        if (matchingData.isEmpty()) {
            return List.of();
        }

        int countCurrentUser = matchingDal.getCountUserInterests(userId);

        PriorityQueue<Map.Entry<UUID, Double>> heap = buildMatchingHeap(matchingData, countCurrentUser, limit, true);

        return buildUserResponsesFromHeap(heap, userId);
    }

    @Override
    public List<GroupMatchingResponse> getMatchingGroups(UUID userId, int limit, int minMatchCount) {
        Map<UUID, Integer> matchingData = matchingDal.getGroupsWithCommonTagsCount(userId, minMatchCount);
        if (matchingData.isEmpty()) return List.of();

        int countCurrentUser = matchingDal.getCountUserInterests(userId);
        // Передаем false, чтобы указать, что мы мэтчим с группами
        PriorityQueue<Map.Entry<UUID, Double>> heap = buildMatchingHeap(matchingData, countCurrentUser, limit, false);

        return buildGroupResponsesFromHeap(heap, userId);
    }

    /**
     * Создает очередь с приоритетом по проценту совпадений
     */
    private PriorityQueue<Map.Entry<UUID, Double>> buildMatchingHeap(
            Map<UUID, Integer> matchingData,
            int countCurrentUser,
            int limit,
            boolean isUserMatch) {

        PriorityQueue<Map.Entry<UUID, Double>> heap = new PriorityQueue<>(
                limit + 1,
                (a, b) -> Double.compare(a.getValue(), b.getValue()));

        for (UUID otherId : matchingData.keySet()) {
            int commonCount = matchingData.get(otherId);
            int countOther;
            if (isUserMatch) {
                countOther = matchingDal.getCountUserInterests(otherId);
            }
            else{
                countOther = matchingDal.getCountGroupTags(otherId);
            }
            // Коэффициент Жаккара:
            // % = (общие теги × 100) / (теги_текущего + теги_другого - общие)
            double matchScore = (double) (commonCount * 100) / (countCurrentUser + countOther - commonCount);

            Map.Entry<UUID, Double> entry = new AbstractMap.SimpleEntry<>(otherId, matchScore);
            heap.offer(entry);

            // Если превысили лимит - удаляем элемент с наименьшим matchScore
            if (heap.size() > limit) {
                heap.poll();
            }
        }

        return heap;
    }

    /**
     * Преобразует очередь в response
     */
    private List<UserMatchingResponse> buildUserResponsesFromHeap(
            PriorityQueue<Map.Entry<UUID, Double>> heap,
            UUID userId) {

        List<UserMatchingResponse> usersMatchingResponses = new ArrayList<>();

        for (Map.Entry<UUID, Double> entry : heap) {
            UUID otherUserId = entry.getKey();
            Double matchScore = entry.getValue();

            List<UUID> commonTagIds = matchingDal.getCommonTagIds(userId, otherUserId);
            List<String> matchingInterests = new ArrayList<>();

            for (UUID tagId : commonTagIds) {
                String name = matchingDal.findTagById(tagId).getName();
                matchingInterests.add(name);
            }

            UserMatchingResponse userMatchingResponse =
                    UserMatchingResponse.builder()
                            .userId(otherUserId)
                            .matchingInterests(matchingInterests)
                            .matchScore(matchScore)
                            .build();

            usersMatchingResponses.add(userMatchingResponse);
        }

        return usersMatchingResponses;
    }

    /**
     * Преобразует очередь в response для групп
     */
    private List<GroupMatchingResponse> buildGroupResponsesFromHeap(
            PriorityQueue<Map.Entry<UUID, Double>> heap,
            UUID userId) {

        List<GroupMatchingResponse> groupMatchingResponses = new ArrayList<>();

        for (Map.Entry<UUID, Double> entry : heap) {
            UUID groupId = entry.getKey();
            Double matchScore = entry.getValue();

            // Используем метод для поиска общих тегов между юзером и группой
            List<UUID> commonTagIds = matchingDal.getCommonTagIdsUserGroup(userId, groupId);
            List<String> matchingInterests = new ArrayList<>();

            for (UUID tagId : commonTagIds) {
                String name = matchingDal.findTagById(tagId).getName();
                matchingInterests.add(name);
            }

            GroupMatchingResponse groupMatchingResponse =
                    GroupMatchingResponse.builder()
                            .groupId(groupId)
                            .matchingInterests(matchingInterests)
                            .matchScore(matchScore)
                            .build();

            groupMatchingResponses.add(groupMatchingResponse);
        }

        // Сортируем итоговый список по убыванию процента совпадения (от лучших к худшим)
        groupMatchingResponses.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return groupMatchingResponses;
    }
}
