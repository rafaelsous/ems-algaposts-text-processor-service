package com.rafaelsousa.text.processor.service.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostData {
    private UUID id;
    private String title;
    private String body;
    private String author;
    private Long wordCount;
    private Double calculatedValue;
}