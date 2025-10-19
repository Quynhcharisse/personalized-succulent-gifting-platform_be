package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateCommentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdatePostRequest;
import com.exe201.group1.psgp_be.dto.requests.CreatePostImageRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.*;
import com.exe201.group1.psgp_be.repositories.*;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.PostService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepo postRepo;
    private final AccountRepo accountRepo;
    private final JWTService jwtService;
    private final TagRepo tagRepo;
    private final CommentRepo commentRepo;
    private final ProductRepo productRepo;
    private final PostImageRepo postImageRepo;
    private final PostTagRepo postTagRepo;

    @Override
    @Transactional
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

        Post post = Post.builder()
                .seller(seller)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .product(product)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createOrUpdatePost(request, post);

        return ResponseEntity.ok(new ResponseObject("Post created successfully", EntityResponseBuilder.buildPostsResponse(post)));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPosts() {
        List<Post> posts = postRepo.findAllByStatus(Status.PUBLISHED).stream().peek(post -> {
            List<Comment> comments = post.getComments().stream().filter(c -> Status.VISIBLE.equals(c.getStatus())).toList();
            post.setComments(comments);
        }).toList();
        return ResponseEntity.ok(new ResponseObject("Posts fetched successfully", EntityResponseBuilder.buildPostsResponse(posts)));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPostsBySeller(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Không tìm thấy tài khoản", null);
        }
        Integer sellerId = account.getUser().getId();
        List<Post> posts = postRepo.findAllBySellerId(sellerId).stream().peek(post -> {
            List<Comment> comments = post.getComments().stream().filter(c -> Status.VISIBLE.equals(c.getStatus())).toList();
            post.setComments(comments);
        }).toList();
        return ResponseEntity.ok(new ResponseObject("Posts fetched successfully", EntityResponseBuilder.buildPostsResponse(posts)));
    }

    @Override
    public ResponseEntity<ResponseObject> viewPost(Integer id) {
        Post post = postRepo.findById(id).orElse(null);
        if (post == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Post not found with id: " + id, null);
        }
        List<Comment> comments = post.getComments().stream().filter(c -> Status.VISIBLE.equals(c.getStatus())).toList();
        post.setComments(comments);
        return ResponseEntity.ok(new ResponseObject("Post fetched successfully", EntityResponseBuilder.buildPostsResponse(post)));
    }

    @Override
    @Transactional
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

        return ResponseEntity.ok(new ResponseObject("Post updated successfully", EntityResponseBuilder.buildPostsResponse(post)));
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
                .status(Status.VISIBLE)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepo.save(comment);

        return ResponseEntity.ok(new ResponseObject("Comment added successfully", EntityResponseBuilder.buildCommentsResponse(comment)));
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
        comment.setStatus(request.getStatus());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepo.save(comment);
        return ResponseEntity.ok(new ResponseObject("Comment updated successfully", EntityResponseBuilder.buildCommentsResponse(comment)));
    }

    @Override
    public ResponseEntity<ResponseObject> getPostTags() {
        List<Tag> tags = tagRepo.findAll();
        return ResponseEntity.ok(new ResponseObject("Tags fetched successfully", tags));
    }

    private void createOrUpdatePost(CreateOrUpdatePostRequest request, Post post) {
        // Handle images: reconcile existing images with incoming ones to avoid duplicates
        List<CreatePostImageRequest> postImages = request.getPostImages();
        if (postImages != null) {
            // get current images (mutable copy)
            List<PostImage> existingImages = post.getPostImageList() != null
                    ? new ArrayList<>(post.getPostImageList())
                    : new ArrayList<>();

            Set<Integer> keptIds = new HashSet<>();
            List<PostImage> resultingImages = new ArrayList<>();

            for (CreatePostImageRequest imgReq : postImages) {
                if (imgReq.getId() != null) {
                    Optional<PostImage> opt = postImageRepo.findById(imgReq.getId());
                    if (opt.isPresent()) {
                        PostImage existing = opt.get();
                        existing.setName(imgReq.getName());
                        existing.setLink(imgReq.getLink());
                        existing.setPost(post);
                        resultingImages.add(existing);
                        keptIds.add(existing.getId());
                        continue;
                    }
                }
                // new image
                PostImage newImg = PostImage.builder()
                        .post(post)
                        .name(imgReq.getName())
                        .link(imgReq.getLink())
                        .build();
                resultingImages.add(newImg);
            }

            // Delete images removed by client
            List<Integer> toDeleteIds = existingImages.stream()
                    .map(PostImage::getId)
                    .filter(Objects::nonNull)
                    .filter(id -> !keptIds.contains(id))
                    .collect(Collectors.toList());
            if (!toDeleteIds.isEmpty()) {
                postImageRepo.deleteAllByIdInBatch(toDeleteIds);
            }

            // persist new/updated images explicitly so they get ids and DB rows before post save
            if (!resultingImages.isEmpty()) {
                postImageRepo.saveAll(resultingImages);
            }

            // set mutable list on post
            post.setPostImageList(new ArrayList<>(resultingImages));
        } else {
            // if client provided null and you want to remove all images:
            // postImageRepo.deleteAllByPostId(post.getId()); // implement in repo if needed
            // post.setPostImageList(new ArrayList<>());
        }

        List<String> tagStrings = request.getTagNames();
        if (tagStrings != null) {
            // Load current PostTag associations from DB (avoid relying on possibly-stale post.getPostTagList())
            List<PostTag> existingPostTags = post.getId() != null
                    ? postTagRepo.findAllByPostId(post.getId())
                    : new ArrayList<>();

            Map<String, PostTag> existingByName = existingPostTags.stream()
                    .filter(Objects::nonNull)
                    .filter(pt -> pt.getTag() != null && pt.getTag().getName() != null)
                    .collect(Collectors.toMap(pt -> pt.getTag().getName(), pt -> pt, (a, b) -> a));

            List<PostTag> resulting = new ArrayList<>();
            for (String tagName : tagStrings) {
                Tag tag;
                if (tagRepo.existsByName(tagName)) {
                    tag = tagRepo.findByName(tagName);
                } else {
                    tag = Tag.builder().name(tagName).build();
                    tagRepo.save(tag);
                }

                if (existingByName.containsKey(tagName)) {
                    PostTag existingPT = existingByName.remove(tagName);
                    existingPT.setPost(post);
                    existingPT.setTag(tag);
                    resulting.add(existingPT);
                } else {
                    PostTag newPT = PostTag.builder().post(post).tag(tag).build();
                    resulting.add(newPT);
                }
            }

            // Delete associations removed by client
            if (!existingByName.isEmpty()) {
                List<PostTag> toDelete = new ArrayList<>(existingByName.values());
                postTagRepo.deleteAllInBatch(toDelete);
            }

            // Persist new/updated PostTag rows explicitly
            if (!resulting.isEmpty()) {
                postTagRepo.saveAll(resulting);
            }

            post.setPostTagList(resulting);
        }

        // ensure DB update immediately
        postRepo.saveAndFlush(post);
    }
}
