package com.fullness.keihiseisan.model.value;

import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 経費申請ValueObject
 */
@Data
@NoArgsConstructor
public class ExpenseApplication {
    /** 
     * 基本情報 
     */
    /** 申請ID */
    private int applicationId;
    /** 申請者ID */
    private String applicantUserId;
    /** 申請日 */
    private Date applicationDate;
    /** 勘定科目ID */
    private int accountId;
    /** 支払日 */
    private Date paymentDate;
    /** 支払先 */
    private String payee;
    /** 金額 */
    private int amount;
    /** 内容（詳細） */
    private String description;
    /** 領収書ファイルパス */
    private String receiptPath;
    /** ステータスID */
    private int statusId;

    /**
     * 承認情報
     */
    /** 承認者1（課長）ID */
    private String approver1UserId;
    /** 承認者1承認日時 */
    private Timestamp approval1Date;
    /** 承認者2（部長）ID */
    private String approver2UserId;
    /** 承認者2承認日時 */
    private Timestamp approval2Date;
    /** 却下理由 */
    private String rejectionReason;

    /** 
     * 結合表示用フィールド
     */
    /** 申請者名 */
    private String applicantName;
    /** 勘定科目名 */
    private String accountName;
    /** ステータス名 */
    private String statusName;
    /** 承認者1名 */
    private String approver1Name;
    /** 承認者2名 */
    private String approver2Name;
}