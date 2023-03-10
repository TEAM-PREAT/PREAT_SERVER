package com.andes.preat.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
    Optional<User> findByIdAndStatus(Long userId, UserState state);
    Optional<User> findByNickname(String nickname);
    boolean existsUserByEmailAndStatus(String email, UserState state);
    boolean existsUserByNickname(String nickname);
}
