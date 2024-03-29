package com.i5e2.likeawesomevegetable.company.buying.service;

import com.i5e2.likeawesomevegetable.alarm.Alarm;
import com.i5e2.likeawesomevegetable.alarm.AlarmJpaRepository;
import com.i5e2.likeawesomevegetable.alarm.dto.AlarmDetail;
import com.i5e2.likeawesomevegetable.common.exception.AppErrorCode;
import com.i5e2.likeawesomevegetable.common.exception.AwesomeVegeAppException;
import com.i5e2.likeawesomevegetable.company.apply.Apply;
import com.i5e2.likeawesomevegetable.company.apply.ApplyJpaRepository;
import com.i5e2.likeawesomevegetable.company.buying.CompanyBuying;
import com.i5e2.likeawesomevegetable.company.buying.dto.BuyingRequest;
import com.i5e2.likeawesomevegetable.company.buying.dto.BuyingResponse;
import com.i5e2.likeawesomevegetable.company.buying.repository.CompanyBuyingJpaRepository;
import com.i5e2.likeawesomevegetable.user.basic.User;
import com.i5e2.likeawesomevegetable.user.basic.repository.UserJpaRepository;
import com.i5e2.likeawesomevegetable.user.company.CompanyUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuyingService {
    private final CompanyBuyingJpaRepository buyingJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CompanyBuyingJpaRepository companyBuyingJpaRepository;
    private final ApplyJpaRepository applyJpaRepository;
    private final AlarmJpaRepository alarmJpaRepository;

    public BuyingResponse creatBuying(BuyingRequest buyingRequest, String email) {
        User user = userJpaRepository.findByEmail(email).get();
        CompanyUser companyUser = user.getCompanyUser();

        notValidCompanyUser(companyUser);

        CompanyBuying companyBuying = buyingRequest.toEntity(buyingRequest, companyUser);
        buyingJpaRepository.save(companyBuying);

        BuyingResponse buyingResponse = BuyingResponse.builder()
                .buyingId(companyBuying.getId())
                .message("모집 게시글 작성 완료")
                .build();
        return buyingResponse;
    }


    private void notValidCompanyUser(CompanyUser companyUser) {
        if (companyUser == null) {
            throw new AwesomeVegeAppException(
                    AppErrorCode.COMPANY_USER_NOT_FOUND,
                    AppErrorCode.COMPANY_USER_NOT_FOUND.getMessage()
            );
        }
    }

    // 모집 종료
    public void applyEnd(Long companyBuyingId) {
        CompanyBuying companyBuying = companyBuyingJpaRepository.findById(companyBuyingId)
                .orElseThrow(() -> new AwesomeVegeAppException(AppErrorCode.POST_NOT_FOUND, AppErrorCode.POST_NOT_FOUND.getMessage()));

        applyJpaRepository.findAllByCompanyBuyingId(companyBuyingId).forEach(Apply::updateStatusToEnd);

        companyBuying.updateStatusToEnd();
        // TODO - alarm
        List<User> list = applyJpaRepository.selectByCompanyBuyingId(companyBuyingId);
        for (User user : list) {
            log.info("user:{}", user);
        }
        list.get(1).getCompanyUser();
        for (int i = 0; i < list.size(); i++) {
            User getUser = userJpaRepository.findById(list.get(i).getId()).get();
            Alarm alarm = Alarm.builder()
                    .alarmDetail(AlarmDetail.BUYING)
                    .alarmTriggerId(companyBuying.getId())
                    .alarmRead(Boolean.FALSE)
                    .alarmSenderId(companyBuying.getCompanyUser().getId())
                    .user(getUser)
                    .build();
            alarmJpaRepository.save(alarm);
        }
    }
}
