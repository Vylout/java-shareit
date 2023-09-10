package ru.practicum.shareit.utils;

import org.springframework.data.domain.Sort;

public class Constants {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final Sort SORT_BY_START = Sort.by("start");
    public static final Sort SORT_BY_START_DESC = Sort.by("start").descending();
}
