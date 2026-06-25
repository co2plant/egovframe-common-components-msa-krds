package egovframework.com.cop.brd.service.impl;

import egovframework.com.cop.brd.entity.Comment;
import egovframework.com.cop.brd.entity.CommentId;
import egovframework.com.cop.brd.repository.EgovCommentRepository;
import egovframework.com.cop.brd.service.CommentVO;
import egovframework.com.cop.brd.service.EgovCommentService;
import egovframework.com.cop.brd.util.EgovBoardUtility;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service("brdEgovCommentService")
public class EgovCommentServiceImpl extends EgovAbstractServiceImpl implements EgovCommentService {

    private final EgovCommentRepository commentRepository;

    @Qualifier("egovAnswerNoGnrService")
    private final EgovIdGnrService idgenServiceComment;

    public EgovCommentServiceImpl(EgovCommentRepository commentRepository, @Qualifier("egovAnswerNoGnrService") EgovIdGnrService egovAnswerNoGnrService) {
        this.commentRepository = commentRepository;
        this.idgenServiceComment = egovAnswerNoGnrService;
    }

    @Override
    public Page<CommentVO> selectArticleCommentList(CommentVO commentVO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "frstRegistPnttm");
        Pageable pageable = PageRequest.of(commentVO.getFirstIndex(), commentVO.getRecordCountPerPage(), sort);
        return commentRepository.findAllByCommentId_BbsIdAndCommentId_NttIdAndUseAt(commentVO.getBbsId(), commentVO.getNttId(), "Y", pageable).map(EgovBoardUtility::commentEntityToVO);
    }

    @Override
    public void insertArticleComment(CommentVO commentVO, Map<String, String> userInfo) throws FdlException {
        if (commentVO.getAnswerNo() == null) {
            Long id = idgenServiceComment.getNextLongId();
            commentVO.setAnswerNo(id);
        }

        if (commentVO.getFrstRegistPnttm() == null) {
            commentVO.setFrstRegistPnttm(LocalDateTime.now());
        } else {
            commentVO.setLastUpdtPnttm(LocalDateTime.now());
        }

        commentVO.setUseAt("Y");

        // 더미데이터 추가
        commentVO.setWrterId(userInfo.get("uniqId"));
        commentVO.setWrterNm(userInfo.get("userName"));
        commentVO.setFrstRegisterId(userInfo.get("uniqId"));

        commentRepository.save(EgovBoardUtility.commentVOToEntity(commentVO));
    }

    @Override
    public void deleteArticleComment(CommentVO commentVO, Map<String, String> userInfo) {
        CommentId commentId = new CommentId();
        commentId.setBbsId(commentVO.getBbsId());
        commentId.setNttId(commentVO.getNttId());
        commentId.setAnswerNo(commentVO.getAnswerNo());

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            throw new IllegalStateException("댓글을 찾을 수 없습니다.");
        }

        String uniqId = userInfo != null ? userInfo.get("uniqId") : null;
        if (ObjectUtils.isEmpty(uniqId)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String ownerId = comment.getFrstRegisterId();
        if (ObjectUtils.isEmpty(ownerId)) {
            ownerId = comment.getWrterId();
        }
        if (ObjectUtils.isEmpty(ownerId) || !Objects.equals(uniqId, ownerId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        comment.setLastUpdusrId(uniqId);
        comment.setLastUpdtPnttm(LocalDateTime.now());
        comment.setUseAt("N");
        commentRepository.save(comment);
    }

}
