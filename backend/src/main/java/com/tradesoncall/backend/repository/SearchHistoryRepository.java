package com.tradesoncall.backend.repository;

import com.tradesoncall.backend.model.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, UUID> {

    /**
     * Get user's search history
     */
    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(UUID userId);

    /**
     * Get user's recent searches
     */
    @Query("""
        SELECT sh FROM SearchHistory sh 
        WHERE sh.userId = :userId 
        ORDER BY sh.searchedAt DESC 
        LIMIT :limit
        """)
    List<SearchHistory> findRecentSearches(
            @Param("userId") UUID userId,
            @Param("limit") int limit
    );

    /**
     * Get popular service types
     */
    @Query("""
        SELECT sh.serviceType, COUNT(sh) as count 
        FROM SearchHistory sh 
        WHERE sh.searchedAt > :since
        GROUP BY sh.serviceType 
        ORDER BY count DESC
        """)
    List<Object[]> findPopularServices(@Param("since") LocalDateTime since);
}