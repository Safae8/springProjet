package com.ApplicationGoogleDrive.service;

import com.ApplicationGoogleDrive.model.AccessRequest;
import com.ApplicationGoogleDrive.model.File;
import com.ApplicationGoogleDrive.model.User;
import com.ApplicationGoogleDrive.repository.AccessRequestRepository;
import com.ApplicationGoogleDrive.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessRequestService {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private FileRepository fileRepository;

    public AccessRequest createRequest(User requester, Long fileId, String message) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Vérifier si l'utilisateur n'est pas le propriétaire
        if (file.getOwner().getId().equals(requester.getId())) {
            throw new RuntimeException("You cannot request access to your own file");
        }

        // Vérifier si une demande existe déjà
        accessRequestRepository.findByRequesterAndFile(requester, file)
                .ifPresent(request -> {
                    throw new RuntimeException("You already have a pending request for this file");
                });

        AccessRequest request = new AccessRequest();
        request.setRequester(requester);
        request.setFile(file);
        request.setOwner(file.getOwner());
        request.setMessage(message);
        request.setStatus(AccessRequest.RequestStatus.PENDING);

        return accessRequestRepository.save(request);
    }

    public List<AccessRequest> getRequestsByOwner(User owner) {
        return accessRequestRepository.findByOwner(owner);
    }

    public List<AccessRequest> getRequestsByRequester(User requester) {
        return accessRequestRepository.findByRequester(requester);
    }

    public AccessRequest updateRequestStatus(Long requestId, User owner, AccessRequest.RequestStatus status) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Vérifier si l'utilisateur est bien le propriétaire
        if (!request.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You are not authorized to update this request");
        }

        request.setStatus(status);
        request.setRespondedAt(LocalDateTime.now());

        return accessRequestRepository.save(request);
    }

    public void deleteRequest(Long requestId, User user) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Vérifier si l'utilisateur est le demandeur ou le propriétaire
        if (!request.getRequester().getId().equals(user.getId()) &&
                !request.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this request");
        }

        accessRequestRepository.delete(request);
    }
}