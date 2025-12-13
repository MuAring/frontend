package org.maru.muaring.data.api.dto;

import java.util.List;

public class LibraryDeleteRequest {
    private List<Long> libraryIds;

    public LibraryDeleteRequest(List<Long> libraryIds) {
        this.libraryIds = libraryIds;
    }
}
