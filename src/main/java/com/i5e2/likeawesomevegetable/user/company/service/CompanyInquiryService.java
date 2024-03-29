package com.i5e2.likeawesomevegetable.user.company.service;

import com.i5e2.likeawesomevegetable.board.post.dto.ParticipationStatus;
import com.i5e2.likeawesomevegetable.common.exception.AppErrorCode;
import com.i5e2.likeawesomevegetable.common.exception.AwesomeVegeAppException;
import com.i5e2.likeawesomevegetable.company.buying.CompanyBuying;
import com.i5e2.likeawesomevegetable.company.buying.repository.CompanyBuyingJpaRepository;
import com.i5e2.likeawesomevegetable.company.buying.repository.CompanyImageJpaRepository;
import com.i5e2.likeawesomevegetable.item.repository.ItemJpaRepository;
import com.i5e2.likeawesomevegetable.user.company.CompanyUser;
import com.i5e2.likeawesomevegetable.user.company.dto.*;
import com.i5e2.likeawesomevegetable.user.company.repository.CompanyUserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyInquiryService {

    private final CompanyUserJpaRepository companyUserJpaRepository;
    private final CompanyBuyingJpaRepository companyBuyingJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final CompanyImageJpaRepository companyImageJpaRepository;

    public Page<CompanyListResponse> list(Pageable pageable) {

        return companyUserJpaRepository.findAll(pageable)
                .map(companyUser -> CompanyListResponse.builder()
                        .id(companyUser.getId())
                        .companyName(companyUser.getCompanyName())
                        .companyMajorItem(itemJpaRepository.findByItemCode(companyUser.getCompanyMajorItem()).getItemName())
                        .companyAddress(companyUser.getCompanyAddress())
                        .build()
                );
    }

    public CompanyDetailResponse detail(Long companyId, Pageable pageable) {

        CompanyUser companyUser = companyUserJpaRepository.findById(companyId)
                .orElseThrow(() -> new AwesomeVegeAppException(AppErrorCode.USER_NOT_FOUND, AppErrorCode.USER_NOT_FOUND.getMessage()));

        // 기업 이미지 가져오기
        List<CompanyImageLink> companyImage = companyImageJpaRepository.findByCompanyUserId(companyId);

        // 기업 소개
        CompanyUserResponse companyUserResponse = CompanyUserResponse
                .fromEntity(companyUser, itemJpaRepository.findByItemCode(companyUser.getCompanyMajorItem()).getItemName());

        // 현재 진행중인 모집 목록
        Page<CompanyBuying> companyBuyings = companyBuyingJpaRepository
                .findAllByCompanyUserIdAndParticipationStatus(companyId, ParticipationStatus.UNDERWAY, pageable);

        Page<BuyingListResponse> buyingListResponses = companyBuyings.map(buyingListResponse -> BuyingListResponse
                .fromEntity(buyingListResponse,
                        itemJpaRepository.findByItemCode(buyingListResponse.getBuyingItem()).getItemCategoryName(),
                        itemJpaRepository.findByItemCode(buyingListResponse.getBuyingItem()).getItemName()));

        return CompanyDetailResponse.builder()
                .companyImage(companyImage)
                .companyUserResponse(companyUserResponse)
                .buyingListResponses(buyingListResponses)
                .build();
    }
}