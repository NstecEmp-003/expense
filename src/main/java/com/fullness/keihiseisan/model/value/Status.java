package com.fullness.keihiseisan.model.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ステータスValueObject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    /** ステータスID定数 */
    public static final int APPLIED = 1;           // 申請中
    public static final int MANAGER_APPROVED = 2;  // 課長承認済
    public static final int APPROVED_COMPLETED = 3; // 承認完了
    public static final int MANAGER_REJECTED = 8;  // 課長却下
    public static final int DIRECTOR_REJECTED = 9; // 部長却下
    /** ステータスID */
    private int statusId;
    /** ステータス名 */
    private String statusName;
}