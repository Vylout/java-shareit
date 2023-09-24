package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentClientDto;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.user.dto.UserClientDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemClientDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemClientDto itemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }


    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findItemsByText(String text, Integer from, Integer size) {
        if (text.isBlank()) return ResponseEntity.ok().body(List.of());
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "text", text
        );
        return get("/search?from={from}&size={size}&text={text}", null, parameters);
    }


    public ResponseEntity<Object> update(UserClientDto userDto, long userId) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> createComment(CommentClientDto commentDto, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
