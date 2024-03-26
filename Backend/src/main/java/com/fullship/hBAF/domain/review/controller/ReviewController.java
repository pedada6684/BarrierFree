package com.fullship.hBAF.domain.review.controller;

import com.fullship.hBAF.domain.review.controller.request.AddReviewRequest;
import com.fullship.hBAF.domain.review.controller.request.DeleteReviewRequest;
import com.fullship.hBAF.domain.review.controller.request.ModifyReviewRequest;
import com.fullship.hBAF.domain.review.controller.response.*;
import com.fullship.hBAF.domain.review.service.ReviewService;
import com.fullship.hBAF.domain.review.service.command.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<GetReviewResponse> getReview(@RequestParam Long reviewId){
        System.out.println("******* "+reviewId);
        GetReviewRequestCommand command = GetReviewRequestCommand.builder()
                .reviewId(reviewId)
                .build();

        GetReviewResponse response = reviewService.getReview(command);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<GetAllReviewsByPoiIdResponse> getAllReviewsByPoiId(@RequestParam String poiId){

        GetAllReviewsByPoiIdRequestCommand command = GetAllReviewsByPoiIdRequestCommand.builder()
                .poiId(poiId)
                .build();

        GetAllReviewsByPoiIdResponse response = reviewService.getAllReviewsByPoiId(command);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AddReviewResponse> addReview(@ModelAttribute AddReviewRequest request){

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
    public ResponseEntity<ModifyReviewResponse> modifyReview(@ModelAttribute ModifyReviewRequest request){

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
    public ResponseEntity<DeleteReviewResponse> deleteReview(@RequestBody DeleteReviewRequest request){

        DeleteReviewRequestCommand command = DeleteReviewRequestCommand.builder()
                .reviewId(request.getReviewId())
                .build();

        DeleteReviewResponse response = reviewService.deleteReview(command);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
