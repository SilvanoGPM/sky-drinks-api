package com.github.skyg0d.skydrinksapi.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApplicationUserParameters {

    private String name;

    private String role;

    private String createdAt;

    private String createdInDateOrAfter;

    private String createdInDateOrBefore;

}
