package com.dbexp.db_experiment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.dbexp.db_experiment.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT * FROM ForumComment WHERE comment_id = :id")
    Optional<Comment> findById(long id);

    @Query("SELECT * FROM ForumComment WHERE post_id = :postId")
    List<Comment> findByPostId(Long postId);

    @Query("SELECT * FROM ForumComment WHERE user_id = :userId")
    List<Comment> findByUserId(Long userId);

    @Query("SELECT * FROM ForumComment WHERE parent_comment_id = :parentCommentId")
    List<Comment> findByParentCommentId(Long parentCommentId);

    @Query("SELECT * FROM ForumComment WHERE post_id = :postId AND parent_comment_id IS NULL")
    List<Comment> findByPostIdAndParentCommentIdIsNull(Long postId);

    @Modifying
    @Query("UPDATE ForumComment SET comment_content = :commentContent WHERE comment_id = :commentId")
    int updateComment(Long commentId, String commentContent);

    @Modifying
    @Query("DELETE FROM ForumComment WHERE comment_id = :commentId")
    int deleteByCommentId(Long commentId);
}