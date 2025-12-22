-- item_dummy.sql
-- (혹시 모르니 기존 데이터 삭제 후 삽입 - 선택사항)
-- DELETE FROM items; 

INSERT INTO `items` (`name`, `slot`, `image_url`, `price`, `is_purchasable`) VALUES
('기본 스킨',   'SKIN',   '/assets/avatar/skin-base.png', 0, FALSE),
('빨간 모자', 'HAT', '/assets/avatar/redcap.png', 150, TRUE),
('갈색 머리', 'HAIR',   '/assets/avatar/brownhair.png', 100, TRUE),
('가죽 자켓', 'TOP',    '/assets/avatar/hiptop.png', 250, TRUE),
('셔츠', 'TOP',    '/assets/avatar/neatshirts.png', 200, TRUE),
('츄리닝 상의', 'TOP',    '/assets/avatar/tracktop.png', 150, TRUE),
('베이지 면바지', 'BOTTOM', '/assets/avatar/beigecottonpants.png', 150, TRUE),
('청바지', 'BOTTOM', '/assets/avatar/jeans.png', 150, TRUE),
('츄리닝 하의', 'BOTTOM', '/assets/avatar/trackpants.png', 100, TRUE);