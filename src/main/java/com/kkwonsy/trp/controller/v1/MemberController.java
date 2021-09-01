package com.kkwonsy.trp.controller.v1;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.IdResponse;
import com.kkwonsy.trp.model.MemberSaveRequest;
import com.kkwonsy.trp.model.ObjectResponse;
import com.kkwonsy.trp.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1")
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/members")
    public ResponseEntity saveCity(@Valid @RequestBody MemberSaveRequest request) throws TrpException {
        Long savedId = memberService.saveMember(request);
        return new ResponseEntity(new ObjectResponse(new IdResponse(savedId)), HttpStatus.OK);
    }
}
