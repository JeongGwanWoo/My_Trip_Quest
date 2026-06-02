-- item_dummy.sql
-- (혹시 모르니 기존 데이터 삭제 후 삽입 - 선택사항)
-- DELETE FROM items; 

INSERT INTO `items` (`name`, `slot`, `image_url`, `price`, `is_purchasable`) VALUES
('기본 스킨',   'SKIN',   '/assets/avatar/skin-base.svg', 0, FALSE),
('테스트 머리', 'HAIR',   '/assets/avatar/testhair.png', 100, TRUE),
('멋진 모자',   'HAT',    '/assets/avatar/hat_test.png', 300, TRUE),
('테스트 상의', 'TOP',    '/assets/avatar/testshirts.png', 200, TRUE),
('테스트 하의', 'BOTTOM', '/assets/avatar/testpants.png', 150, TRUE),
('안경',       'FACE',   '/assets/avatar/glasses_test.png', 250, TRUE);