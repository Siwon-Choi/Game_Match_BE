package com.hd.gamematch.game.domain;

import java.util.Objects;

public class Game {
    private final GameId id;
    private final GameName name;
    private final GameSort sort;
    private final String url;

    // requireNonNull을 쓰기 위해서 직접 생성자 작성
    // @RequireArgsConstructor을 안 썼다.
    private Game(GameId id, GameName name, GameSort sort, String url){
        // null이면 바로 예외를 터뜨리도록 설계 -> NullPointerException(NPE)
        this.id = Objects.requireNonNull(id, "해당 게임의 id가 존재하지 않습니다.");
        this.name = Objects.requireNonNull(name, "해당 게임의 name이 존재하지 않습니다.");
        this.sort = Objects.requireNonNull(sort, "해당 게임의 sort가 존재하지 않습니다.");
        this.url = url;
    }

    // 정적 팩토리 메서드
    public static Game of(Long id, String name, String sort, String url){
        return new Game(
                new GameId(id),
                new GameName(name),
                new GameSort(sort),
                url
        );
    }

    public Long id() {
        return id.value();
    }

    public String name(){
        return name.value();
    }

    public String sort() {
        return sort.value();
    }

    public String url() {
        return url;
    }
}
