package egovframework.com.uss.olp.qtm.web;

import egovframework.com.pagination.EgovPaginationFormat;
import egovframework.com.uss.olp.qtm.service.EgovQustnrTmplatService;
import egovframework.com.uss.olp.qtm.service.QustnrTmplatDTO;
import egovframework.com.uss.olp.qtm.service.QustnrTmplatVO;
import lombok.RequiredArgsConstructor;
import org.egovframe.boot.crypto.service.impl.EgovEnvCryptoServiceImpl;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller("qtmEgovQustnrTmplatAPIController")
@RequestMapping("/uss/olp/qtm")
@RequiredArgsConstructor
public class EgovQustnrTmplatAPIController {

    @Value("${egov.page.unit}")
    private int pageUnit;

    @Value("${egov.page.size}")
    private int pageSize;

    private final EgovQustnrTmplatService service;
    private final EgovEnvCryptoServiceImpl egovEnvCryptoService;

    @PostMapping(value="/qustnrTmplatList")
    public ResponseEntity<?> qustnrTmplatList(@ModelAttribute QustnrTmplatVO qustnrTmplatVO) {
        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(qustnrTmplatVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(pageUnit);
        paginationInfo.setPageSize(pageSize);

        qustnrTmplatVO.setFirstIndex(paginationInfo.getCurrentPageNo()-1);
        qustnrTmplatVO.setLastIndex(paginationInfo.getLastRecordIndex());
        qustnrTmplatVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        Page<QustnrTmplatDTO> list = service.list(qustnrTmplatVO);
        paginationInfo.setTotalRecordCount((int) list.getTotalElements());

        EgovPaginationFormat egovPaginationFormat = new EgovPaginationFormat();
        String pagination = egovPaginationFormat.paginationFormat(paginationInfo, "linkPage");

        Map<String, Object> response = new HashMap<>();
        response.put("qustnrTmplatList", list.getContent());
        response.put("pagination", pagination);
        response.put("lineNumber", (qustnrTmplatVO.getPageIndex()-1)*pageSize);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value="/qustnrTmplatDetail")
    public ResponseEntity<?> qustnrTmplatDetail(@ModelAttribute QustnrTmplatVO qustnrTmplatVO) {
        QustnrTmplatDTO result = service.detail(qustnrTmplatVO);

        Map<String, Object> response = new HashMap<>();
        if (!ObjectUtils.isEmpty(result)) {
            response.put("status", "success");
            response.put("result", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value="/qustnrTmplatInsert")
    public ResponseEntity<?> qustnrTmplatInsert(@Valid @ModelAttribute QustnrTmplatVO qustnrTmplatVO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "valid");
            response.put("errors", errors);
            return ResponseEntity.ok(response);
        }

        Map<String, String> userInfo = extracted(request);
        QustnrTmplatVO result = service.insert(qustnrTmplatVO, userInfo);

        Map<String, Object> response = new HashMap<>();
        if (!ObjectUtils.isEmpty(result)) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value="/qustnrTmplatUpdate")
    public ResponseEntity<?> qustnrTmplatUpdate(@Valid @ModelAttribute QustnrTmplatVO qustnrTmplatVO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "valid");
            response.put("errors", errors);
            return ResponseEntity.ok(response);
        }

        Map<String, String> userInfo = extracted(request);
        QustnrTmplatVO result = service.update(qustnrTmplatVO, userInfo);

        Map<String, Object> response = new HashMap<>();
        if (!ObjectUtils.isEmpty(result)) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value="/qustnrTmplatDelete")
    public ResponseEntity<?> qustnrTmplatDelete(@ModelAttribute QustnrTmplatVO qustnrTmplatVO) {
        boolean result = service.delete(qustnrTmplatVO);

        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping(value="/qustnrTmplatImage")
    public void qustnrTmplatImage(HttpServletResponse response, @RequestParam("qustnrTmplatId") String qustnrTmplatId) throws IOException {
        byte[] image = service.getImage(qustnrTmplatId);
        response.setContentType("image/*");
        response.setContentLength(image.length);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.getOutputStream().write(image);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    private Map<String, String> extracted(HttpServletRequest request) {
        Map<java.lang.String, java.lang.String> userInfo = new HashMap<>();

        java.lang.String encryptUserId = request.getHeader("X-USER-ID");
        java.lang.String encryptUserNm = request.getHeader("X-USER-NM");
        java.lang.String encryptUniqId = request.getHeader("X-UNIQ-ID");

        userInfo.put("userId", egovEnvCryptoService.decrypt(encryptUserId));
        userInfo.put("userName", egovEnvCryptoService.decrypt(encryptUserNm));
        userInfo.put("uniqId", egovEnvCryptoService.decrypt(encryptUniqId));

        return userInfo;
    }

}
