DROP DATABASE if exists `mytripquest`;
CREATE DATABASE `mytripquest`;
USE `mytripquest`;

-- ------------------------------
-- 1. users (사용자 테이블)
-- ------------------------------
CREATE TABLE users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인용)',
  password VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
  nickname VARCHAR(50) NOT NULL COMMENT '닉네임',
  point_balance INT DEFAULT 0 COMMENT '보유 포인트',
  profile_image VARCHAR(255) COMMENT '프로필 이미지 URL',
  character_customization VARCHAR(255) DEFAULT '{}' COMMENT '캐릭터 꾸미기 상태 (JSON)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일'
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사용자 테이블';

-- ------------------------------
-- 2. tourist_spots (관광지 테이블)
-- ------------------------------
CREATE TABLE tourist_spots (
  no INT AUTO_INCREMENT PRIMARY KEY COMMENT '관광지번호',
  content_id INT COMMENT '콘텐츠ID (한국관광공사 API)',
  title VARCHAR(500) NOT NULL COMMENT '관광지명',
  content_type_id INT COMMENT '관광타입ID',
  area_code INT COMMENT '시도코드',
  si_gun_gu_code INT COMMENT '시군구코드',
  first_image1 VARCHAR(500) COMMENT '이미지경로1',
  first_image2 VARCHAR(500) COMMENT '이미지경로2',
  map_level INT COMMENT '지도레벨',
  latitude DECIMAL(20,17) NOT NULL COMMENT '위도',
  longitude DECIMAL(20,17) NOT NULL COMMENT '경도',
  tel VARCHAR(50) COMMENT '전화번호',
  addr1 VARCHAR(500) COMMENT '주소1',
  addr2 VARCHAR(500) COMMENT '주소2',
  homepage TEXT COMMENT '홈페이지',
  overview TEXT COMMENT '개요/설명',
  gps_verify_range INT DEFAULT 100 COMMENT '인증 반경(미터)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci;

-- ------------------------------
-- 3. missions (미션 테이블)
-- ------------------------------
CREATE TABLE missions (
  mission_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '미션 ID',
  spot_no INT NOT NULL COMMENT '관광지번호 (FK)',
  mission_order INT NOT NULL COMMENT '미션 순서 (1=도착, 2,3,4...=추가미션)',
  unlock_condition BIGINT COMMENT '선행 미션 ID (NULL=바로 시작 가능)',
  mission_type ENUM('ARRIVAL', 'PHOTO', 'QUIZ', 'EXPLORE', 'EXPERIENCE') NOT NULL,
  require_gps_verify BOOLEAN DEFAULT FALSE COMMENT 'GPS 재확인 필요 여부',
  title VARCHAR(200) NOT NULL COMMENT '미션 제목',
  description TEXT COMMENT '미션 설명',
  reward_point INT NOT NULL COMMENT '보상 포인트',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (spot_no) REFERENCES tourist_spots(no) ON DELETE CASCADE,
  FOREIGN KEY (unlock_condition) REFERENCES missions(mission_id) ON DELETE SET NULL
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci;

-- ------------------------------
-- 4. point_logs (포인트 거래 내역 테이블)
-- ------------------------------
CREATE TABLE point_logs (
  log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '로그 ID',
  user_id BIGINT NOT NULL COMMENT '사용자 ID',
  transaction_type ENUM('EARN', 'SPEND') NOT NULL COMMENT '거래 유형',
  amount INT NOT NULL COMMENT '포인트 수량',
  balance_after INT NOT NULL COMMENT '거래 후 잔액',
  source_type ENUM('MISSION', 'ITEM_PURCHASE', 'ADMIN') NOT NULL COMMENT '소스 타입',
  source_id BIGINT COMMENT '소스 참조 ID (mission_log_id, item_id 등)',
  description VARCHAR(255) COMMENT '설명',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '거래 시각',
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='포인트 거래 내역';
  
-- =================================================================
--  Sample Data Inserts
-- =================================================================

-- ------------------------------
-- 1) users: user_id = 1 사용자 1명
-- ------------------------------
INSERT INTO users (user_id, email, password, nickname, point_balance, profile_image)
VALUES (1, 'testuser@example.com', 'test-password', '테스트유저', 0, '/img/character_base.png')
ON DUPLICATE KEY UPDATE email=VALUES(email), nickname=VALUES(nickname), profile_image=VALUES(profile_image);

-- ------------------------------
-- 2) tourist_spots: 경복궁 (no=1) & SSAFY광주 (no=2)
-- ------------------------------
INSERT INTO tourist_spots (no, title, latitude, longitude, gps_verify_range, addr1, tel, overview)
VALUES
(1, '경복궁', 37.579617, 126.977041, 1000, '서울특별시 종로구 사직로 161', '02-3700-3900', '조선 왕조의 법궁, 대표 관광지 경복궁입니다.'),
(2, 'SSAFY광주', 35.205496, 126.811577, 1000, '광주광역시 광산구 오선동', '062-2222-3333', '광주삼성사업장입니다.')
ON DUPLICATE KEY UPDATE
  title=VALUES(title),
  latitude=VALUES(latitude),
  longitude=VALUES(longitude),
  gps_verify_range=VALUES(gps_verify_range),
  addr1=VALUES(addr1),
  tel=VALUES(tel),
  overview=VALUES(overview);

-- ------------------------------
-- 3) missions: 각 관광지별 ARRIVAL 미션 + PHOTO 미션
-- ------------------------------
INSERT INTO missions (mission_id, spot_no, mission_order, mission_type, require_gps_verify, title, description, reward_point)
VALUES
(1, 1, 1, 'ARRIVAL', TRUE, '경복궁 도착 미션', '경복궁 반경 1000m 이내로 접근한 후, ''도착 확인'' 버튼을 눌러 완료하는 미션입니다.', 100),
(2, 2, 1, 'ARRIVAL', TRUE, 'SSAFY광주 도착 미션', 'SSAFY광주 반경 1000m 이내로 접근한 후, ''도착 확인'' 버튼을 눌러 완료하는 미션입니다.', 50),
(3, 1, 2, 'PHOTO', FALSE, '근정전 사진 찍기', '경복궁의 중심, 근정전의 멋진 사진을 찍어 인증해주세요.', 150),
(4, 2, 2, 'PHOTO', FALSE, 'SSAFY 건물 사진 찍기', 'SSAFY 광주 캠퍼스 건물의 멋진 사진을 찍어 인증해주세요.', 120)
ON DUPLICATE KEY UPDATE
  title=VALUES(title),
  description=VALUES(description),
  reward_point=VALUES(reward_point);

