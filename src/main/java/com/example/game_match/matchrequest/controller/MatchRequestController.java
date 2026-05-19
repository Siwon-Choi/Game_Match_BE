package com.example.game_match.matchrequest.controller;

import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import com.example.game_match.matchrequest.dto.MatchRequestAppliedSummaryResponseDto;
import com.example.game_match.matchrequest.dto.MatchRequestApplyRequestDto;
import com.example.game_match.matchrequest.dto.MatchRequestDetailResponseDto;
import com.example.game_match.matchrequest.service.MatchRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchRequestController {
    private final MatchRequestService matchRequestService;

    // 친선 경기 신청을 생성한다.
    // 신청자 gameUserId와 팀원 목록을 요청 body로 받고, 생성된 requestId를 반환한다.
    @PostMapping("/friendly-matches/{matchId}/requests")
    public ResponseEntity<CommonResponse<Integer>> createMatchRequest(
            @PathVariable Integer matchId,
            @RequestBody MatchRequestApplyRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(matchRequestService.applyMatchAndRegisterTeam(
                        requestDto.toServiceDto(matchId)
                )));
    }

    // 특정 친선 경기의 활성 신청 수를 조회한다.
    // await 또는 approve 상태인 신청만 활성 신청으로 센다.
    @GetMapping("/friendly-matches/{matchId}/requests/count")
    public ResponseEntity<CommonResponse<Long>> countActiveRequests(@PathVariable Integer matchId) {
        return ResponseEntity.ok(CommonResponse.success(matchRequestService.countActiveRequestsByMatchId(matchId)));
    }

    // 호스트가 특정 친선 경기에 들어온 신청 목록을 조회한다.
    // hostGameUserId로 해당 경기의 호스트 권한을 확인한다.
    @GetMapping("/friendly-matches/{matchId}/requests")
    public ResponseEntity<CommonResponse<List<MatchRequestDetailResponseDto>>> getRequestsByMatchId(
            @PathVariable Integer matchId,
            @RequestParam Integer hostGameUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchRequestService.getRequestsByMatchId(matchId, hostGameUserId)
        ));
    }

    // 사용자가 이미 같은 경기나 같은 시간대의 다른 경기에 신청했는지 확인한다.
    @GetMapping("/friendly-matches/{matchId}/requests/conflict-check")
    public ResponseEntity<CommonResponse<String>> checkRequestParticipationConflict(
            @PathVariable Integer matchId,
            @RequestParam Integer userId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchRequestService.checkRequestParticipationConflict(userId, matchId)
        ));
    }

    // 특정 게임 유저가 보낸 친선 경기 신청 목록을 조회한다.
    @GetMapping("/game-users/{gameUserId}/match-requests")
    public ResponseEntity<CommonResponse<List<MatchRequestAppliedSummaryResponseDto>>> getRequestsByGameUserId(
            @PathVariable Integer gameUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(matchRequestService.getRequestsByGameUserId(gameUserId)));
    }

    // requestId로 친선 경기 신청 상세 정보를 조회한다.
    @GetMapping("/match-requests/{requestId}")
    public ResponseEntity<CommonResponse<MatchRequestDetailResponseDto>> getRequestDetail(
            @PathVariable Integer requestId
    ) {
        return ResponseEntity.ok(CommonResponse.success(matchRequestService.getRequestDetail(requestId)));
    }

    // requestId에 연결된 친선 경기 ID를 조회한다.
    @GetMapping("/match-requests/{requestId}/match-id")
    public ResponseEntity<CommonResponse<Integer>> getFriendlyMatchIdByRequestId(@PathVariable Integer requestId) {
        return ResponseEntity.ok(CommonResponse.success(
                matchRequestService.getFriendlyMatchIdByRequestId(requestId)
        ));
    }

    // requestId의 현재 신청 상태를 조회한다.
    @GetMapping("/match-requests/{requestId}/status")
    public ResponseEntity<CommonResponse<MatchRequestStatus>> getRequestStatus(@PathVariable Integer requestId) {
        return ResponseEntity.ok(CommonResponse.success(matchRequestService.getRequestStatusByRequestId(requestId)));
    }

    // 호스트가 친선 경기 신청 상태를 승인 또는 거절로 변경한다.
    @PatchMapping("/match-requests/{requestId}/status")
    public ResponseEntity<CommonResponse<MatchRequestDetailResponseDto>> updateRequestStatus(
            @PathVariable Integer requestId,
            @RequestParam Integer hostGameUserId,
            @RequestParam MatchRequestStatus status
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchRequestService.updateRequestStatus(requestId, hostGameUserId, status)
        ));
    }

    // 신청자가 본인의 친선 경기 신청을 취소한다.
    @PatchMapping("/match-requests/{requestId}/cancel")
    public ResponseEntity<CommonResponse<MatchRequestDetailResponseDto>> cancelRequest(
            @PathVariable Integer requestId,
            @RequestParam Integer requesterGameUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchRequestService.cancelRequest(requestId, requesterGameUserId)
        ));
    }
}
