package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

  @PersistenceContext
  EntityManager em;


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


  @Test
  void paging() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;
    int limit = 3;

    Sort sort = Sort.by(Sort.Direction.DESC, "username");
    PageRequest pageRequest = PageRequest.of(0, limit, sort);
    Page<Member> page = memberRepository.findByAge(age, pageRequest);
    assertThat(page.getContent().size()).isEqualTo(limit);
    assertThat(page.getContent().get(0).getUsername()).isEqualTo("member5");
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();

    Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
  }


  @Test
  void slicing() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;
    int limit = 3;

    Sort sort = Sort.by(Sort.Direction.DESC, "username");
    PageRequest pageRequest = PageRequest.of(0, limit, sort);
    Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
    assertThat(slice.getContent().size()).isEqualTo(limit);
    assertThat(slice.getContent().get(0).getUsername()).isEqualTo("member5");
    assertThat(slice.getNumber()).isEqualTo(0);
    assertThat(slice.isFirst()).isTrue();
    assertThat(slice.hasNext()).isTrue();
  }


  @Test
  void listing() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;
    int limit = 3;

    Sort sort = Sort.by(Sort.Direction.DESC, "username");
    PageRequest pageRequest = PageRequest.of(0, limit, sort);
    List<Member> list = memberRepository.findListByAge(age, pageRequest);
    assertThat(list.size()).isEqualTo(limit);
    assertThat(list.get(0).getUsername()).isEqualTo("member5");
  }


  @Test
  void bulkUpdate() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 15));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));

    int resultCount = memberRepository.bulkAgePlus(20);
    Assertions.assertThat(resultCount).isEqualTo(3);

    List<Member> members = memberRepository.findByUsername("member3");
    Assertions.assertThat(members.get(0).getAge()).isEqualTo(21);
  }


  @Test
  void findMemberLazy() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    memberRepository.save(new Member("member1", 10, teamA));
    memberRepository.save(new Member("member1", 10, teamB));
    memberRepository.save(new Member("member2", 10, teamB));

    em.flush();
    em.clear();

//    memberRepository.findAll().forEach(this::printMember);
//    memberRepository.findFetchJoinAll().forEach(this::printMember);
//    memberRepository.findMemberEntityGraphAll().forEach(this::printMember);
    memberRepository.findMemberNamedEntityGraphAll().forEach(this::printMember);
//    memberRepository.findMemberEntityGraphByUsername("member1").forEach(this::printMember);
  }


  private void printMember(Member member) {
    System.out.println("member = " + member);
    System.out.println("member.teamClass = " + member.getTeam().getClass());
    System.out.println("member.team = " + member.getTeam().getName());
  }


  @Test
  void queryHint() {
    String name = "member1";
    Member member = new Member(name, 10);
    memberRepository.save(member);
    em.flush();
    em.clear();

//    Member findMember = memberRepository.findById(member.getId()).get();
    Member findMember = memberRepository.findReadOnlyByUsername(name).get(0);
//    Member findMember = memberRepository.findLockByUsername(name).get(0);
    findMember.setUsername("member2");

    em.flush();
  }


  @Test
  void customQuery() {
    memberRepository.findMemberCustom();
  }


  @Test
  public void specBasic() {
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
    List<Member> result = memberRepository.findAll(spec);

    assertThat(result.size()).isEqualTo(1);
  }


  @Test
  public void queryByExample() {
    given();

    String name = "m1";
    Member member = new Member(name);
    var result1 = memberRepository.findAll(Example.of(member));
    assertThat(result1.size()).isEqualTo(1);
    assertThat(result1.get(0).getUsername()).isEqualTo(name);

    ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
    var result2 = memberRepository.findAll(Example.of(member, matcher));
    assertThat(result2.size()).isEqualTo(2);

    Team team = new Team("teamA");
    member.setTeam(team);
    var result3 = memberRepository.findAll(Example.of(member, matcher));
    assertThat(result3.size()).isEqualTo(1);
  }


  private void given() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m1", 10, teamB);
    Member m3 = new Member("m2", 20, teamA);
    em.persist(m1);
    em.persist(m2);
    em.persist(m3);

    em.flush();
    em.clear();
  }


  @Test
  public void projections() {
    given();

    String name = "m1";
//    var result = memberRepository.findProjectionsByUsername(name);
    var result = memberRepository.findProjectionsByUsername(name, UsernameOnly.class);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getUsername()).isEqualTo(name);
  }


  @Test
  public void OpenProjections() {
    given();

    String name = "m1";
//    var result = memberRepository.findOpenProjectionsByUsername(name);
    var result = memberRepository.findProjectionsByUsername(name, UsernameOnlyOpen.class);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getUsername()).isEqualTo(name + " 0 teamA");
  }


  @Test
  public void ClassProjections() {
    given();

    String name = "m1";
    var result = memberRepository.findProjectionsByUsername(name, UsernameOnlyDto.class);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getUsername()).isEqualTo(name);
  }


  @Test
  public void nestedProjections() {
    given();

    String name = "m1";
    var result = memberRepository.findProjectionsByUsername(name, NestedClosedProjection.class);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).getUsername()).isEqualTo(name);
  }


  @Test
  public void nativeQuery() {
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    String name = "m1";
    var result = memberRepository.findByNativeQuery(name);
    assertThat(result.getUsername()).isEqualTo(name);
  }


  @Test
  public void nativeProjection() {
    given();

    var result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
    List<MemberProjection> content = result.getContent();
    for (var m : content) {
      String mp = String.format("MemberProjection = id: %d, username = %s, teamName = %s",
                                m.getId(), m.getUsername(), m.getTeamName());
      System.out.println(mp);
    }
  }

}