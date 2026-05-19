package com.example.game_match.matchparticipation.controller;

import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.matchparticipation.dto.MatchParticipationCreateRequestDto;
import com.example.game_match.matchparticipation.dto.MatchParticipationResponseDto;
import com.example.game_match.matchparticipation.service.MatchParticipationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friendly-matches/{matchId}/participations")
public class MatchParticipationController {
    private final MatchParticipationService matchParticipationService;

    // 특정 친선 경기에 참여자를 등록한다.
    // matchId는 URL 경로에서 받고, gameUserId와 role은 요청 body에서 받는다.
    @PostMapping
    public ResponseEntity<CommonResponse<MatchParticipationResponseDto>> createMatchParticipation(
            @PathVariable Integer matchId,
            @RequestBody MatchParticipationCreateRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(
                        matchParticipationService.createMatchParticipation(requestDto.toServiceDto(matchId))
                ));
    }

    // 특정 친선 경기에 등록된 참여자 목록을 조회한다.
    // 승인된 신청이 있으면 전체 roster를 반환하고, 없으면 host 역할 참여자만 반환한다.
    @GetMapping
    public ResponseEntity<CommonResponse<List<MatchParticipationResponseDto>>> getParticipationsByMatchId(
            @PathVariable Integer matchId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchParticipationService.getParticipationsByMatchId(matchId)
        ));
    }

    // 특정 친선 경기의 host 참여자들이 모두 같은 그룹에 속해 있으면 해당 그룹명을 반환한다.
    // 같은 그룹이 아니거나 그룹이 없으면 null을 반환한다.
    @GetMapping("/host-group")
    public ResponseEntity<CommonResponse<String>> getHostGroupName(@PathVariable Integer matchId) {
        return ResponseEntity.ok(CommonResponse.success(matchParticipationService.getHostGroupName(matchId)));
    }

    // 특정 사용자를 현재 친선 경기 roster에 추가할 수 있는지 확인한다.
    // 이미 roster에 있거나 같은 시간대에 다른 친선 경기가 있으면 예외가 발생한다.
    @GetMapping("/conflict-check")
    public ResponseEntity<CommonResponse<String>> checkRosterConflict(
            @PathVariable Integer matchId,
            @RequestParam Integer userId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                matchParticipationService.checkRosterConflict(userId, matchId)
        ));
    }
}
