package com.dbexp.db_experiment.service;

import java.util.List;
import jakarta.servlet.http.HttpSession;

import com.dbexp.db_experiment.dto.post.CreatePostRequest;
import com.dbexp.db_experiment.dto.post.CreatePostResponse;
import com.dbexp.db_experiment.dto.post.DeletePostResponse;
import com.dbexp.db_experiment.dto.post.EditPostRequest;
import com.dbexp.db_experiment.dto.post.EditPostResponse;
import com.dbexp.db_experiment.dto.post.GetPostByIdRequest;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;

public interface PostService {
    CreatePostResponse createPost(HttpSession session, CreatePostRequest request);

    GetPostByIdResponse getPostById(GetPostByIdRequest request);
    
    List<GetPostByIdResponse> getPostsByCommunityId(Long communityId);

    EditPostResponse editPost(HttpSession session, Long postId, EditPostRequest request);

    DeletePostResponse deletePost(HttpSession session, Long postId);
}