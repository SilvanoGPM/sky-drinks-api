package com.github.skyg0d.skydrinksapi.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

}
