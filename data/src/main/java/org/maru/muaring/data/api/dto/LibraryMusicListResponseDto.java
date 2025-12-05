package org.maru.muaring.data.api.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class LibraryMusicListResponseDto {
    public int totalMusicCount;
    public List<LibraryMusicListRequestDto> musicList;
}
