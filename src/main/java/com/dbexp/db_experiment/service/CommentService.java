package com.dbexp.db_experiment.service;

import java.util.List;
import jakarta.servlet.http.HttpSession;

import com.dbexp.db_experiment.dto.comment.CreateCommentRequest;
import com.dbexp.db_experiment.dto.comment.CreateCommentResponse;
import com.dbexp.db_experiment.dto.comment.DeleteCommentResponse;
import com.dbexp.db_experiment.dto.comment.EditCommentRequest;
import com.dbexp.db_experiment.dto.comment.EditCommentResponse;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdRequest;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;

public interface CommentService {
    CreateCommentResponse createUnderPost(HttpSession session, CreateCommentRequest request);

    CreateCommentResponse createUnderComment(HttpSession session, CreateCommentRequest request);

    GetCommentByIdResponse getById(GetCommentByIdRequest request);
    
    List<GetCommentByIdResponse> getCommentsByPostId(Long postId);

    EditCommentResponse edit(HttpSession session, Long commentId, EditCommentRequest request);

    DeleteCommentResponse delete(HttpSession session, Long commentId);
}