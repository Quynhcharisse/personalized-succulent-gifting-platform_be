package com.exe201.group1.psgp_be.utils;

import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Comment;
import com.exe201.group1.psgp_be.models.Notification;
import com.exe201.group1.psgp_be.models.Post;
import com.exe201.group1.psgp_be.models.PostImage;
import com.exe201.group1.psgp_be.models.PostTag;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityResponseBuilder {

    //-------Account---------//

    public static Map<String, Object> buildAccountResponse(Account account) {
        List<String> keys = List.of(
                "id", "email", "role",
                "registerDate", "active", "user"
        );
        List<Object> values = List.of(
                account.getId(),
                Objects.requireNonNullElse(account.getEmail(), ""),
                Objects.requireNonNullElse(account.getRole(), ""),
                Objects.requireNonNullElse(account.getRegisterDate(), ""),
                account.isActive(),
                Objects.requireNonNullElse(buildUserResponse(account.getUser()), "")
        );
        return MapUtils.build(keys, values);
    }

    //-------Buyer---------//
    public static Map<String, Object> buildUserResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> keys = List.of(
                "id", "name", "phone", "gender",
                "address", "avatarUrl", "fengShui", "zodiac"
        );
        List<Object> values = List.of(
                user.getId(),
                Objects.requireNonNullElse(user.getName(), ""),
                Objects.requireNonNullElse(user.getPhone(), ""),
                Objects.requireNonNullElse(user.getGender(), ""),
                Objects.requireNonNullElse(user.getAddress(), ""),
                Objects.requireNonNullElse(user.getAvatarUrl(), ""),
                Objects.requireNonNullElse(user.getFengShui(), ""),
                Objects.requireNonNullElse(user.getZodiac(), "")
        );

        return MapUtils.build(keys, values);
    }

    //-------Supplier---------//
    public static Map<String, Object> buildSupplierResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        List<String> keys = List.of(
                "id", "supplierName", "contactPerson", "phone",
                "email", "address", "description", "status",
                "createdAt", "updatedAt"
        );

        List<Object> values = List.of(
                supplier.getId(),
                Objects.requireNonNullElse(supplier.getName(), ""),
                Objects.requireNonNullElse(supplier.getContactPerson(), ""),
                Objects.requireNonNullElse(supplier.getPhone(), ""),
                Objects.requireNonNullElse(supplier.getEmail(), ""),
                Objects.requireNonNullElse(supplier.getAddress(), ""),
                Objects.requireNonNullElse(supplier.getDescription(), ""),
                Objects.requireNonNullElse(supplier.getStatus().getValue(), ""),
                Objects.requireNonNullElse(supplier.getCreatedAt(), null),
                Objects.requireNonNullElse(supplier.getUpdatedAt(), null)
        );
        return MapUtils.build(keys, values);
    }

    public static Map<String, Object> buildNotificationsResponse(List<Notification> notifications) {
        return Map.of(
                "count", notifications.size(),
                "notifications", notifications.stream().map(notification -> Map.of(
                        "id", notification.getId(),
                        "message", notification.getMessage(),
                        "isRead", notification.isRead(),
                        "createdAt", notification.getCreatedAt(),
                        "accountId", notification.getAccount().getId()
                )).toList()
        );
    }

    public static Map<String, Object> buildPostsResponse(List<Post> posts) {
        return Map.of(
                "count", posts.size(),
                "posts", posts.stream().map(EntityResponseBuilder::buildPostsResponse).toList()
        );
    }

    public static Map<String, Object> buildPostsResponse(Post post) {
        Map<String, Object> response = new HashMap<>(Map.of(
                "id", post.getId(),
                "title", post.getTitle(),
                "description", post.getDescription(),
                "status", post.getStatus(),
                "createdAt", post.getCreatedAt(),
                "updatedAt", Objects.requireNonNullElse(post.getUpdatedAt(), ""),
                "images", buildPostImageResponse(post.getPostImageList()),
                "tags", buildPostTagResponse(post.getPostTagList()),
                "sellerId", post.getSeller().getId(),
                "productId", post.getProduct().getId()
        ));
        response.put("comments", buildCommentsResponse(post.getComments()));
        return response;
    }

    public static Map<String, Object> buildPostImageResponse(List<PostImage> postImages) {
        return Map.of(
                "count", postImages.size(),
                "postImages", postImages.stream().map(postImage -> Map.of(
                        "id", postImage.getId(),
                        "name", postImage.getName(),
                        "link", postImage.getLink(),
                        "postId", postImage.getPost().getId()
                )).toList()
        );
    }

    public static Map<String, Object> buildPostTagResponse(List<PostTag> postTags) {
        return Map.of(
                "count", postTags.size(),
                "postTags", postTags.stream().map(postTag -> Map.of(
                        "id", postTag.getId(),
                        "tagName", postTag.getTag().getName(),
                        "postId", postTag.getPost().getId()
                )).toList()
        );
    }

    public static Map<String, Object> buildCommentsResponse(List<Comment> comments) {
        return Map.of(
                "count", comments.size(),
                "comments", comments.stream().map(EntityResponseBuilder::buildCommentsResponse
                ).toList()
        );
    }

    public static Map<String, Object> buildCommentsResponse(Comment comment) {
        if (comment == null) {
            return Map.of();
        }
        return Map.of(
                "id", comment.getId(),
                "content", comment.getContent(),
                "createdAt", comment.getCreatedAt(),
                "updatedAt", Objects.requireNonNullElse(comment.getUpdatedAt(), ""),
                "postId", comment.getPost().getId(),
                "buyerName", comment.getBuyer().getName(),
                "accountId", comment.getBuyer().getAccount().getId()
        );
    }

}
