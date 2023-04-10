package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Member;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
//@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  MemberJpaRepository memberJpaRepository;


  @Test
  void testMember() {
    Member member = new Member("memberA");
    Member saveMember = memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.findById(saveMember.getId());

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

}