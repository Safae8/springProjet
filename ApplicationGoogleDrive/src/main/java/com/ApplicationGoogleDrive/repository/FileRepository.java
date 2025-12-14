package com.ApplicationGoogleDrive.repository;


import com.ApplicationGoogleDrive.model.File;
import com.ApplicationGoogleDrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwner(User owner);
    List<File> findByIsPublicTrue();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM File f " +
            "LEFT JOIN AccessRequest ar ON f.id = ar.file.id " +
            "WHERE f.id = :fileId " +
            "AND (f.owner.id = :userId " +
            "OR f.isPublic = true " +
            "OR (ar.requester.id = :userId AND ar.status = 'APPROVED'))")
    boolean checkUserAccess(@Param("fileId") Long fileId, @Param("userId") Long userId);
}