-- 4) point_logs: 비워둠
-- ------------------------------

-- ------------------------------
-- 5) tourist_spots: 테스트용 스팟 (no=3) - 전국 어디서든 통과
-- ------------------------------
INSERT INTO tourist_spots (no, title, latitude, longitude, gps_verify_range, addr1, tel, overview)
VALUES
(3, 'SSAFY 광주 캠퍼스 (테스트용 - 전국)', 35.205496, 126.811577, 500000, '광주광역시 광산구 오선동 (테스트용)', '000-0000-0000', '전국 어디서든 위치 인증이 통과되는 테스트용 스팟입니다.')
ON DUPLICATE KEY UPDATE
  title=VALUES(title),
  latitude=VALUES(latitude),
  longitude=VALUES(longitude),
  gps_verify_range=VALUES(gps_verify_range),
  addr1=VALUES(addr1),
  tel=VALUES(tel),
  overview=VALUES(overview);

-- ------------------------------
-- 6) missions: 테스트용 스팟 (no=3)의 ARRIVAL + PHOTO 미션
-- ------------------------------
INSERT INTO missions (mission_id, spot_no, mission_order, mission_type, require_gps_verify, title, description, reward_point)
VALUES
(5, 3, 1, 'ARRIVAL', TRUE, 'SSAFY 광주 캠퍼스 도착 미션 (테스트용 - 전국)', '도착 확인 버튼을 눌러 완료하는 테스트용 미션입니다.', 10),
(6, 3, 2, 'PHOTO', FALSE, 'SSAFY 광주 캠퍼스 사진 미션 (테스트용 - 전국)', '전국 어디서든 사진 인증이 통과되는 테스트용 미션입니다. (AI 검증 포함)', 20)
ON DUPLICATE KEY UPDATE
  title=VALUES(title),
  description=VALUES(description),
  reward_point=VALUES(reward_point);