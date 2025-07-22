package com.example.demo.menu.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchMenuRequest {

    private Integer menuLevel;
    private Long parentId;
}

