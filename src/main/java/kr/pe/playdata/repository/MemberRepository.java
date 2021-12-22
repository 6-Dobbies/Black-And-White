package kr.pe.playdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pe.playdata.model.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Member findByMemberIdx(Long memberIdx);

	Member findByMemberId(String memberId);

	Member findByNickname(String nickname);
	
	Member findByEmail(String email);
	
	List<Member> findByNicknameContaining(String nickname);
	
	List<Member> findByDel(int del);
	
}
