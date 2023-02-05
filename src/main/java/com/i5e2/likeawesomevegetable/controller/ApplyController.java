package com.i5e2.likeawesomevegetable.controller;

import com.i5e2.likeawesomevegetable.domain.Result;
import com.i5e2.likeawesomevegetable.domain.apply.ApplyService;
import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyRequest;
import com.i5e2.likeawesomevegetable.domain.apply.dto.ApplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{companyBuyingId}")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    // 모집 참여 조회
    @GetMapping("/list")
    public ResponseEntity<Result<Page<ApplyResponse>>> getApply(
            @PathVariable Long companyBuyingId,
            @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ApplyResponse> applyResponsePage = applyService.list(companyBuyingId, pageable);
        return ResponseEntity.ok().body(Result.success(applyResponsePage));
    }

    // 참여 신청
    @PostMapping("/apply")
    public ResponseEntity<Result<String>> quantityInput(
            @RequestBody ApplyRequest request, @PathVariable Long companyBuyingId, Authentication authentication) {

        applyService.apply(request, companyBuyingId, authentication.getName());
        return ResponseEntity.ok().body(Result.success("신청 성공"));
    }
}
