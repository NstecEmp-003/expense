package com.fullness.keihiseisan.model.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * ファイルアップロードに関するユーティリティクラス。
 */
public class FileUploadUtil {
    /** ログ */
    private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());
    /** アップロードディレクトリ */
    private static final String UPLOADS_DIR = "uploads";
    /**
     * プライベートコンストラクタ。ユーティリティクラスのためインスタンス化を防止します。
     */
    private FileUploadUtil() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * 実行環境に応じた適切なアップロードディレクトリを取得します。
     * @param servletContext サーブレットコンテキスト
     * @return アップロードディレクトリのパス
     * @throws SystemException ディレクトリの作成に失敗した場合
     */
    public static String getUploadDirectoryPath(ServletContext servletContext) throws SystemException {
        // まず、Tomcatの標準的なWebアプリケーションディレクトリパスを試みる
        String realPath = servletContext.getRealPath("/" + UPLOADS_DIR);
        Path uploadPath = Paths.get(realPath);
        // パスの存在チェックと書き込み権限チェック
        if (Files.exists(uploadPath) && Files.isWritable(uploadPath)) {
            logger.info("アップロードディレクトリ: " + uploadPath);
            return realPath;
        }
        // パスが存在しない場合は作成を試みる
        try {
            Files.createDirectories(uploadPath);
            logger.info("アップロードディレクトリを作成しました: " + uploadPath);
            return realPath;
        } catch (IOException e) {
            logger.log(Level.WARNING, "標準パスへのディレクトリ作成に失敗: " + e.getMessage());
            // 代替パスを試みる - 開発環境用
            String tempDir = System.getProperty("java.io.tmpdir");
            Path tempPath = Paths.get(tempDir, UPLOADS_DIR);
            try {
                Files.createDirectories(tempPath);
                logger.info("代替アップロードディレクトリを使用: " + tempPath);
                return tempPath.toString();
            } catch (IOException ex) {
                throw new SystemException("アップロードディレクトリの作成に失敗しました", ex);
            }
        }
    }
    /**
     * アップロードされた領収書ファイルを指定されたディレクトリに保存します。
     * ファイル名はUUIDを使用して一意にし、元のファイル名を付加します。
     * @param filePart        アップロードされたファイルパート (jakarta.servlet.http.Part)
     * @param uploadDirectory アップロード先ディレクトリの絶対パス
     * @return 保存されたファイルの相対パス (例: uploads/uuid_receipt.jpg)。保存に失敗した場合は null。
     * @throws SystemException ファイル保存中にエラーが発生した場合
     */
    public static String saveReceipt(Part filePart, String uploadDirectory) throws SystemException {
        if (filePart == null || filePart.getSize() == 0) {
            return null; // ファイルがない場合は何もしない
        }
        // アップロードディレクトリの存在確認  
        try {
            Path uploadDirPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadDirPath)) {
                // アップロードディレクトリが存在しない場合は作成
                try {
                    Files.createDirectories(uploadDirPath);
                    logger.info("アップロードディレクトリを作成しました: " + uploadDirPath.toString());
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "アップロードディレクトリの作成に失敗しました: " + uploadDirPath.toString(), e);
                    // 代替ディレクトリを使用
                    uploadDirPath = Paths.get(System.getProperty("java.io.tmpdir"), "uploads");
                    Files.createDirectories(uploadDirPath);
                    logger.info("代替ディレクトリを使用します: " + uploadDirPath.toString());
                }
            }
            String originalFileName = getFileName(filePart);
            if (originalFileName == null || originalFileName.isEmpty()) {
                logger.warning("ファイル名の取得に失敗しました。");
                return null;
            }
            Path filePath = uploadDirPath.resolve(originalFileName);
            System.out.println("filePath: " + filePath.toString());
            // ファイルを保存
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("ファイルを保存しました: " + filePath.toString());
            }
            // 保存したファイル名のみを返すように変更
            String relativePath = originalFileName;
            logger.info("保存されたファイル名 (相対パスとして使用): " + relativePath);
            return relativePath;
        } catch (IOException e) {
            throw new SystemException("ファイルの保存に失敗しました", e);
        }
    }
    /**
     * Partオブジェクトから元のファイル名を取得します。
     * Content-Dispositionヘッダーを解析します。
     * @param part ファイルパート
     * @return ファイル名。取得できない場合は null。
     */
    public static String getFileName(Part part) {
        String contentDisposition = part.getHeader("Content-Disposition");
        if (contentDisposition != null) {
            for (String cd : contentDisposition.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                    // 古いブラウザや一部のケースでパスが含まれる場合があるため、ファイル名のみを抽出
                    return Paths.get(fileName).getFileName().toString();
                }
            }
        }
        return null; // ファイル名が見つからない場合
    }
}