package com.fullship.hBAF.domain.review.service;

import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.review.controller.response.*;
import com.fullship.hBAF.domain.review.entity.Review;
import com.fullship.hBAF.domain.review.repository.ReviewRepository;
import com.fullship.hBAF.domain.review.service.command.request.*;
import com.fullship.hBAF.domain.review.service.command.response.*;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;


    public GetReviewResponse getReview(GetReviewRequestCommand command){

        Review review = reviewRepository.findById(command.getReviewId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
        Member member = memberRepository.findById(review.getId()).get();

        GetReviewResponseCommand responseCommand = GetReviewResponseCommand.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .content(review.getContent())
                .like(review.getLike())
                .regDate(review.getRegDate())
                .modifyDate(review.getModifyDate())
                .status(review.getStatus())
                .poiId(review.getPoiId())
                .build();

        GetReviewResponse response  = GetReviewResponse.builder()
                .memberId(responseCommand.getMemberId())
                .nickname(responseCommand.getNickname())
                .content(responseCommand.getContent())
                .like(responseCommand.getLike())
                .regDate(review.getRegDate())
                .modifyDate(responseCommand.getModifyDate())
                .status(responseCommand.getStatus())
                .poiId(responseCommand.getPoiId())
                .build();

        return response;
    }

    public GetAllReviewsByPoiIdResponse getAllReviewsByPoiId(GetAllReviewsByPoiIdRequestCommand command){

        List<Review> allReviews = reviewRepository.findAllByPoiId(command.getPoiId());
        List<GetAllReviewsByPoiIdResponseCommand> list = new ArrayList<>();

        for(Review review : allReviews) {
            Member member = memberRepository.findById(review.getId()).get();

            GetAllReviewsByPoiIdResponseCommand responseCommand = GetAllReviewsByPoiIdResponseCommand.builder()
                    .memberId(member.getId())
                    .nickname(member.getNickname())
                    .content(review.getContent())
                    .like(review.getLike())
                    .regDate(review.getRegDate())
                    .modifyDate(review.getModifyDate())
                    .status(review.getStatus())
                    .poiId(review.getPoiId())
                    .build();

            list.add(responseCommand);
        }

        GetAllReviewsByPoiIdResponse response = GetAllReviewsByPoiIdResponse.builder()
                .list(list)
                .build();
        return response;
    }

    public AddReviewResponse addReview(AddReviewRequestCommand command){
        Member member = memberRepository.findById(command.getMemberId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        Review review = Review.createToReview(
                member,
                command.getContent(),
                command.getLike(),
                command.getPoiId());

        reviewRepository.save(review);

        AddReviewResponseCommand responseCommand = AddReviewResponseCommand.builder()
                .response("success")
                .build();

        return AddReviewResponse.builder().response(responseCommand).build();
    }

    public ModifyReviewResponse modifyReview(ModifyReviewRequestCommand command){
        Review review = reviewRepository.findById(command.getReviewId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        review.modifyReview(
                command.getContent(),
                command.getLike()
        );

        ModifyReviewResponseCommand responseCommand = ModifyReviewResponseCommand.builder()
                .response("success")
                .build();

        return ModifyReviewResponse.builder().response(responseCommand).build();
    }

    public DeleteReviewResponse deleteReview(DeleteReviewRequestCommand command){
        reviewRepository.findById(command.getReviewId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        reviewRepository.deleteById(command.getReviewId());

        DeleteReviewResponseCommand responseCommand = DeleteReviewResponseCommand.builder()
                .response("success")
                .build();

        return DeleteReviewResponse.builder().response(responseCommand).build();
    }

}
