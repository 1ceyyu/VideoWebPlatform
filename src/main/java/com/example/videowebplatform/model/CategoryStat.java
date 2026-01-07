package com.example.videowebplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryStat extends Category {
    private int totalClicks; // 额外增加的统计字段
}