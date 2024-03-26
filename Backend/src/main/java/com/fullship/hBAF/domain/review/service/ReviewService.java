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
import com.fullship.hBAF.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ImageUtil imageUtil;

    public GetReviewResponse getReview(GetReviewRequestCommand command){

        Review review = reviewRepository.findById(command.getReviewId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
        Member member = memberRepository.findById(review.getId()).get();

        GetReviewResponseCommand responseCommand = GetReviewResponseCommand.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .content(review.getContent())
                .feedback(review.getFeedback())
                .regDate(review.getRegDate())
                .modifyDate(review.getModifyDate())
                .status(review.getStatus())
                .poiId(review.getPoiId())
                .img(review.getImgUrl())
                .build();

        GetReviewResponse response  = GetReviewResponse.builder()
                .memberId(responseCommand.getMemberId())
                .nickname(responseCommand.getNickname())
                .content(responseCommand.getContent())
                .feedback(responseCommand.getFeedback())
                .regDate(review.getRegDate())
                .modifyDate(responseCommand.getModifyDate())
                .status(responseCommand.getStatus())
                .poiId(responseCommand.getPoiId())
                .img(responseCommand.getImg())
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
                    .feedback(review.getFeedback())
                    .regDate(review.getRegDate())
                    .modifyDate(review.getModifyDate())
                    .status(review.getStatus())
                    .poiId(review.getPoiId())
                    .img(review.getImgUrl())
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

        List<String> list = new ArrayList<>();
        for(MultipartFile file : command.getFile())
            list.add(imageUtil.uploadImageToS3(file,"review", UUID.randomUUID().toString().replace("-", "")+command.getPoiId()).toString());

        Review review = Review.createToReview(
                member,
                command.getContent(),
                command.getFeedback(),
                command.getPoiId(),
                list
        );

        reviewRepository.save(review);

        AddReviewResponseCommand responseCommand = AddReviewResponseCommand.builder()
                .response("success")
                .build();

        return AddReviewResponse.builder().response(responseCommand).build();
    }

    public ModifyReviewResponse modifyReview(ModifyReviewRequestCommand command){
        Review review = reviewRepository.findById(command.getReviewId()).orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        List<String> list = new ArrayList<>();
        for (MultipartFile file : command.getImg())
            list.add(imageUtil.uploadImageToS3(file, "review", UUID.randomUUID().toString().replace("-", "") + reviewRepository.findById(command.getReviewId()).get().getPoiId()).toString());
        review.modifyReview(
                command.getContent(),
                command.getFeedback(),
                list
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
