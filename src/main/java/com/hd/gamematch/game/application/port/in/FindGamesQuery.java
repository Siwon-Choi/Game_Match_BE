package com.hd.gamematch.game.application.port.in;



// FindGamesQuery 장점
//- 조회 조건을 하나로 묶음
//- 조건이 늘어나도 UseCase 메서드 변경이 적음
//- 검증/trim/null 처리 위치를 만들 수 있음
//- HTTP 파라미터와 application input을 분리함
//- 테스트할 때 입력 의도가 명확해짐
public record FindGamesQuery(String name, String sort) {

    public FindGamesQuery {
        name = normalize(name);
        sort = normalize(sort);
    }

    public static FindGamesQuery of(String name, String sort) {
        return new FindGamesQuery(name, sort);
    }

    public boolean hasName(){
        return name != null;
    }

    public boolean hasSort(){
        return sort != null;
    }

    private static String normalize(String value){
        if(value == null || value.isBlank()){
            return null;
        }
        return value.trim();
    }

}
