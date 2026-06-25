package egovframework.com.cop.brd.web;

import egovframework.com.cop.brd.service.EgovFileService;
import egovframework.com.cop.brd.service.FileVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egovframe.boot.crypto.service.impl.EgovEnvCryptoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("brdEgovFileAPIController")
@RequiredArgsConstructor
@Slf4j
public class EgovFileAPIController {

    public final EgovFileService fileMngService;
    private final EgovEnvCryptoServiceImpl egovEnvCryptoService;

    @ResponseBody
    @PostMapping("/cop/brd/selectFileInfs")
    public ResponseEntity<List<FileVO>> selectFileInfs(@RequestBody String atchFileId, HttpServletRequest request) throws Exception {
        Map<String, String> userInfo = extracted(request);
        String uniqId = userInfo.get("uniqId");

        String decodeId = egovEnvCryptoService.decrypt(atchFileId);
        if (StringUtils.isBlank(decodeId) || !decodeId.contains("|")) {
            log.warn("selectFileInfs: invalid or empty token");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String[] bindParts = decodeId.split("\\|", -1);
        if (bindParts.length < 2 || StringUtils.isBlank(bindParts[0]) || StringUtils.isBlank(bindParts[1])) {
            log.warn("selectFileInfs: malformed token");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (StringUtils.isBlank(uniqId) || !uniqId.equals(bindParts[1])) {
            log.warn("selectFileInfs: user binding mismatch");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String decodeFileId = bindParts[0];

        List<FileVO> response = fileMngService.selectFileInfs(decodeFileId);
        response.forEach(fileVO -> fileVO.setAtchFileId(egovEnvCryptoService.encrypt(decodeId + "|" + fileVO.getFileSn())));

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @PostMapping("/cop/brd/deleteFileInfs")
    public void deleteFileInfs(FileVO fileVO, HttpServletRequest request) throws Exception {
        Map<String, String> userInfo = extracted(request);
        String uniqId = userInfo.get("uniqId");

        String decodeId = egovEnvCryptoService.decrypt(fileVO.getAtchFileId());
        if (StringUtils.isBlank(decodeId) || !decodeId.contains("|")) {
            log.warn("deleteFileInfs: invalid token");
            return;
        }
        String[] segments = decodeId.split("\\|", -1);
        if (segments.length < 2 || StringUtils.isBlank(segments[0]) || StringUtils.isBlank(segments[1])) {
            log.warn("deleteFileInfs: malformed token");
            return;
        }
        if (StringUtils.isBlank(uniqId) || !uniqId.equals(segments[1])) {
            log.warn("deleteFileInfs: user binding mismatch");
            return;
        }

        String decodeFileId = segments[0];

        if (fileVO.getDeleteFileSn() == null || fileVO.getDeleteFileSn().length == 0) {
            return;
        }

        List<FileVO> existing = fileMngService.selectFileInfs(decodeFileId);
        Set<String> validSn = existing.stream()
                .map(FileVO::getFileSn)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        List<String> allowed = Arrays.stream(fileVO.getDeleteFileSn())
                .filter(StringUtils::isNotBlank)
                .filter(validSn::contains)
                .collect(Collectors.toList());

        if (allowed.isEmpty()) {
            log.warn("deleteFileInfs: no valid fileSn in request for atchFileId={}", decodeFileId);
            return;
        }

        fileVO.setAtchFileId(decodeFileId);
        fileVO.setDeleteFileSn(allowed.toArray(new String[0]));
        fileMngService.deleteFileInfs(fileVO);
    }

    @ResponseBody
    @GetMapping("/cop/brd/fileDownload")
    public void fileDownload(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String decodeId = egovEnvCryptoService.decrypt(fileVO.getAtchFileId());
        if (StringUtils.isBlank(decodeId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] segments = decodeId.split("\\|", -1);
        Map<String, String> userInfo = extracted(request);
        String uniqId = userInfo.get("uniqId");
        String decodeFileId;
        String decodeFileSn;
        if (segments.length >= 3) {
            if (StringUtils.isBlank(uniqId) || !segments[1].equals(uniqId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.warn("fileDownload denied: token user binding mismatch");
                return;
            }
            decodeFileId = segments[0];
            decodeFileSn = segments[segments.length - 1];
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            log.warn("fileDownload denied: unsupported token format");
            return;
        }

        fileVO.setAtchFileId(decodeFileId);
        fileVO.setFileSn(decodeFileSn);

        fileVO = fileMngService.detailFileInf(fileVO);
        File file = new File(fileVO.getFileStreCours(), fileVO.getStreFileNm());

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.warn("요청한 파일이 존재하지 않습니다. atchFileId={}, fileSn={}, path={}",
                    decodeFileId, decodeFileSn, file.getAbsolutePath());
            return;
        }

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String originalFileName = fileVO.getOrignlFileNm();
            String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
            String userAgent = request.getHeader("User-Agent");

            String contentDisposition;

            if (userAgent != null && userAgent.contains("MSIE")) {
                contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
            } else if (userAgent != null && userAgent.contains("Trident")) {
                contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
            } else if (userAgent != null && userAgent.contains("Edge")) {
                contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
            } else {
                contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", contentDisposition);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setContentLengthLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.warn("파일을 찾을 수 없습니다. atchFileId={}, fileSn={}, path={}",
                    decodeFileId, decodeFileSn, file.getAbsolutePath(), e);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("파일 다운로드 처리 중 I/O 오류가 발생했습니다. atchFileId={}, fileSn={}, path={}",
                    decodeFileId, decodeFileSn, file.getAbsolutePath(), e);
        }
    }

    private Map<String, String> extracted(HttpServletRequest request) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("userId", egovEnvCryptoService.decrypt(request.getHeader("X-USER-ID")));
        userInfo.put("userName", egovEnvCryptoService.decrypt(request.getHeader("X-USER-NM")));
        userInfo.put("uniqId", egovEnvCryptoService.decrypt(request.getHeader("X-UNIQ-ID")));
        return userInfo;
    }

}
