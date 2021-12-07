package kr.pe.playdata.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.pe.playdata.config.security.JwtLoginToken.JwtTokenProvider;
import kr.pe.playdata.model.domain.Member;
import kr.pe.playdata.model.dto.MemberDTO;
import kr.pe.playdata.model.dto.ResponseDTO;
import kr.pe.playdata.repository.MemberRepository;
import kr.pe.playdata.service.MemberService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000") // CORS때문에 교차출처에 표시
@RequiredArgsConstructor
@RestController
public class MemberController {
	
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepo;
	private final MemberService memberService;
	
	// 회원 조회 - memberIdx
	@GetMapping("/members/idx/{memberIdx}")
	public ResponseDTO.MemberResponse getMemberByIdx(@PathVariable Long memberIdx) {
		return memberService.findByMemberIdx(memberIdx);
	}
	
	// 밑에 nickname 일부 검색해서 조회하는 메서드 있으면 이 메서드는 삭제해도 될 듯...? - service & repository도 같이 삭제하기
	// 회원 조회 - nickname
	@GetMapping("/members/nickname/{nickname}")
	public ResponseDTO.MemberResponse getMemberByNickname(@PathVariable String nickname) {
		return memberService.findByNickname(nickname);
	}
	
	// 회원 조회 - nickname 일부
	@GetMapping("/members/nicknamecon/{nickname}")
	public List<ResponseDTO.MemberListResponse> getMemberByNicknameCon(@PathVariable String nickname) {
		return memberService.findByNicknameContaining(nickname);
	}
	
	// 회원 전체 조회
	@GetMapping("/members/all")
	public List<ResponseDTO.MemberListResponse> getMemberAll() {
		return memberService.findMemberAll();
	}
	
	// 회원 저장
	@PostMapping("/member")
	public Long saveMember(@RequestBody MemberDTO.Create dto) {
		return memberService.saveMember(dto);
	}
	
	// 회원 정보 수정 - pw, email, region
	@PutMapping("members/{memberIdx}")
    public Long updateMember(@PathVariable Long memberIdx, @RequestBody MemberDTO.Update dto) {
        return memberService.updateMember(memberIdx, dto);
    }
	
	// 회원 탈퇴
	@PatchMapping("/members/{memberIdx}")
    public Long deleteMember(@PathVariable Long memberIdx) {
		memberService.deleteMember(memberIdx);
		
        return memberIdx;
    }
	
	// 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        Member member = memberRepo.findByMemberId(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        List<String> roles = new ArrayList<>();
		roles.add(member.getRole());
        return jwtTokenProvider.createToken(member.getUsername(), roles );
    }
	
}
