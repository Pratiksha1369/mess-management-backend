package com.pratiksha.messmanagement.payload;

import java.util.List;

public class PageResponse<T> {

    private List<T> data;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public PageResponse(List<T> data, int currentPage, long totalItems, int totalPages) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }
}