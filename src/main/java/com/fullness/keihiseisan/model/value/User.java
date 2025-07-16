package com.fullness.keihiseisan.model.value;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * ユーザーValueObject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    /** ユーザーID */
    private String userId;
    /** パスワード */
    private String password;
    /** ソルト */
    private String salt;
    /** ユーザー名 */
    private String userName;
    /** 部署ID */
    private int departmentId;
    /** 部署名 */
    private String departmentName;
    /** 役職ID */
    private int roleId;
    /** 役職名 */
    private String roleName;
}