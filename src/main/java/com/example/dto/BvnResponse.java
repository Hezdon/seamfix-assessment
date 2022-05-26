package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@With
@AllArgsConstructor
public class BvnResponse {
    String message, code, bvn, imageDetail, basicDetail;

}
