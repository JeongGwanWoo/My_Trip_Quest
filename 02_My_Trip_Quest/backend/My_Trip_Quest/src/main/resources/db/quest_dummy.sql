SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE quests;
TRUNCATE TABLE locations;
TRUNCATE TABLE quest_types;
SET FOREIGN_KEY_CHECKS = 1;

-- Dummy data for MyTripQuest

-- LOCATIONS
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`)
VALUES
(1, '경복궁', 37.579617, 126.977041, '1'),
(2, '광화문 광장', 37.5759, 126.9768, '1'),
(3, '북촌 한옥마을', 37.5826, 126.9835, '1'),
(4, '국립아시아문화전당', 35.1585, 126.9224, '5'),
(5, '양림동 펭귄마을', 35.1388, 126.9150, '5');

-- QUEST_TYPES
INSERT INTO `quest_types` (`quest_type_id`, `type_name`)
VALUES
(1, '인증샷'),
(2, '발견'),
(3, '도착');

-- QUESTS
INSERT INTO `quests` (`location_id`, `quest_type_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(1, 1, 'EASY', '경복궁 근정전 앞에서 인증샷 찍기', '경복궁의 중심, 근정전의 위엄을 배경으로 멋진 사진을 남겨보세요.', 100, 10, TRUE),
(2, 1, 'NORMAL', '광화문 광장 세종대왕 동상과 함께하기', '세종대왕 동상 앞에서 한글의 위대함을 느껴보세요.', 150, 15, TRUE),
(3, 2, 'HARD', '북촌 한옥마을에서 가장 아름다운 한옥 찾기', '북촌의 수많은 한옥들 중, 자신만의 최고 한옥을 찾아 사진으로 남겨주세요.', 300, 30, FALSE),
(1, 3, 'EASY', '경복궁 도착 퀘스트', '경복궁에 도착하여 퀘스트를 완료하세요. GPS 인증이 필요합니다.', 50, 5, TRUE),
(4, 1, 'NORMAL', '국립아시아문화전당 인증샷', '국립아시아문화전당의 멋진 건축물을 배경으로 인증샷을 남겨보세요.', 150, 15, TRUE),
(5, 2, 'EASY', '양림동 펭귄마을 펭귄 찾기', '양림동 펭귄마을에서 숨어있는 펭귄들을 찾아보세요.', 100, 10, FALSE);
