package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<ResponseCommentDto> toCollectionResponseCommentDto(Collection<Comment> comments) {
        if (comments == null) {
            return Collections.EMPTY_LIST;
        }
        return comments.stream()
                .map(CommentMapper::toResponseCommentDto)
                .collect(Collectors.toList());
    }
}
