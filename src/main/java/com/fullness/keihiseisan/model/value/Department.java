package com.fullness.keihiseisan.model.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部署ValueObject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    /** 部署ID */
    private int deptId;
    /** 部署名 */
    private String deptName;
}