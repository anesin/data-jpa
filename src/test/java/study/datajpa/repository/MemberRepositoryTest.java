package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Transactional
//@Rollback(false)
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TeamRepository teamRepository;


  @Test
  void testMember() {
    Member member = new Member("memberA");
    Member saveMember = memberRepository.save(member);

    Member findMember = memberRepository.findById(saveMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }


  @Test
  void testCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
    Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());

    List<Member> members = memberRepository.findAll();
    Assertions.assertThat(members.size()).isEqualTo(2);

    long count = memberRepository.count();
    Assertions.assertThat(count).isEqualTo(2);

    memberRepository.delete(member1);
    memberRepository.delete(member2);
    long deletedCount = memberRepository.count();
    Assertions.assertThat(deletedCount).isEqualTo(0);
  }


  @Test
  void findByUsernameAndAgeGreaterThan() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("AAA", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
    Assertions.assertThat(members.size()).isEqualTo(1);
    Assertions.assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    Assertions.assertThat(members.get(0).getAge()).isEqualTo(20);
  }


  @Test
  void testNamedQuery() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> members = memberRepository.findByUsername("AAA");
    Assertions.assertThat(members.get(0)).isEqualTo(member1);
  }


  @Test
  void findMember() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> members = memberRepository.findMember("AAA", 10);
    Assertions.assertThat(members.get(0)).isEqualTo(member1);
  }


  @Test
  void findUsernameList() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    memberRepository.findUsernameList().forEach(s -> {
      System.out.println("username = " + s);
    });
  }


  @Test
  void findMemberDto() {
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member member1 = new Member("AAA", 10, team);
    memberRepository.save(member1);

    memberRepository.findMemberDto().forEach(m -> {
      System.out.println("MemberDto = " + m);
    });
  }


  @Test
  void findByNames() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    memberRepository.findByNames(List.of("AAA", "BBB"))
                    .forEach(m -> System.out.println("Member = " + m));
  }


  @Test
  void returnType() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    Member member3 = new Member("AAA", 30);
    memberRepository.save(member1);
    memberRepository.save(member2);
    memberRepository.save(member3);

    System.out.println("======= return type list =======");
    memberRepository.findMembersByUsername("AAA")
                    .forEach(m -> System.out.println("List Member = " + m));
    memberRepository.findMembersByUsername("ABC")
                    .forEach(m -> System.out.println("List Member = " + m));

    System.out.println("======= return type one member =======");
    Member findMember1 = memberRepository.findMemberByUsername("BBB");
    System.out.println("findMember1 = " + findMember1);
    Member findMember2 = memberRepository.findMemberByUsername("ABC");
    System.out.println("findMember2 = " + findMember2);
    try {
      Member findMember3 = memberRepository.findMemberByUsername("AAA");
      System.out.println("findMember3 = " + findMember3);
    }
    catch (IncorrectResultSizeDataAccessException e) {  // NonUniqueResultException
      e.printStackTrace();
    }

    System.out.println("======= return type optional member =======");
    memberRepository.findOptionalMemberByUsername("BBB")
                    .ifPresent(m -> System.out.println("OptionalMember = " + m));
    memberRepository.findOptionalMemberByUsername("ABC")
                    .ifPresent(m -> System.out.println("OptionalMember = " + m));
    try {
      memberRepository.findOptionalMemberByUsername("AAA")
                      .ifPresent(m -> System.out.println("OptionalMember = " + m));
    }
    catch (IncorrectResultSizeDataAccessException e) {  // NonUniqueResultException
      e.printStackTrace();
    }
  }

}