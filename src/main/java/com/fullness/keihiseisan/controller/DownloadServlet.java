package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.util.FileUploadUtil;
import com.fullness.keihiseisan.model.value.ExpenseApplication;

/**
 * 領収書ダウンロードのコントローラークラス
 * 領収書ダウンロードの表示を行う
 */
@WebServlet("/expense/download")
public class DownloadServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // パラメーター取得
            String idStr = request.getParameter("id");
            String fileNameFromParam = request.getParameter("filename");
            // パラメーター検証
            if ((idStr == null || idStr.isEmpty()) && (fileNameFromParam == null || fileNameFromParam.isEmpty())) {
                //throw new BusinessException("申請IDまたはファイル名が指定されていません。");
            }
            // パラメーター検証（ダウンロードかサムネイル表示かで処理を分けるため）
            String viewMode = request.getParameter("view");
            boolean isThumbnail = "thumbnail".equals(viewMode);
            // ファイルダウンロード処理
            String receiptFileName = null;
            Path uploadDir = null;
            // ServletContextからuploadsディレクトリの絶対パスを取得
            String contextUploadsPath = FileUploadUtil.getUploadDirectoryPath(getServletContext());
            if (contextUploadsPath == null) {
                throw new BusinessException("アップロードディレクトリの取得に失敗しました。");
            }
            uploadDir = Paths.get(contextUploadsPath);
            // 申請IDが申請情報を取得し領収書ファイル名を取得
            if (idStr != null && !idStr.isEmpty()) {
                int applicationId = Integer.parseInt(idStr);
                ExpenseApplicationService service = new ExpenseApplicationService();
                ExpenseApplication expense = service.getApplicationDetail(applicationId);
                // 申請情報が存在しない場合
                if (expense == null || expense.getReceiptPath() == null || expense.getReceiptPath().isEmpty()) {
                    throw new BusinessException("指定された申請の領収書が見つかりません。");
                }
                receiptFileName = expense.getReceiptPath(); 
            // ファイル名が指定されている場合
            } else if (fileNameFromParam != null && !fileNameFromParam.isEmpty()) {
            }
            // 領収書ファイル名が特定できない場合
            if (receiptFileName == null) {
                 throw new BusinessException("領収書ファイル名が特定できませんでした。");
            }
            // ファイルパスを取得
            Path filePath = uploadDir.resolve(receiptFileName);
            // ファイルが存在しない場合
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                throw new BusinessException("ファイルが見つかりません (" + receiptFileName + ")。試行パス: " + filePath.toString());
            }
            // MIMEタイプの設定
            String contentType;
            if (receiptFileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (receiptFileName.toLowerCase().endsWith(".jpg") || receiptFileName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
            // サムネイル表示かダウンロードかで送信するHTTPヘッダーを設定
            if (isThumbnail) {
                // サムネイル表示の場合はContent-Dispositionを設定しない
                response.setHeader("Cache-Control", "max-age=86400"); // 1日キャッシュする
            } else {
                // ダウンロードの場合はContent-Dispositionを設定
                response.setHeader("Content-Disposition", "attachment; filename=\"" + receiptFileName + "\"");
            }
            // ファイルの送信
            Files.copy(filePath, response.getOutputStream());
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
}
