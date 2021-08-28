package com.kkwonsy.trp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkwonsy.trp.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
