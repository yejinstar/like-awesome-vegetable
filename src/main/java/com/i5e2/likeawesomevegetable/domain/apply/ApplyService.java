package com.i5e2.likeawesomevegetable.domain.apply;

import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyRequest;
import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyResponse;
import com.i5e2.likeawesomevegetable.domain.apply.exception.ApplyException;
import com.i5e2.likeawesomevegetable.domain.apply.exception.ErrorCode;
import com.i5e2.likeawesomevegetable.domain.market.CompanyBuying;
import com.i5e2.likeawesomevegetable.domain.user.User;
import com.i5e2.likeawesomevegetable.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyJpaRepository applyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CompanyBuyingJpaRepository companyBuyingJpaRepository;

    // 모집 참여 조회
    public Page<ApplyResponse> list(Long companyBuyingId, Pageable pageable) {

        return applyJpaRepository.findAllByCompanyBuyingId(companyBuyingId, pageable).map(ApplyResponse::fromEntity);
    }

    // 모집 참여 신청하기
    public ApplyResponse apply(ApplyRequest request, Long companyBuyingId, String userEmail) {

        CompanyBuying companyBuying = companyBuyingJpaRepository.findById(companyBuyingId)
                .orElseThrow(() -> new ApplyException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

        User user = userJpaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplyException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

        Apply savedApply = applyJpaRepository
                .save(request.toEntity(request.getApplyQuantity(), companyBuying, user, ComapnyBuyingStatus.IN_PROGRESS));

        // 참여 고유번호 생성(APPLY-날짜-게시글번호-신청ID)
        String applyNumber = "APPLY-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"))
                + companyBuying.getId() + savedApply.getId();

        savedApply.setApplyNumber(applyNumber);

        return ApplyResponse.fromEntity(savedApply);
    }
}