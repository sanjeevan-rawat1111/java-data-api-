package com.example.datapi.repository;

import com.example.datapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find users by name containing (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by city
    List<User> findByCity(String city);
    
    // Find users by age range
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Custom query to find users by name pattern
    @Query("SELECT u FROM User u WHERE u.name LIKE %:namePattern%")
    List<User> findUsersByNamePattern(@Param("namePattern") String namePattern);
    
    // Custom query to find users by email domain
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findUsersByEmailDomain(@Param("domain") String domain);
    
    // Custom query to get user count by city
    @Query("SELECT u.city, COUNT(u) FROM User u GROUP BY u.city")
    List<Object[]> getUserCountByCity();
    
    // Custom query to find users with specific criteria
    @Query("SELECT u FROM User u WHERE u.name = :name AND u.email = :email")
    Optional<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);
}
