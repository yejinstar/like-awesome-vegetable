package com.i5e2.likeawesomevegetable.domain.admin;

import com.i5e2.likeawesomevegetable.domain.admin.dto.AdminTransferResponse;
import com.i5e2.likeawesomevegetable.domain.admin.dto.TransferEventDetailResponse;
import com.i5e2.likeawesomevegetable.domain.admin.entity.AdminPaymentOrder;
import com.i5e2.likeawesomevegetable.domain.payment.api.PaymentFactory;
import com.i5e2.likeawesomevegetable.domain.payment.api.entity.Payment;
import com.i5e2.likeawesomevegetable.domain.point.PointFactory;
import com.i5e2.likeawesomevegetable.domain.point.entity.PointEventLog;
import com.i5e2.likeawesomevegetable.exception.AppErrorCode;
import com.i5e2.likeawesomevegetable.exception.AwesomeVegeAppException;
import com.i5e2.likeawesomevegetable.repository.AdminPaymentOrderJpaRepository;
import com.i5e2.likeawesomevegetable.repository.PaymentJpaRepository;
import com.i5e2.likeawesomevegetable.repository.PointEventLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferManagerService {
    private final PaymentJpaRepository paymentJpaRepository;
    private final AdminPaymentOrderJpaRepository adminPaymentOrderJpaRepository;
    private final PointEventLogJpaRepository pointEventLogJpaRepository;

    @Transactional(timeout = 2, rollbackFor = Exception.class)
    public TransferEventDetailResponse savePaymentAndTransfer(AdminTransferResponse adminTransferResponse, String orderId) {
        AdminPaymentOrder adminPaymentOrder = adminPaymentOrderJpaRepository.findByAdminOrderId(orderId)
                .orElseThrow(() -> {
                    throw new AwesomeVegeAppException(AppErrorCode.NO_PAYMENT_ORDER_RESULT,
                            AppErrorCode.NO_PAYMENT_ORDER_RESULT.getMessage());
                });

        Payment transfer = PaymentFactory.createTransfer(adminTransferResponse, adminPaymentOrder);
        paymentJpaRepository.save(transfer);

        PointEventLog transferEventLog = PointFactory.createTransferEventLog(transfer);
        PointEventLog transferDetailResult = pointEventLogJpaRepository.save(transferEventLog);
        return PointFactory.form(transferDetailResult, adminPaymentOrder);
    }
}
