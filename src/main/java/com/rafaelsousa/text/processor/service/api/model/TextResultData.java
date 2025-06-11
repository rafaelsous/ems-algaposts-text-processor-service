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
public class TextResultData {
    private UUID postId;
    private Long wordCount;
    private Double calculatedValue;
}