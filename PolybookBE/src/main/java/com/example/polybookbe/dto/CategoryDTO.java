package com.example.polybookbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryDTO {
    private Integer id;
    private String tenDanhMuc;
    private String url;
    private Integer parentId;
    private List<CategoryDTO> children = new ArrayList<>();
}

