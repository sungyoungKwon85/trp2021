package com.kkwonsy.trp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.exception.ErrorCode;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.MemberSaveRequest;
import com.kkwonsy.trp.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findMemberOrThrow(Long memberId) throws TrpException {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new TrpException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Long saveMember(MemberSaveRequest request) {
        Member member = memberRepository.save(Member.builder().name(request.getName()).build());
        return member.getId();
    }
}
