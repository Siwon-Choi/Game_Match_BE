CREATE DATABASE IF NOT EXISTS __DB_NAME__ CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE __DB_NAME__;
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `friendly_match_request_participation`;
DROP TABLE IF EXISTS `friendly_match_request`;
DROP TABLE IF EXISTS `friendly_match_participation`;
DROP TABLE IF EXISTS `friendly_match`;
DROP TABLE IF EXISTS `Comment`;
DROP TABLE IF EXISTS `Post_Recommendation_User`;
DROP TABLE IF EXISTS `image_post`;
DROP TABLE IF EXISTS `Post`;
DROP TABLE IF EXISTS `Friendship`;
DROP TABLE IF EXISTS `Refresh_Token`;
DROP TABLE IF EXISTS `game_user`;
DROP TABLE IF EXISTS `Group`;
DROP TABLE IF EXISTS `Game`;
DROP TABLE IF EXISTS `User`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `User` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Name` VARCHAR(255) NOT NULL,
    `Email` VARCHAR(255) NOT NULL UNIQUE,
    `Profile` VARCHAR(255),
    `Phone_Number` VARCHAR(20) NOT NULL UNIQUE,
    `Birth` DATE NOT NULL,
    `Login_Id` VARCHAR(255) NOT NULL UNIQUE,
    `Login_Password` VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Game` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Name` VARCHAR(255) NOT NULL UNIQUE,
    `sort` VARCHAR(255) NOT NULL,
    `url` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Group` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Game_Id` INT NOT NULL,
    `Name` VARCHAR(255) NOT NULL,
    CONSTRAINT `fk_group_game` FOREIGN KEY (`Game_Id`) REFERENCES `Game` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `game_user` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `User_Id` INT NOT NULL,
    `Game_Id` INT NOT NULL,
    `Group_Id` INT,
    `Nickname` VARCHAR(255) NOT NULL,
    UNIQUE KEY `unique_user_game` (`User_Id`, `Game_Id`),
    UNIQUE KEY `Nickname` (`Nickname`, `Game_Id`),
    KEY `idx_game_user_group` (`Group_Id`),
    CONSTRAINT `fk_game_user_user` FOREIGN KEY (`User_Id`) REFERENCES `User` (`Id`),
    CONSTRAINT `fk_game_user_game` FOREIGN KEY (`Game_Id`) REFERENCES `Game` (`Id`),
    CONSTRAINT `fk_game_user_group` FOREIGN KEY (`Group_Id`) REFERENCES `Group` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Refresh_Token` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `User_Id` INT NOT NULL UNIQUE,
    `Token_Hash` VARCHAR(128) NOT NULL,
    CONSTRAINT `fk_refresh_token_user` FOREIGN KEY (`User_Id`) REFERENCES `User` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Friendship` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Game_User_Id_1` INT NOT NULL,
    `Game_User_Id_2` INT NOT NULL,
    UNIQUE KEY `unique_friendship` (`Game_User_Id_1`, `Game_User_Id_2`),
    KEY `idx_game_user_id_1` (`Game_User_Id_1`),
    KEY `idx_game_user_id_2` (`Game_User_Id_2`),
    CONSTRAINT `fk_friendship_game_user_1` FOREIGN KEY (`Game_User_Id_1`) REFERENCES `game_user` (`Id`),
    CONSTRAINT `fk_friendship_game_user_2` FOREIGN KEY (`Game_User_Id_2`) REFERENCES `game_user` (`Id`),
    CONSTRAINT `chk_friendship_not_self` CHECK (`Game_User_Id_1` <> `Game_User_Id_2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Post` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(15) NOT NULL,
    `date` DATE NOT NULL,
    `views` INT,
    `recommendations` INT,
    `dislikes` INT,
    `content` VARCHAR(45),
    `user_id` INT NOT NULL,
    `game_id` INT NOT NULL,
    `time` TIME NOT NULL,
    `anonymous` BOOLEAN NOT NULL,
    KEY `idx_post_game` (`game_id`),
    KEY `idx_post_user` (`user_id`),
    CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `User` (`Id`),
    CONSTRAINT `fk_post_game` FOREIGN KEY (`game_id`) REFERENCES `Game` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `image_post` (
    `Id` VARCHAR(45) PRIMARY KEY,
    `URL` VARCHAR(255) NOT NULL,
    `post_Id` INT NOT NULL,
    KEY `idx_image_post_post` (`post_Id`),
    CONSTRAINT `fk_image_post_post` FOREIGN KEY (`post_Id`) REFERENCES `Post` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Post_Recommendation_User` (
    `post_Id` INT NOT NULL,
    `user_Id` INT NOT NULL,
    `goodorbad` BOOLEAN NOT NULL,
    PRIMARY KEY (`post_Id`, `user_Id`),
    CONSTRAINT `fk_post_recommendation_post` FOREIGN KEY (`post_Id`) REFERENCES `Post` (`Id`),
    CONSTRAINT `fk_post_recommendation_user` FOREIGN KEY (`user_Id`) REFERENCES `User` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Comment` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_Id` INT NOT NULL,
    `content` VARCHAR(45) NOT NULL,
    `date` DATE NOT NULL,
    `time` TIME NOT NULL,
    `post_Id` INT NOT NULL,
    `comment_Id` INT,
    `anonymous` BOOLEAN NOT NULL,
    KEY `idx_comment_post` (`post_Id`),
    KEY `idx_comment_parent` (`comment_Id`),
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_Id`) REFERENCES `User` (`Id`),
    CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_Id`) REFERENCES `Post` (`Id`),
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`comment_Id`) REFERENCES `Comment` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `friendly_match` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Host_Id` INT NOT NULL,
    `Game_Id` INT NOT NULL,
    `Date` DATE NOT NULL,
    `Time` TIME NOT NULL,
    `Sort` TINYINT NOT NULL,
    `State` TINYINT NOT NULL,
    `Recruit` INT NOT NULL,
    `Comment` VARCHAR(255),
    KEY `idx_friendly_match_game_date_time` (`Game_Id`, `Date`, `Time`),
    CONSTRAINT `fk_friendly_match_host` FOREIGN KEY (`Host_Id`) REFERENCES `game_user` (`Id`),
    CONSTRAINT `fk_friendly_match_game` FOREIGN KEY (`Game_Id`) REFERENCES `Game` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `friendly_match_participation` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Game_User_Id` INT NOT NULL,
    `Friendly_Match_Id` INT NOT NULL,
    `Role` ENUM('client', 'host') NOT NULL,
    UNIQUE KEY `unique_participation` (`Game_User_Id`, `Friendly_Match_Id`),
    CONSTRAINT `fk_match_participation_game_user` FOREIGN KEY (`Game_User_Id`) REFERENCES `game_user` (`Id`),
    CONSTRAINT `fk_match_participation_match` FOREIGN KEY (`Friendly_Match_Id`) REFERENCES `friendly_match` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `friendly_match_request` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Game_user_Id` INT NOT NULL,
    `Friendly_Match_Id` INT NOT NULL,
    `Comment` VARCHAR(255),
    `status` ENUM('approve', 'reject', 'await', 'cancel') NOT NULL,
    `Updated_At` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `unique_request` (`Game_user_Id`, `Friendly_Match_Id`),
    CONSTRAINT `fk_match_request_game_user` FOREIGN KEY (`Game_user_Id`) REFERENCES `game_user` (`Id`),
    CONSTRAINT `fk_match_request_match` FOREIGN KEY (`Friendly_Match_Id`) REFERENCES `friendly_match` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `friendly_match_request_participation` (
    `Id` INT PRIMARY KEY AUTO_INCREMENT,
    `Friendly_match_request_id` INT NOT NULL,
    `Game_user_id` INT NOT NULL,
    UNIQUE KEY `friendly_match_request_id` (`Friendly_match_request_id`, `Game_user_id`),
    CONSTRAINT `fk_match_request_participation_request` FOREIGN KEY (`Friendly_match_request_id`) REFERENCES `friendly_match_request` (`Id`),
    CONSTRAINT `fk_match_request_participation_game_user` FOREIGN KEY (`Game_user_id`) REFERENCES `game_user` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `Game` (`Name`, `sort`, `url`) VALUES
    ('던전앤파이터', 'RPG', '던전앤파이터_로고'),
    ('FC 온라인', '스포츠', 'FC_온라인_로고'),
    ('메이플스토리', 'RPG', '메이플스토리_로고'),
    ('프라시아 전기', 'RPG', '프라시아_전기_로고'),
    ('메이플스토리M', 'RPG', '메이플스토리M_로고'),
    ('히트2', 'RPG', '히트2_로고'),
    ('FC 모바일', '스포츠', 'FC_모바일_로고'),
    ('블루 아카이브', 'RPG', '블루_아카이브_로고'),
    ('던전앤파이터 모바일', 'RPG', '던전앤파이터_모바일_로고'),
    ('마비노기', '기타', '마비노기_로고'),
    ('데이브 더 다이버', '기타', '데이브_더_다이버_로고'),
    ('서든어택', '액션', '서든어택_로고'),
    ('바람의나라: 연', 'RPG', '바람의나라_연_로고'),
    ('카운터-스트라이크 온라인', '액션', '카운터-스트라이크_온라인_로고'),
    ('마비노기 영웅전', 'RPG', '마비노기_영웅전_로고'),
    ('카트라이더 러쉬플러스', '레이싱', '카트라이더_러쉬플러스_로고'),
    ('엘소드', '기타', '엘소드_로고'),
    ('진·삼국무쌍 M', '액션', '진·삼국무쌍_M_로고'),
    ('V4', 'RPG', 'V4_로고'),
    ('메이플스토리 월드', '기타', '메이플스토리_월드_로고'),
    ('어둠의전설', 'RPG', '어둠의전설_로고'),
    ('빌딩앤파이터', '액션', '빌딩앤파이터_로고'),
    ('테일즈위버', 'RPG', '테일즈위버_로고'),
    ('바람의나라', 'RPG', '바람의나라_로고'),
    ('크레이지 아케이드', '기타', '크레이지_아케이드_로고'),
    ('나이트 워커', '기타', '나이트_워커_로고'),
    ('사이퍼즈', '전략', '사이퍼즈_로고'),
    ('아스가르드', '기타', '아스가르드_로고'),
    ('클로저스', '기타', '클로저스_로고'),
    ('워헤이븐', '기타', '워헤이븐_로고'),
    ('카트라이더: 드리프트', '레이싱', '카트라이더_드리프트_로고'),
    ('던전앤파이터 듀얼', '기타', '던전앤파이터_듀얼_로고'),
    ('버블파이터', '기타', '버블파이터_로고'),
    ('일랜시아', 'RPG', '일랜시아_로고'),
    ('고질라 디펜스 포스', '전략', '고질라_디펜스_포스_로고'),
    ('메이플스토리2', 'RPG', '메이플스토리2_로고'),
    ('THE FINALS', '기타', 'THE_FINALS_로고'),
    ('넥슨타운: NEXONTOWN', '기타', '넥슨타운_NEXONTOWN_로고'),
    ('퍼스트 버서커: 카잔', '기타', '퍼스트_버서커_카잔_로고'),
    ('퍼스트 디센던트', '기타', '퍼스트_디센던트_로고'),
    ('테일즈런너', '기타', '테일즈런너_로고'),
    ('파이널판타지14', 'RPG', '파이널판타지14_로고'),
    ('프리스타일2', '스포츠', '프리스타일2_로고'),
    ('아키에이지', 'RPG', '아키에이지_로고'),
    ('드래곤네스트', 'RPG', '드래곤네스트_로고');
