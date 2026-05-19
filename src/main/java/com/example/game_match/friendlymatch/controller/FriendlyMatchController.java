package com.example.game_match.friendlymatch.controller;

import com.example.game_match.friendlymatch.dto.FriendlyMatchCreateRequestDto;
import com.example.game_match.friendlymatch.dto.FriendlyMatchHostedSummaryResponseDto;
import com.example.game_match.friendlymatch.dto.FriendlyMatchResponseDto;
import com.example.game_match.friendlymatch.service.FriendlyMatchService;
import com.example.game_match.global.response.CommonResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/friendly-matches")
public class FriendlyMatchController {
    private final FriendlyMatchService friendlyMatchService;

    // 친선 경기 모집글을 새로 생성한다.
    @PostMapping
    public ResponseEntity<CommonResponse<FriendlyMatchResponseDto>> createFriendlyMatch(
            @RequestBody FriendlyMatchCreateRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(friendlyMatchService.createFriendlyMatch(requestDto.toServiceDto())));
    }

    // 날짜 목록, 시간 목록, 게임 이름을 기준으로 친선 경기를 검색한다.
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<List<FriendlyMatchResponseDto>>> searchFriendlyMatches(
            @RequestParam(value = "dates") @DateTimeFormat(pattern = "yyyy-MM-dd") List<LocalDate> dates,
            @RequestParam(value = "times") List<String> times,
            @RequestParam(value = "gameName") String gameName
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                friendlyMatchService.searchByDatesTimesAndGameName(dates, times, gameName)
        ));
    }

    // matchId로 친선 경기 상세 정보를 조회한다.
    // 조회 시 경기 시간이 지났거나 승인된 신청이 있으면 모집 상태를 갱신한 뒤 응답한다.
    @GetMapping("/{matchId}")
    public ResponseEntity<CommonResponse<FriendlyMatchResponseDto>> getMatchById(@PathVariable Integer matchId) {
        return ResponseEntity.ok(CommonResponse.success(friendlyMatchService.getMatchById(matchId)));
    }

    // 특정 게임 유저가 호스트로 만든 친선 경기 목록을 조회한다.
    // 각 경기마다 신청 수, 대기 신청 수, 승인 신청 수, 최근 신청 변경 시각을 함께 반환한다.
    @GetMapping("/hosted")
    public ResponseEntity<CommonResponse<List<FriendlyMatchHostedSummaryResponseDto>>> getHostedMatches(
            @RequestParam Integer gameUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(friendlyMatchService.getHostedMatches(gameUserId)));
    }
}
