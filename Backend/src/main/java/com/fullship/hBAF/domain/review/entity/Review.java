package com.fullship.hBAF.domain.review.entity;

import com.fullship.hBAF.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String content;

    private Long like;

    private LocalDateTime regDate;

    private LocalDateTime modifyDate;

    private Long status;

    private String poiId;

    public static Review createToReview(
            Member member,
            String content,
            Long like,
            String poiId
    ){
        Review review = new Review();
        review.member = member;
        review.content = content;
        review.like = like;
        review.regDate = LocalDateTime.now();
        review.modifyDate = LocalDateTime.now();
        review.status = 0L;
        review.poiId = poiId;

        return review;
    }

    public void modifyReview(
            String content,
            Long like
    ){
        if(content!=null)
            this.content=content;
        if(like!=null)
            this.like=like;
        this.modifyDate = LocalDateTime.now();
    }

}
