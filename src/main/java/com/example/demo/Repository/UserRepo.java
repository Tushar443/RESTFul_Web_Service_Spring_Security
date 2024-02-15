package com.example.demo.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.ws.io.Entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    UserEntity findUserByEmail(String email);

    UserEntity findByUserId(String id);

    UserEntity findUserByEmailVerificationToken(String token);

    //Using Native Queries
    @Query(value = "select * from Users u where u.email_verification_status = 'true'", nativeQuery = true)
    Page<UserEntity> findAllUserWithConfirmedEmailAddress(Pageable pageable);

    @Query(value = "select * from Users u where u.first_name = ?1 and u.last_name = ?2", nativeQuery = true)
    List<UserEntity> findUserByFullName(String firstName, String lastName);

    @Query(value = "select * from Users u where u.last_name = :lName", nativeQuery = true)
    List<UserEntity> findUserByLastName(@Param("lName") String lastName);

    @Query(value = "select * from Users u where u.last_name Like %:key%", nativeQuery = true)
    List<UserEntity> findUsersByKey(@Param("key") String lastName);

    @Query(value = "select u.first_name,u.last_name from Users u where u.first_name = ?1 and u.last_name = ?2", nativeQuery = true)
    List<Object[]> findUserByLatNameAndFirstName(String firstName, String lastName);

    @Transactional
    @Modifying
    @Query(value = "update users u set u.EMAIL_VERIFICATION_STATUS=:email where u.user_id =:userId", nativeQuery = true)
    void updateUserEmailVerificationStatus(@Param("email") boolean emailVerify, @Param("userId") String userId);

    //Using Java Persistence Query Language JPQL

    @Query("select user from UserEntity user where user.userId=:userIdd")
    UserEntity findUserByUserId(@Param("userIdd") String userId);

    @Query(value = "select u.firstName,u.lastName from UserEntity u where u.firstName = ?1 and u.lastName = ?2")
    List<Object[]> findUserByLatNameAndFirstNameByJPQL(String firstName, String lastName);

    @Transactional
    @Modifying
    @Query(value = "update UserEntity u set u.emailVerificationStatus = :email where u.userId = :userId")
    void updateUserEmailVerificationStatusJPQL(@Param("email") boolean emailVerify, @Param("userId") String userId);

}
