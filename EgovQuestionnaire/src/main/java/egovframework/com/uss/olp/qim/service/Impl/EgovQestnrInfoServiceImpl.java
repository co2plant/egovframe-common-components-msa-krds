package egovframework.com.uss.olp.qim.service.impl;

import egovframework.com.uss.olp.qim.repository.EgovQestnrInfoRepository;
import egovframework.com.uss.olp.qim.service.EgovQestnrInfoService;
import egovframework.com.uss.olp.qim.service.QestnrInfoDTO;
import egovframework.com.uss.olp.qim.service.QestnrInfoVO;
import lombok.RequiredArgsConstructor;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("qimEgovQestnrInfoService")
@RequiredArgsConstructor
public class EgovQestnrInfoServiceImpl extends EgovAbstractServiceImpl implements EgovQestnrInfoService {

    private final EgovQestnrInfoRepository repository;

    @Override
    public Page<QestnrInfoDTO> list(QestnrInfoVO qestnrInfoVO) {
        Pageable pageable = PageRequest.of(qestnrInfoVO.getFirstIndex(), qestnrInfoVO.getRecordCountPerPage());
        String searchCondition = qestnrInfoVO.getSearchCondition();
        String searchKeyword = qestnrInfoVO.getSearchKeyword();
        return repository.qestnrInfoList(searchCondition, searchKeyword, pageable);
    }

}
