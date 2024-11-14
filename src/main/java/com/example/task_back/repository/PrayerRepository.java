package com.example.task_back.repository;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;


public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    List<Prayer> findByUserUsernameAndDeletedFalseOrderByIdDesc(String username);
    List<Prayer> findByUserUsernameAndDeletedFalseAndTimeOfPrayerBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
    Integer countByUserUsernameAndDeletedFalseAndTimeOfPrayerBetween(String username,LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            SELECT p FROM Prayer p
            WHERE p.id IN(
            SELECT MAX(p2.id)
            FROM Prayer p2
            WHERE p2.user.id IN :userIdList
            AND p2.isPublic = true
            AND p2.deleted = false
            GROUP BY p2.user.id
            )
            """)
    List<Prayer> findLatestPrayerForEachUser(@Param("userIdList") List<Long> userIdList);

    @Query("SELECT p FROM Prayer p where p.user.username = :username AND p.deleted =false AND p.keywords LIKE CONCAT('%\"', :keyword, '\"%')")
    List<Prayer> findByUser_UsernameAndKeywordsContaining(@Param("username")String username, @Param("keyword") String keyword);

    //전체 사용자의 기도문을 조회(pagination 기능 사용)
    Page<Prayer> findAllByOrderByTimeOfPrayerDesc(Pageable pageable);

    //특정 사용자의 기도문을 조회(pagination 기능 사용)
    Page<Prayer> findAllByUserUsernameOrderByTimeOfPrayerDesc(String id, Pageable pageable);

    Integer countByUserUsername(String id);

    @Query("""
            SELECT p FROM Prayer p
            WHERE p.user.id IN :userIdList
            AND p.isPublic = true
            AND p.deleted = false
            """)
    Integer countPrayerForEachUser(@Param("userIdList") List<Long> userIdList);


    @Query("""
            SELECT p FROM Prayer p
            WHERE p.user.id IN :userIdList
            AND p.isPublic = true
            AND p.deleted = false
            """)
    Page<Prayer> findPrayerForEachUser(@Param("userIdList") List<Long> userIdList, Pageable pageable);

    List<Prayer> findAllByUserUsername(String username);

    @Query("""
            SELECT p FROM Prayer p
            WHERE p.id IN :prayerIdList
            """)
    List<Prayer> findAllByIdIn(@Param("prayerIdList")List<Long> prayerIdList);
}

