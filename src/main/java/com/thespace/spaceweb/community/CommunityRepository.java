package com.thespace.spaceweb.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    @Query("SELECT c.id FROM Community c WHERE UPPER(c.name) = :name")
    Long findCommunityIdByNameIgnoreCase(@Param("name") String name);
}
