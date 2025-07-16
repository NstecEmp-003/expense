package com.fullness.keihiseisan.model.value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 勘定科目ValueObject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    /** 勘定科目ID */
    private int accountId;
    /** 勘定科目名 */
    private String accountName;
}