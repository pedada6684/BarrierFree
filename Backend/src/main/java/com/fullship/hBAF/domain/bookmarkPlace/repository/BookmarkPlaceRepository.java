package com.fullship.hBAF.domain.bookmarkPlace.repository;

import com.fullship.hBAF.domain.bookmarkPlace.entity.BookmarkPlace;
import com.fullship.hBAF.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkPlaceRepository extends JpaRepository<BookmarkPlace, Long> {

    public BookmarkPlace findBookmarkPlaceByMemberAndPoiId(Member member, String poiId);

}
