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
-- For clarity, quest_id is now explicitly defined.
-- Gyeongbokgung (Location 1)
INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`, `require_gps_verify`)
VALUES
(10, 1, 3, NULL, 'EASY', '경복궁 도착', '경복궁에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(11, 1, 1, 10, 'EASY', '경복궁 근정전 앞에서 인증샷 찍기', '경복궁의 중심, 근정전의 위엄을 배경으로 멋진 사진을 남겨보세요.', 100, 10, TRUE),

-- Gwanghwamun Square (Location 2)
(20, 2, 3, NULL, 'EASY', '광화문 광장 도착', '광화문 광장에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(21, 2, 1, 20, 'NORMAL', '광화문 광장 세종대왕 동상과 함께하기', '세종대왕 동상 앞에서 한글의 위대함을 느껴보세요.', 150, 15, TRUE),

-- Bukchon Hanok Village (Location 3)
(30, 3, 3, NULL, 'EASY', '북촌 한옥마을 도착', '북촌 한옥마을에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(31, 3, 2, 30, 'HARD', '북촌 한옥마을에서 가장 아름다운 한옥 찾기', '북촌의 수많은 한옥들 중, 자신만의 최고 한옥을 찾아 사진으로 남겨주세요.', 300, 30, FALSE),

-- Asia Culture Center (Location 4)
(40, 4, 3, NULL, 'EASY', '국립아시아문화전당 도착', '국립아시아문화전당에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(41, 4, 1, 40, 'NORMAL', '국립아시아문화전당 인증샷', '국립아시아문화전당의 멋진 건축물을 배경으로 인증샷을 남겨보세요.', 150, 15, TRUE),

-- Yanglim-dong Penguin Village (Location 5)
(50, 5, 3, NULL, 'EASY', '양림동 펭귄마을 도착', '양림동 펭귄마을에 도착하여 GPS를 인증하세요.', 50, 5, TRUE),
(51, 5, 2, 50, 'EASY', '양림동 펭귄마을 펭귄 찾기', '양림동 펭귄마을에서 숨어있는 펭귄들을 찾아보세요.', 100, 10, FALSE);