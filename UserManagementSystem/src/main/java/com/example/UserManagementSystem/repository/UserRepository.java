package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT u FROM User u WHERE u.active = true AND " +
           "(:firstName IS NULL OR :firstName = '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR :lastName = '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    Page<User> searchNames(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        Pageable pageable
    );
}
