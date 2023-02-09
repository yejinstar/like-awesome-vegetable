package com.i5e2.likeawesomevegetable.controller;

import com.i5e2.likeawesomevegetable.domain.Result;
import com.i5e2.likeawesomevegetable.domain.market.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
@Slf4j
public class MarketRestController {

    private final AuctionService auctionService;
    private final BuyingService buyingService;

    @PostMapping("/auction")
    public ResponseEntity<Result<AuctionResponse>> createAuction(@RequestPart(value = "dto") AuctionRequest dto,
                                                                 @RequestPart(value = "imgs") List<MultipartFile> imgs,
                                                                 Authentication authentication) throws IOException {

    AuctionResponse auctionResponse = auctionService.createAuction(dto,imgs,authentication.getName());
    Result<AuctionResponse> response = new Result<>("SUCCESS", auctionResponse);

        return ResponseEntity.ok().body(response);
}

    @PostMapping("/buying")
    public ResponseEntity<Result<BuyingResponse>> createBuying(@RequestBody BuyingRequest buyingRequest, Authentication authentication) {
        BuyingResponse buyingResponse = buyingService.creatBuying(buyingRequest, authentication.getName());
        Result<BuyingResponse> response = new Result<>("SUCCESS", buyingResponse);

        return ResponseEntity.ok().body(response);
    }

}
