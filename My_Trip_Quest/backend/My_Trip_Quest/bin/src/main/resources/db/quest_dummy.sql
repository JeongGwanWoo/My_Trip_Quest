SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE quests;
TRUNCATE TABLE locations;
TRUNCATE TABLE quest_types;
SET FOREIGN_KEY_CHECKS = 1;

-- Dummy data for MyTripQuest

-- QUEST_TYPES
INSERT INTO `quest_types` (`quest_type_id`, `type_name`)
VALUES
(1, '도착'),
(2, '사진');

-- =======================================================================
-- 관광지: 경복궁
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(1, '경복궁', 37.579617, 126.977041, '1', 500);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(10, 1, 1, NULL, 'EASY', '경복궁 도착', '경복궁에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(11, 1, 2, 10, 'EASY', '경복궁 근정전 사진 찍기', '경복궁의 중심, 근정전의 위엄을 배경으로 멋진 사진을 남겨보세요.', 100, 10, TRUE);

-- =======================================================================
-- 관광지: 광화문 광장
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(2, '광화문 광장', 37.5759, 126.9768, '1', 280);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(20, 2, 1, NULL, 'EASY', '광화문 광장 도착', '광화문 광장에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(21, 2, 2, 20, 'NORMAL', '광화문 광장 세종대왕 동상 사진 찍기', '세종대왕 동상 앞에서 한글의 위대함을 느껴보세요.', 150, 15, TRUE);

-- =======================================================================
-- 관광지: 북촌 한옥마을
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(3, '북촌 한옥마을', 37.5826, 126.9835, '1', 600);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(30, 3, 1, NULL, 'EASY', '북촌 한옥마을 도착', '북촌 한옥마을에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(31, 3, 2, 30, 'HARD', '북촌 한옥마을에서 가장 아름다운 한옥 사진 찍기', '북촌의 수많은 한옥들 중, 자신만의 최고 한옥을 찾아 사진으로 남겨주세요.', 300, 30, TRUE);

-- =======================================================================
-- 관광지: 국립아시아문화전당 (기존)
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(4, '국립아시아문화전당 (기존)', 35.1585, 126.9224, '5', 280);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(40, 4, 1, NULL, 'EASY', '국립아시아문화전당 (기존) 도착', '국립아시아문화전당에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(41, 4, 2, 40, 'NORMAL', '국립아시아문화전당 (기존) 사진 찍기', '국립아시아문화전당의 멋진 건축물을 배경으로 멋진 한 컷을 남겨보세요.', 150, 15, TRUE);

-- =======================================================================
-- 관광지: 양림동 펭귄마을
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5, '양림동 펭귄마을', 35.1388, 126.9150, '5', 150);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(50, 5, 1, NULL, 'EASY', '양림동 펭귄마을 도착', '양림동 펭귄마을에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(51, 5, 2, 50, 'EASY', '양림동 펭귄마을 펭귄 사진 찍기', '양림동 펭귄마을에서 숨어있는 펭귄들을 찾아보세요.', 100, 10, TRUE);


-- ======== 서울 관광지 데이터 (신규 추가) ========

-- =======================================================================
-- 관광지: 63스퀘어
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`, `tel`, `homepage`)
VALUES
(1001, '63스퀘어', 37.520024, 126.94007, '1', 100, '1833-7001', 'https://www.hanwharesort.co.kr/irsweb/resort3/resort/resort_view.do?resort_cd=04000');

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(10010, 1001, 1, NULL, 'EASY', '63스퀘어 도착', '63스퀘어에 도착하여 GPS를 인증하세요.', 50, 5),
(10011, 1001, 2, 10010, 'NORMAL', '63스퀘어 배경으로 사진 찍기', '한강을 배경으로 63스퀘어의 멋진 모습을 사진으로 남겨보세요.', 150, 15);

-- =======================================================================
-- 관광지: N서울타워
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`, `tel`, `homepage`)
VALUES
(1002, 'N서울타워', 37.5511694, 126.9882266, '1', 150, '02-3455-9277', 'http://www.seoultower.co.kr/');

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(10020, 1002, 1, NULL, 'EASY', 'N서울타워 도착', 'N서울타워에 도착하여 GPS를 인증하세요.', 50, 5),
(10021, 1002, 2, 10020, 'NORMAL', 'N서울타워에서 서울 전경 사진 찍기', 'N서울타워에서 서울의 아름다운 전경을 사진으로 남겨보세요.', 150, 15);

-- =======================================================================
-- 관광지: 창덕궁
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`, `tel`, `homepage`)
VALUES
(1003, '창덕궁', 37.5794833, 126.9911750, '1', 450, '02-3668-2300', 'http://www.cdg.go.kr/');

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(10030, 1003, 1, NULL, 'EASY', '창덕궁 도착', '창덕궁에 도착하여 GPS를 인증하세요.', 50, 5),
(10031, 1003, 2, 10030, 'NORMAL', '창덕궁 인정전 사진 찍기', '창덕궁의 정전인 인정전의 모습을 사진으로 남겨보세요.', 150, 15);

-- =======================================================================
-- 관광지: 롯데월드타워
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`, `tel`, `homepage`)
VALUES
(1004, '롯데월드타워', 37.51250, 127.10278, '1', 200, '02-3213-5000', 'https://seoulsky.lotteworld.com/');

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(10040, 1004, 1, NULL, 'EASY', '롯데월드타워 도착', '롯데월드타워에 도착하여 GPS를 인증하세요.', 50, 5),
(10041, 1004, 2, 10040, 'NORMAL', '롯데월드타워를 배경으로 사진 찍기', '롯데월드타워의 웅장한 모습을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: DDP (동대문디자인플라자)
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`, `tel`, `homepage`)
VALUES
(1005, 'DDP (동대문디자인플라자)', 37.567191, 127.010490, '1', 180, '02-2153-0000', 'https://ddp.or.kr/');

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(10050, 1005, 1, NULL, 'EASY', 'DDP 도착', 'DDP(동대문디자인플라자)에 도착하여 GPS를 인증하세요.', 50, 5),
(10051, 1005, 2, 10050, 'NORMAL', 'DDP 건물 사진 찍기', '독특한 디자인의 DDP 건물을 배경으로 사진을 찍어 인증하세요.', 150, 15);


-- ======== 광주 관광지 데이터 (신규 추가) ========

-- =======================================================================
-- 관광지: 광주학생독립운동발생지(광주제일고등학교)
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5001, '광주학생독립운동발생지(광주제일고등학교)', 35.1532549, 126.9072398, '5', 200); -- 학교 면적 불분명으로 200m 임의 지정

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50010, 5001, 1, NULL, 'EASY', '광주학생독립운동발생지 도착', '광주학생독립운동발생지(광주제일고등학교)에 도착하여 GPS를 인증하세요.', 50, 5),
(50011, 5001, 2, 50010, 'NORMAL', '광주학생독립운동발생지 기념탑 사진 찍기', '광주학생독립운동 기념탑을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 월봉서원(빙월당)
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5002, '월봉서원(빙월당)', 35.235583, 126.744750, '5', 150);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50020, 5002, 1, NULL, 'EASY', '월봉서원(빙월당) 도착', '월봉서원(빙월당)에 도착하여 GPS를 인증하세요.', 50, 5),
(50021, 5002, 2, 50020, 'NORMAL', '월봉서원(빙월당) 건축물 사진 찍기', '월봉서원(빙월당)의 고즈넉한 건축물 앞에서 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 구 전남도청 본관
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5003, '구 전남도청 본관', 35.146666, 126.920277, '5', 50);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50030, 5003, 1, NULL, 'EASY', '구 전남도청 본관 도착', '구 전남도청 본관에 도착하여 GPS를 인증하세요.', 50, 5),
(50031, 5003, 2, 50030, 'NORMAL', '구 전남도청 본관 전경 사진 찍기', '구 전남도청 본관 앞에서 역사적인 건물을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 충효동 정려비각
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5004, '충효동 정려비각', 35.18472, 127.00111, '5', 40);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50040, 5004, 1, NULL, 'EASY', '충효동 정려비각 도착', '충효동 정려비각에 도착하여 GPS를 인증하세요.', 50, 5),
(50041, 5004, 2, 50040, 'NORMAL', '충효동 정려비각 앞에서 사진 찍기', '충효동 정려비각의 고즈넉한 모습을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 포충사
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5005, '포충사', 35.090555, 126.847221, '5', 30);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50050, 5005, 1, NULL, 'EASY', '포충사 도착', '포충사에 도착하여 GPS를 인증하세요.', 50, 5),
(50051, 5005, 2, 50050, 'NORMAL', '포충사 삼문 앞에서 사진 찍기', '포충사의 삼문 앞에서 충절을 기리며 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 증심사 삼층석탑
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5006, '증심사 삼층석탑', 35.128389, 126.969695, '5', 20);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50060, 5006, 1, NULL, 'EASY', '증심사 삼층석탑 도착', '증심사 삼층석탑 앞에 도착하여 GPS를 인증하세요.', 50, 5),
(50061, 5006, 2, 50060, 'NORMAL', '증심사 삼층석탑과 함께 사진 찍기', '증심사 삼층석탑을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 증심사 오백전
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5007, '증심사 오백전', 35.12833, 126.96972, '5', 30);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50070, 5007, 1, NULL, 'EASY', '증심사 오백전 도착', '증심사 오백전 앞에 도착하여 GPS를 인증하세요.', 50, 5),
(50071, 5007, 2, 50070, 'NORMAL', '증심사 오백전 건물 사진 찍기', '증심사 오백전의 아름다운 모습을 배경으로 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 증심사
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5008, '증심사', 35.12915259439334, 126.96901302479323, '5', 250);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50080, 5008, 1, NULL, 'EASY', '증심사 도착', '증심사에 도착하여 GPS를 인증하세요.', 50, 5),
(50081, 5008, 2, 50080, 'NORMAL', '증심사 대웅전 사진 찍기', '증심사 대웅전을 배경으로 사찰의 평온함을 담아 사진을 찍어 인증하세요.', 150, 15);

-- =======================================================================
-- 관광지: 광주읍성터
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5009, '광주읍성터', 35.1468419, 126.9210583, '5', 400);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50090, 5009, 1, NULL, 'EASY', '광주읍성터 도착', '광주읍성터 추정 위치에 도착하여 GPS를 인증하세요.', 50, 5),
(50091, 5009, 2, 50090, 'NORMAL', '광주읍성터 흔적 사진 찍기', '광주읍성터의 흔적(표지석 등)을 찾아 사진으로 남겨주세요.', 150, 15);

-- =======================================================================
-- 관광지: 광주향교
-- =======================================================================
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
(5010, '광주향교', 35.14611, 126.903611, '5', 100);

INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
(50100, 5010, 1, NULL, 'EASY', '광주향교 도착', '광주향교에 도착하여 GPS를 인증하세요.', 50, 5),
(50101, 5010, 2, 50100, 'NORMAL', '광주향교 대성전 사진 찍기', '광주향교의 중심 건물인 대성전 앞에서 사진을 찍어 인증하세요.', 150, 15);
