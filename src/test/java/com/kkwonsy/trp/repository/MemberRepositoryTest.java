package com.kkwonsy.trp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.kkwonsy.trp.entity.Member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void simpleTest() {
        // given
        Member kwon = Member.builder().name("kwon").build();

        // when
        Member save = memberRepository.save(kwon);
        Member found = memberRepository.findById(save.getId()).orElseGet(null);

        // then
        assertNotNull(save);
        assertNotNull(found);
        assertEquals(kwon.getName(), found.getName());
    }

}