package com.dbexp.db_experiment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.dbexp.db_experiment.entity.Post;

public interface PostRepository extends CrudRepository<Post, Long> {

    @Query("SELECT * FROM Post WHERE post_id = :id")
    Optional<Post> findById(long id);

    @Query("SELECT * FROM Post WHERE user_id = :userId")
    List<Post> findByUserId(Long userId);

    @Query("SELECT * FROM Post WHERE community_id = :communityId")
    List<Post> findByCommunityId(Long communityId);

    @Modifying
    @Query("UPDATE Post SET post_title = :postTitle, post_content = :postContent WHERE post_id = :postId")
    int updatePost(Long postId, String postTitle, String postContent);

    @Modifying
    @Query("DELETE FROM Post WHERE post_id = :postId")
    int deleteByPostId(Long postId);
}