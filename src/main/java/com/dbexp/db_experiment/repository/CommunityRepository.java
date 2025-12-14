package com.dbexp.db_experiment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.dbexp.db_experiment.entity.Community;

public interface CommunityRepository extends CrudRepository<Community, Long> {

    @Query("SELECT * FROM Community WHERE community_id = :id")
    Optional<Community> findById(long id);

    @Query("SELECT * FROM Community WHERE community_name = :name")
    Optional<Community> findByName(String name);

    @Modifying
    @Query("UPDATE Community SET community_description = :newDescription WHERE community_id = :communityId")
    int updateDescription(Long communityId, String newDescription);
    
    @Query("SELECT * FROM Community ORDER BY created_at DESC LIMIT 20")
    List<Community> findTop20ByOrderByCreatedAtDesc();
}