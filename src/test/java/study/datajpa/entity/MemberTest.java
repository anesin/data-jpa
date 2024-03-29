package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberTest {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private MemberRepository memberRepository;


  @Test
  void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    em.flush();

    List<Member> members = em.createQuery("select m from Member m", Member.class)
                             .getResultList();
    members.forEach(m -> {
      System.out.println("member: " + m);
      System.out.println("-> member.team: " + m.getTeam());
    });
  }


  @Test
  void JpaEventBaseEntity() throws Exception {
    Member member = new Member("member1");
    memberRepository.save(member);

    Thread.sleep(100);
    member.setUsername("member2");
    em.flush();
    em.clear();

    Member findMember = memberRepository.findById(member.getId()).get();
    System.out.println("member.updatedDate = " + findMember.getCreatedDate());
    System.out.println("member.lastModifiedDate = " + findMember.getLastModifiedDate());
    System.out.println("member.updatedBy = " + findMember.getCreatedBy());
    System.out.println("member.lastModifiedBy = " + findMember.getLastModifiedBy());
  }

}