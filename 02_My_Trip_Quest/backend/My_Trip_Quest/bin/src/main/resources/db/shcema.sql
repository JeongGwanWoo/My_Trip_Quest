-- 0) 안전하게 가려면, 먼저 DB 선택
CREATE DATABASE IF NOT EXISTS mytripquest CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mytripquest;

-- 1. USERS (사용자)
CREATE TABLE IF NOT EXISTS `users` (
    `user_id`      BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email`        VARCHAR(255) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `nickname`     VARCHAR(100) NOT NULL UNIQUE,
    `role`         ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    `total_xp`     INT NOT NULL DEFAULT 0,
    `points`       INT NOT NULL DEFAULT 0,
    `created_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 2. AVATARS (아바타) - USERS와 1:1
CREATE TABLE IF NOT EXISTS `avatars` (
    `user_id`        BIGINT NOT NULL,
    `character_style` VARCHAR(100) NOT NULL,
    `created_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `pk_avatars` PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_avatars_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 3. LOCATIONS (장소)
CREATE TABLE IF NOT EXISTS `locations` (
    `location_id`       BIGINT AUTO_INCREMENT PRIMARY KEY,
    `content_id`        VARCHAR(100) NULL UNIQUE,
    `title`             VARCHAR(255) NOT NULL,
    `content_type_id`   VARCHAR(50) NULL,
    `area_code`         VARCHAR(20) NULL,
    `sigungu_code`      VARCHAR(20) NULL,
    `latitude`          DECIMAL(10,7) NOT NULL,
    `longitude`         DECIMAL(10,7) NOT NULL,
    `gps_verify_radius` INT NOT NULL DEFAULT 100,
    `tel`               VARCHAR(50) NULL,
    `addr1`             VARCHAR(255) NULL,
    `addr2`             VARCHAR(255) NULL,
    `homepage`          TEXT NULL,
    `overview`          TEXT NULL,
    `created_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 4. QUEST_TYPES (퀘스트 타입)
CREATE TABLE IF NOT EXISTS `quest_types` (
    `quest_type_id` INT AUTO_INCREMENT PRIMARY KEY,
    `type_name`     VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 5. QUESTS (퀘스트)
CREATE TABLE IF NOT EXISTS `quests` (
    `quest_id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `location_id`       BIGINT NULL,
    `quest_type_id`     INT NOT NULL,
    `previous_quest_id` BIGINT NULL,
    `difficulty`        ENUM('EASY','NORMAL','HARD') NOT NULL DEFAULT 'NORMAL',
    `title`             VARCHAR(255) NOT NULL,
    `description`       TEXT NOT NULL,
    `reward_xp`         INT NOT NULL DEFAULT 0,
    `reward_points`     INT NOT NULL DEFAULT 0,
    `require_gps_verify` BOOLEAN NOT NULL DEFAULT TRUE,
    `is_active`         BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT `fk_quests_location`
        FOREIGN KEY (`location_id`) REFERENCES `locations`(`location_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
        
    CONSTRAINT `fk_quests_type`
        FOREIGN KEY (`quest_type_id`) REFERENCES `quest_types`(`quest_type_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT `fk_quests_previous`
        FOREIGN KEY (`previous_quest_id`) REFERENCES `quests`(`quest_id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- 6. ITEMS (콤마 에러 수정됨)
CREATE TABLE `items` (
    `item_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`           VARCHAR(100) NOT NULL,            -- 아이템 이름 (예: 빨간 모자)
    `slot`           ENUM('HAIR', 'HAT', 'TOP', 'BOTTOM', 'FACE', 'SKIN') NOT NULL, -- 장착 부위
    `image_url`      VARCHAR(255) NOT NULL,            -- 이미지 경로
    `price`          INT DEFAULT 0,                    -- 가격
    `is_purchasable` BOOLEAN DEFAULT TRUE,             -- 상점 판매 여부
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 등록일 (이건 남겨두는 게 좋습니다)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 7. USER_ITEMS (인벤토리, 사용자-아이템 N:M)
CREATE TABLE IF NOT EXISTS `user_items` (
    `user_id`     BIGINT NOT NULL,
    `item_id`     BIGINT NOT NULL,
    `quantity`    INT NOT NULL DEFAULT 1,
    `is_equipped` BOOLEAN NOT NULL DEFAULT FALSE,
    `acquired_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT `pk_user_items` PRIMARY KEY (`user_id`, `item_id`),
    CONSTRAINT `fk_user_items_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_user_items_item`
        FOREIGN KEY (`item_id`) REFERENCES `items`(`item_id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 8. USER_QUESTS (유저별 퀘스트 상태)
CREATE TABLE IF NOT EXISTS `user_quests` (
    `user_quest_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`         BIGINT NOT NULL,
    `quest_id`        BIGINT NOT NULL,
    `status`          ENUM('ACCEPTED','IN_PROGRESS','COMPLETED','FAILED') NOT NULL DEFAULT 'ACCEPTED',
    `proof_data`      VARCHAR(255) NULL,    -- 사진 URL, 텍스트 답안, 기타 인증 정보
    `accepted_at`     TIMESTAMP NULL,
    `completed_at`    TIMESTAMP NULL,
    `last_updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT `fk_user_quests_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT `fk_user_quests_quest`
        FOREIGN KEY (`quest_id`) REFERENCES `quests`(`quest_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT `uq_user_quest` UNIQUE (`user_id`, `quest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- 9. ACTIVITY_LOGS (활동 로그)
CREATE TABLE IF NOT EXISTS `activity_logs` (
    `log_id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT NOT NULL,
    `activity_type` VARCHAR(50) NOT NULL,
    `related_id`    BIGINT NULL,
    `log_message`   VARCHAR(255) NOT NULL,
    `created_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT `fk_activity_logs_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
