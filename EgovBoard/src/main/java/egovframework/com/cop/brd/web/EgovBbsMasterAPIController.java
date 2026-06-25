package egovframework.com.cop.brd.web;

import egovframework.com.cop.bbs.service.CmmnDetailCodeVO;
import egovframework.com.cop.bbs.service.EgovCmmnDetailCodeService;
import egovframework.com.cop.brd.service.BbsMasterDTO;
import egovframework.com.cop.brd.service.BbsMasterOptnPublicVO;
import egovframework.com.cop.brd.service.BbsMasterOptnVO;
import egovframework.com.cop.brd.service.BbsMasterVO;
import egovframework.com.cop.brd.service.EgovBbsMasterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.egovframe.boot.crypto.service.impl.EgovEnvCryptoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("brdEgovBbsMasterAPIController")
@RequestMapping("/cop/brd")
@RequiredArgsConstructor
public class EgovBbsMasterAPIController {

    private final EgovBbsMasterService service;
    private final EgovCmmnDetailCodeService cmmnDetailCodeService;
    private final EgovEnvCryptoServiceImpl egovEnvCryptoService;

    @PostMapping(value = "/bbsMasterDetail")
    public ResponseEntity<?> bbsMasterDetail(@ModelAttribute BbsMasterVO bbsMasterVO) {
        BbsMasterDTO result = service.detail(bbsMasterVO);
        List<CmmnDetailCodeVO> list = cmmnDetailCodeService.list();

        Map<String, Object> response = new HashMap<>();
        if (!ObjectUtils.isEmpty(result)) {
            response.put("status", "success");
            response.put("result", result);
            response.put("cmmnDetailCodeList", list);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/selectBBSMasterOptn")
    public ResponseEntity<?> selectBBSMasterOptn(String bbsId, HttpServletRequest request) {
        Map<String, String> userInfo = extracted(request);
        if (ObjectUtils.isEmpty(userInfo.get("uniqId"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BbsMasterOptnVO bbsMasterOptnVO = service.selectBBSMasterOptn(bbsId);
        if (bbsMasterOptnVO == null) {
            return ResponseEntity.notFound().build();
        }
        if (!service.isBbsMasterOptnAccessible(bbsId, userInfo.get("uniqId"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(BbsMasterOptnPublicVO.from(bbsMasterOptnVO));
    }

    private Map<String, String> extracted(HttpServletRequest request) {
        Map<String, String> userInfo = new HashMap<>();

        String encryptUserId = request.getHeader("X-USER-ID");
        String encryptUserNm = request.getHeader("X-USER-NM");
        String encryptUniqId = request.getHeader("X-UNIQ-ID");

        userInfo.put("userId", egovEnvCryptoService.decrypt(encryptUserId));
        userInfo.put("userName", egovEnvCryptoService.decrypt(encryptUserNm));
        userInfo.put("uniqId", egovEnvCryptoService.decrypt(encryptUniqId));

        return userInfo;
    }

}
