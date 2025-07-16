package com.fullness.keihiseisan.model.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 役職ValueObject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class role {
    /** 役職ID定数 */
    public static final int EMPLOYEE = 1; // 一般
    public static final int MANAGER = 2;  // 課長
    public static final int DIRECTOR = 3; // 部長
    public static final int SYSTEM_ADMIN = 9; // システム管理者
    /** 役職ID */
    private int roleId;
    /** 役職名 */
    private String roleName;
}