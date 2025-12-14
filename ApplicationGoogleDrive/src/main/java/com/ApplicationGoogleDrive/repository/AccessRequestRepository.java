package com.ApplicationGoogleDrive.repository;

import com.ApplicationGoogleDrive.model.AccessRequest;
import com.ApplicationGoogleDrive.model.User;
import com.ApplicationGoogleDrive.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByRequester(User requester);
    List<AccessRequest> findByOwner(User owner);
    List<AccessRequest> findByFile(File file);
    Optional<AccessRequest> findByRequesterAndFile(User requester, File file);
    List<AccessRequest> findByOwnerAndStatus(User owner, AccessRequest.RequestStatus status);
}