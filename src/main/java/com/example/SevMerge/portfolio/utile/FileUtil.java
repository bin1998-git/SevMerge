package com.example.SevMerge.portfolio.utile;

import com.example.SevMerge.core.exception.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {

    // 업로드될 파일 경로
    public static final String IMAGE_DIR = Paths.get(System.getProperty("user.home"),"sevMerge_uploads", "portfolio").toString();

    public static String saveFile(MultipartFile file) throws IOException {

        // 1 . 1단계 파일 유효성 검사 - 파일이 없거나 크기가 0 이면 오류
        if(file == null || file.isEmpty()){
            return null;
        }

        // 파일 업로드 경로 생성
        Path uploadPath = Paths.get(IMAGE_DIR);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath); // 뎁스가 깊으면 상위 폴더까지 자동생성
        }

        // 원본 파일 이름 가져오기 만약 A 사용자와 B 사용자가 파일 이름같을때 덮어쓰기 방지
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || originalFilename.isBlank()){
            throw new NotFoundException("파일명이 없습니다.");
        }

        // 파일 이미지가 절대 중복되지 않도록 처리
        // uuid로 고유 명사 생성
        String uuid = UUID.randomUUID().toString();
        String savedFileName = uuid + "_" + originalFilename;
        // 파일 경로
        Path filePath = uploadPath.resolve(savedFileName);
        Files.copy(file.getInputStream(),filePath);
        return savedFileName;
    }

    // 2 . 파일 삭제하는 기능 만들어 주기
    // 파일 삭제에서는 파일 이름과 파일 경로가 필요하다
    // File은 파일 생성 경로를 만들고 파일 자체의 데이터는 가지지 않는다
    public static void deleteFile(String fileName) throws IOException {
        if(fileName == null || fileName.isEmpty()){
            return;
        }
        // Path -> C://upload/xxx_a.png
        Path filePath = Paths.get(IMAGE_DIR, fileName);
        if(Files.exists(filePath)){
            // 폴더경로 존재 확인 했고 파일명 기준으로 파일이 존재하면 삭제
            Files.delete(filePath);
        }
    }
    // 3. 이미지 파일 맞는지 확인하는 메서드
    public static boolean isImageFile(MultipartFile file){
        if(file == null || file.isEmpty()){
            return false;
        }
        String contentType = file.getContentType(); // image/png , image/jpg , application/pdf
        boolean isImage = contentType.startsWith("image/"); // image로 시작하는 파일
        return isImage;
    }

}
