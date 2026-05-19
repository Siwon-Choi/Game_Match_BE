package com.example.game_match;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// SpringBootApplication은 아래 3개의 묶은 어노테이션이다.
// SpringBootConfiguration = 이 클래스는 설정 클래스 (bean 등록 등등)
// EnableAutoConfiguration = 자동 설정을 켜라 ( ex: tomcat 설정, dispatcherServlet 등록, Controller 매핑, JSON 변환기, 예외처리 등등)
// ComponentScan = 현재 패키지 아래를 Component Scan 해라 (Spring bean으로 등록할 클래스를 찾음)
// 이 3개의 결합체이다.
public class GameMatchApplication {
    public static void main(String[] args) {
        // SpringApplication 객체 생성
        // ApplicationContext 생성
        // @SpringBootApplication 해석 -> 3개 실행 (Spring 컨테이너 생성, bean 등록 등등)
        // 내장 Tomcat 실행
        // DispatcherServlet 등록
        // 요청 받을 준비까지 완료
        SpringApplication.run(GameMatchApplication.class, args);
    }
}
