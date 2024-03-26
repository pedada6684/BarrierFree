package com.fullship.hBAF.domain.review.entity;

import com.fullship.hBAF.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String content;

    private Long feedback;

    private LocalDateTime regDate;

    private LocalDateTime modifyDate;

    private Long status;

    private String poiId;

    @ElementCollection
    @Column(length = 3000)
    private List<String> imgUrl;

    public static Review createToReview(
            Member member,
            String content,
            Long feedback,
            String poiId,
            List<String> list
    ){
        Review review = new Review();
        review.member = member;
        review.content = content;
        review.feedback = feedback;
        review.regDate = LocalDateTime.now();
        review.modifyDate = LocalDateTime.now();
        review.status = 0L;
        review.poiId = poiId;
        review.imgUrl = list;

        return review;
    }

    public void modifyReview(
            String content,
            Long feedback,
            List<String> img
    ){
        if(content!=null)
            this.content=content;
        if(feedback!=null)
            this.feedback=feedback;
        if(img!=null)
            this.imgUrl=img;
        this.modifyDate = LocalDateTime.now();
    }

}
