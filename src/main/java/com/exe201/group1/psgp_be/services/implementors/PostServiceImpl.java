package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateCommentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.*;
import com.exe201.group1.psgp_be.repositories.*;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.PostService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepo postRepo;
    private final AccountRepo accountRepo;
    private final JWTService jwtService;
    private final TagRepo tagRepo;
    private final CommentRepo commentRepo;
    private final ProductRepo productRepo;

    @Override
    public ResponseEntity<ResponseObject> createPost(CreateOrUpdatePostRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        User seller = account.getUser();

        Integer productId = request.getProductId();
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Product not found with id: " + productId, null);
        }

        Post post = postRepo.save(Post.builder()
                .seller(seller)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .product(product)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        createOrUpdatePost(request, post);

        return ResponseEntity.ok(new ResponseObject("Post created successfully", post));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPosts() {
        List<Post> posts = postRepo.findAll();
        return ResponseEntity.ok(new ResponseObject("Posts fetched successfully", posts));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPost(Integer id) {
        Post post = postRepo.findById(id).orElse(null);
        if (post == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Post not found with id: " + id, null);
        }
        return ResponseEntity.ok(new ResponseObject("Post fetched successfully", post));
    }

    @Override
    public ResponseEntity<ResponseObject> updatePost(Integer id, CreateOrUpdatePostRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        Post post = postRepo.findById(id).orElse(null);
        if (post == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Post not found with id: " + id, null);
        }

        if (!post.getSeller().getId().equals(account.getUser().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You do not have permission to update this post", null);
        }

        Integer productId = request.getProductId();
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Product not found with id: " + productId, null);
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setStatus(request.getStatus());
        post.setProduct(product);
        post.setUpdatedAt(LocalDateTime.now());

        createOrUpdatePost(request, post);

        return ResponseEntity.ok(new ResponseObject("Post updated successfully", post));
    }

    @Override
    public ResponseEntity<ResponseObject> deletePost(Integer id, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        Post post = postRepo.findById(id).orElse(null);
        if (post == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Post not found with id: " + id, null);
        }

        if (!post.getSeller().getId().equals(account.getUser().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You do not have permission to delete this post", null);
        }

        postRepo.delete(post);

        return ResponseEntity.ok(new ResponseObject("Post deleted successfully", null));
    }

    @Override
    public ResponseEntity<ResponseObject> createPostComment(Integer postId, CreateOrUpdateCommentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        User buyer = account.getUser();

        String content = request.getContent();

        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Post not found with id: " + postId, null);
        }

        Comment comment = Comment.builder()
                .post(post)
                .buyer(buyer)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepo.save(comment);

        return ResponseEntity.ok(new ResponseObject("Comment added successfully", comment));
    }

    @Override
    public ResponseEntity<ResponseObject> updatePostComment(Integer commentId, CreateOrUpdateCommentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        String content = request.getContent();

        Comment comment = commentRepo.findById(commentId).orElse(null);
        if (comment == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Comment not found with id: " + commentId, null);
        }

        if (!comment.getBuyer().getId().equals(account.getUser().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You do not have permission to update this comment", null);
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepo.save(comment);
        return ResponseEntity.ok(new ResponseObject("Comment updated successfully", comment));
    }

    @Override
    public ResponseEntity<ResponseObject> deletePostComment(Integer commentId, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }

        Comment comment = commentRepo.findById(commentId).orElse(null);
        if (comment == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Comment not found with id: " + commentId, null);
        }

        if (!comment.getBuyer().getId().equals(account.getUser().getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "You do not have permission to delete this comment", null);
        }

        commentRepo.delete(comment);
        return ResponseEntity.ok(new ResponseObject("Comment deleted successfully", null));
    }

    private void createOrUpdatePost(CreateOrUpdatePostRequest request, Post post) {
        List<PostImage> images = request.getPostImages()
                .stream()
                .map(img ->
                        PostImage.builder()
                                .post(post)
                                .name(img.getName())
                                .link(img.getLink())
                                .build()
                ).toList();
        post.setPostImageList(images);

        List<PostTag> tags = request.getTagIds()
                .stream()
                .map(tagId -> {
                    Tag tag = tagRepo.findById(tagId).orElse(null);
                    if (tag == null) {
                        throw new IllegalArgumentException("Tag not found with id: " + tagId);
                    }
                    return PostTag.builder().post(post).tag(tag).build();
                })
                .toList();
        post.setPostTagList(tags);

        postRepo.save(post);
    }
}
