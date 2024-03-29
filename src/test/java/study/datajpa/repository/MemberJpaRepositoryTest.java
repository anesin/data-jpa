package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
//@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @PersistenceContext
  EntityManager em;


  @Test
  void testMember() {
    Member member = new Member("memberA");
    Member saveMember = memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.find(saveMember.getId());

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }


  @Test
  void testCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
    Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
    assertThat(findMember1.getId()).isEqualTo(member1.getId());
    assertThat(findMember2.getId()).isEqualTo(member2.getId());

    List<Member> members = memberJpaRepository.findAll();
    assertThat(members.size()).isEqualTo(2);

    long count = memberJpaRepository.count();
    assertThat(count).isEqualTo(2);

    memberJpaRepository.delete(member1);
    memberJpaRepository.delete(member2);
    long deletedCount = memberJpaRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }


  @Test
  void findByUsernameAndAgeGreaterThan() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("AAA", 20);
    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
    assertThat(members.size()).isEqualTo(1);
    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(20);
  }


  @Test
  void testNamedQuery() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    List<Member> members = memberJpaRepository.findByUsername("AAA");
    assertThat(members.get(0)).isEqualTo(member1);
  }


  @Test
  void paging() {
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 10));
    memberJpaRepository.save(new Member("member3", 10));
    memberJpaRepository.save(new Member("member4", 10));
    memberJpaRepository.save(new Member("member5", 10));

    int age = 10;
    int offset = 0;
    int limit = 3;

    List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
    long totalCount = memberJpaRepository.totalCount(age);
    assertThat(members.size()).isEqualTo(limit);
    assertThat(members.get(0).getUsername()).isEqualTo("member5");
    assertThat(totalCount).isEqualTo(5);
  }


  @Test
  void bulkUpdate() {
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 15));
    memberJpaRepository.save(new Member("member3", 20));
    memberJpaRepository.save(new Member("member4", 21));
    memberJpaRepository.save(new Member("member5", 40));

    int resultCount = memberJpaRepository.bulkAgePlus(20);
    assertThat(resultCount).isEqualTo(3);

    em.clear();

    List<Member> members = memberJpaRepository.findByUsername("member3");
    assertThat(members.get(0).getAge()).isEqualTo(21);
  }

}