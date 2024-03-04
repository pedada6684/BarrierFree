package com.fullship.hBAF.domain.member.repository;

import com.fullship.hBAF.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
