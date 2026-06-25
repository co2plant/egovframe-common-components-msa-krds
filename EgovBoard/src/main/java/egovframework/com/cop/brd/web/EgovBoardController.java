package egovframework.com.cop.brd.web;

import egovframework.com.cop.brd.service.BbsVO;
import egovframework.com.cop.brd.service.EgovBoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.egovframe.boot.crypto.service.impl.EgovEnvCryptoServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller("brdEgovBoardController")
@RequestMapping("/cop/brd")
@RequiredArgsConstructor
public class EgovBoardController {

    private final EgovBoardService egovBoardService;
    private final EgovEnvCryptoServiceImpl egovEnvCryptoService;

    @GetMapping(value = "/index")
    public String index(BbsVO bbsVO, Model model) {
        return boardListView(bbsVO, model);
    }

    @RequestMapping(value = "/boardListView", method = {RequestMethod.GET, RequestMethod.POST})
    public String boardListView(BbsVO bbsVO, Model model) {
        model.addAttribute("bbsVO", bbsVO);
        return "cop/brd/boardList";
    }

    @PostMapping(value = "/boardDetailView")
    public String boardDetailView(BbsVO bbsVO, Model model) {
        model.addAttribute("bbsVO", bbsVO);
        return "cop/brd/boardDetail";
    }

    @PostMapping(value = "/boardInsertView")
    public String boardInsertView(BbsVO bbsVO, Model model) {
        model.addAttribute("bbsVO", bbsVO);
        return "cop/brd/boardInsert";
    }

    @PostMapping(value = "/boardUpdateView")
    public String boardUpdateView(BbsVO bbsVO, Model model, HttpServletRequest request) {
        try {
            Map<String, String> userInfo = extracted(request);
            egovBoardService.assertUpdateAuthorized(bbsVO, userInfo);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "cop/brd/boardUpdateDenied";
        }
        model.addAttribute("bbsVO", bbsVO);
        return "cop/brd/boardUpdate";
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
