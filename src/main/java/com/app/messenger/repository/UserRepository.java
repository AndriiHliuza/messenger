package com.app.messenger.repository;

import com.app.messenger.repository.model.Role;
import com.app.messenger.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUniqueName(String uniqueName);
    Optional<User> findByRoleAndUsername(Role role, String username);
    Optional<User> findByRoleAndUniqueName(Role role, String uniqueName);
    boolean existsByUsername(String username);
    boolean existsByUniqueName(String uniqueName);
    List<User> findAllByUniqueNameStartingWithAndRole(String uniqueNamePrefix, Role role);
}
