package com.fullship.hBAF.domain.review.controller;

import com.fullship.hBAF.domain.review.controller.request.AddReviewRequest;
import com.fullship.hBAF.domain.review.controller.request.DeleteReviewRequest;
import com.fullship.hBAF.domain.review.controller.request.ModifyReviewRequest;
import com.fullship.hBAF.domain.review.controller.response.*;
import com.fullship.hBAF.domain.review.service.ReviewService;
import com.fullship.hBAF.domain.review.service.command.request.*;
import com.fullship.hBAF.util.ImageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final ImageUtil imageUtil;

    @PostMapping("/img")
    @Operation(summary = "리뷰 이미지 등록", description = "리뷰 작성 중 등록한 이미지 저장")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UploadImgResponse.class)))
    public ResponseEntity<UploadImgResponse> uploadImg(@RequestParam("img") List<MultipartFile> img){
        List<String> list = new ArrayList<>();
        for(MultipartFile i : img) {
            System.out.println(i.getOriginalFilename());
            list.add(imageUtil.uploadImageToS3(i, "review", UUID.randomUUID().toString().replace("-", "") + i.getOriginalFilename()).toString());
        }

        UploadImgResponse response = UploadImgResponse.builder()
                .list(list)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "리뷰 세부 조회", description = "reviewId를 이용한 리뷰 세부 조회")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GetReviewResponse.class)))
    public ResponseEntity<GetReviewResponse> getReview(@RequestParam("reviewId") Long reviewId){
        GetReviewRequestCommand command = GetReviewRequestCommand.builder()
                .reviewId(reviewId)
                .build();

        GetReviewResponse response = reviewService.getReview(command);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "리뷰 목록 조회", description = "poiId를 이용한 리뷰 목록 조회")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GetAllReviewsByPoiIdResponse.class)))
    public ResponseEntity<GetAllReviewsByPoiIdResponse> getAllReviewsByPoiId(@RequestParam("poiId") String poiId){

        GetAllReviewsByPoiIdRequestCommand command = GetAllReviewsByPoiIdRequestCommand.builder()
                .poiId(poiId)
                .build();

        GetAllReviewsByPoiIdResponse response = reviewService.getAllReviewsByPoiId(command);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "리뷰 등록", description = "poiId 해당하는 장소에 대한 리뷰 작성")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AddReviewResponse.class)))
    public ResponseEntity<AddReviewResponse> addReview(@RequestBody AddReviewRequest request){

        AddReviewRequestCommand command = AddReviewRequestCommand.builder()
                .feedback(request.getFeedback())
                .content(request.getContent())
                .memberId(request.getMemberId())
                .poiId(request.getPoiId())
                .file(request.getImg())
                .build();

        AddReviewResponse response = reviewService.addReview(command);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "리뷰 수정", description = "reviewId에 해당하는 리뷰 수정")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ModifyReviewResponse.class)))
    public ResponseEntity<ModifyReviewResponse> modifyReview(@RequestBody ModifyReviewRequest request){

        ModifyReviewRequestCommand command = ModifyReviewRequestCommand.builder()
                .reviewId(request.getReviewId())
                .content(request.getContent())
                .feedback(request.getFeedback())
                .img(request.getImg())
                .build();

        ModifyReviewResponse response = reviewService.modifyReview(command);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "리뷰 삭제", description = "reviewId에 해당하는 리뷰 삭제")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DeleteReviewResponse.class)))
    public ResponseEntity<DeleteReviewResponse> deleteReview(@RequestBody DeleteReviewRequest request){

        DeleteReviewRequestCommand command = DeleteReviewRequestCommand.builder()
                .reviewId(request.getReviewId())
                .build();

        DeleteReviewResponse response = reviewService.deleteReview(command);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
