package com.github.skyg0d.skydrinksapi.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileResponse {

    @Schema(description = "Nome do arquivo enviado", example = "blood_mary.png")
    private String fileName;

    @Schema(description = "URI para download do arquivo", example = "blood_mary.png")
    private String fileDownloadUri;

    @Schema(description = "Tipo do arquivo enviado", example = "http://localhost:{port}{context}{controller_path}/blood_mary.png")
    private String fileType;

    @Schema(description = "Tamanho do arquivo enviado", example = "67108")
    private long size;

}
