package com.fullship.hBAF.domain.review.repository;

import com.fullship.hBAF.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    public List<Review> findAllByPoiId(String poiId);
    public List<Review> findAllByMemberId(long memberId);

}
