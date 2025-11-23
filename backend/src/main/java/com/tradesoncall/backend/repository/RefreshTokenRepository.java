package com.tradesoncall.backend.repository;

import com.tradesoncall.backend.model.entity.RefreshToken;
import com.tradesoncall.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Boolean existsByToken(String token);
    Optional<RefreshToken> findByToken(String token);
    Boolean existsByUser(User user);
    Boolean deleteByUser(User user);

    Boolean deleteByToken(String token);
}
