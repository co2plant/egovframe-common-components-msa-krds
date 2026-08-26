package egovframework.com.sym.ccm.ccc.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import egovframework.com.sym.ccm.ccc.service.CmmnClCodeVO;
import egovframework.com.sym.ccm.ccc.service.EgovCmmnClCodeManageService;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class EgovCmmnClCodeManageServiceImplTest {

	@Autowired
	EgovCmmnClCodeManageService egovCmmnClCodeManageService;

	@Test
	void insert() {
		// given
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("uniqId", "USRCNFRM_00000000000");

		CmmnClCodeVO cmmnClCodeVO = new CmmnClCodeVO();
		cmmnClCodeVO.setClCode("TES");

		log.debug("getClCode={}", cmmnClCodeVO.getClCode());

		// when
		CmmnClCodeVO result = egovCmmnClCodeManageService.insert(cmmnClCodeVO, userInfo);

		// then
		assertThat(result).isNotNull();

		assertThat(result.getClCode()).isEqualTo(cmmnClCodeVO.getClCode());

		log.debug("result, cmmnClCodeVO");
		log.debug("getClCode={}, {}", result.getClCode(), cmmnClCodeVO.getClCode());
		log.debug("getFrstRegistPnttm={}, {}", result.getFrstRegistPnttm(), cmmnClCodeVO.getFrstRegistPnttm());
		log.debug("getFrstRegisterId={}, {}", result.getFrstRegisterId(), cmmnClCodeVO.getFrstRegisterId());
		log.debug("getLastUpdtPnttm={}, {}", result.getLastUpdtPnttm(), cmmnClCodeVO.getLastUpdtPnttm());
		log.debug("getLastUpdusrId={}, {}", result.getLastUpdusrId(), cmmnClCodeVO.getLastUpdusrId());
	}

}
