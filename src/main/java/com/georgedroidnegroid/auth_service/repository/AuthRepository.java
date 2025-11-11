package com.georgedroidnegroid.auth_service.repository;

import com.georgedroidnegroid.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
