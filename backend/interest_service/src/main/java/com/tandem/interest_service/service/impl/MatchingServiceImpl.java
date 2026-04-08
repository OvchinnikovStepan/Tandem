package com.tandem.interest_service.service.impl;

import com.tandem.interest_service.dal.UserInterestDal;
import com.tandem.interest_service.service.MatchingService;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;
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
    private final UserInterestDal userInterestDal;

    @Override
    public List<UserMatchingResponse> getMatchingUsers(
            UUID userId, int limit, int minMatchCount){

        Map<UUID, Integer> matchingData = userInterestDal.getUsersWithCommonTagsCount(userId, minMatchCount);

        if (matchingData.isEmpty()) {
            return List.of();
        }

        int countCurrentUser = userInterestDal.getCountUserInterests(userId);

        PriorityQueue<Map.Entry<UUID, Double>> heap = buildMatchingHeap(matchingData, countCurrentUser, limit);

        return buildResponsesFromHeap(heap, userId);
    }

    /**
     * Создает очередь с приоритетом по проценту совпадений
     */
    private PriorityQueue<Map.Entry<UUID, Double>> buildMatchingHeap(
            Map<UUID, Integer> matchingData,
            int countCurrentUser,
            int limit) {

        PriorityQueue<Map.Entry<UUID, Double>> heap = new PriorityQueue<>(
                limit + 1,
                (a, b) -> Double.compare(a.getValue(), b.getValue()));

        for (UUID otherUserId : matchingData.keySet()) {
            int commonCount = matchingData.get(otherUserId);
            int countOtherUser = userInterestDal.getCountUserInterests(otherUserId);

            // Коэффициент Жаккара:
            // % = (общие теги × 100) / (теги_текущего + теги_другого - общие)
            double matchScore = (double) (commonCount * 100) / (countCurrentUser + countOtherUser - commonCount);

            Map.Entry<UUID, Double> entry = new AbstractMap.SimpleEntry<>(otherUserId, matchScore);
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
    private List<UserMatchingResponse> buildResponsesFromHeap(
            PriorityQueue<Map.Entry<UUID, Double>> heap,
            UUID userId) {

        List<UserMatchingResponse> usersMatchingResponses = new ArrayList<>();

        for (Map.Entry<UUID, Double> entry : heap) {
            UUID otherUserId = entry.getKey();
            Double matchScore = entry.getValue();

            List<UUID> commonTagIds = userInterestDal.getCommonTagIds(userId, otherUserId);
            List<String> matchingInterests = new ArrayList<>();

            for (UUID tagId : commonTagIds) {
                String name = userInterestDal.findTagById(tagId).getName();
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
}
