package org.maru.muaring.data.api.dto;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private boolean last;

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
