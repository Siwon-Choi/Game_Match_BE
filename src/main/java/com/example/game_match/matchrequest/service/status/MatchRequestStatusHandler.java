package com.example.game_match.matchrequest.service.status;

import com.example.game_match.matchrequest.domain.MatchRequest;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;

public interface MatchRequestStatusHandler {
    boolean supports(MatchRequest request, MatchRequestStatus targetStatus);

    void handle(MatchRequest request, MatchRequestStatusOperations operations);
}
