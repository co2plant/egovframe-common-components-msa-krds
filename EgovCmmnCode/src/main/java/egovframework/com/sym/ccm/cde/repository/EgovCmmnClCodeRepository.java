package egovframework.com.sym.ccm.cde.repository;

import egovframework.com.sym.ccm.cde.entity.CmmnClCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("cdeEgovCmmnClCodeRepository")
public interface EgovCmmnClCodeRepository extends JpaRepository<CmmnClCode, String> {

    List<CmmnClCode> findAllByUseAtEquals(String useAt);

}
