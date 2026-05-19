package com.example.game_match.matchrequest.service.status;

import com.example.game_match.matchrequest.domain.MatchRequest;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import org.springframework.stereotype.Component;

@Component
public class RevokeApprovedMatchRequestStatusHandler implements MatchRequestStatusHandler {
    @Override
    public boolean supports(MatchRequest request, MatchRequestStatus targetStatus) {
        return targetStatus == MatchRequestStatus.reject
                && request.getStatus() == MatchRequestStatus.approve;
    }

    @Override
    public void handle(MatchRequest request, MatchRequestStatusOperations operations) {
        operations.revokeApprovedRequest(request);
    }
}
