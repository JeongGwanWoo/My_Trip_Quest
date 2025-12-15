package com.ssafy.tripquest.service;

import com.ssafy.tripquest.domain.User;
import com.ssafy.tripquest.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserMapper userMapper;

	@Transactional
	public User toggleAccessory(String userId, String accessoryToToggle) {
		log.debug("--- 액세서리 상태 변경 시작 ---");
		User user = userMapper.findById(userId);
		if (user == null) {
			log.error("사용자를 찾을 수 없습니다: ID {}", userId);
			throw new RuntimeException("사용자를 찾을 수 없습니다.");
		}

		String currentCustomization = user.getCharacterCustomization();
		log.debug("1단계: DB에서 현재 커스터마이징 정보 가져옴: '{}'", currentCustomization);

		if ("{}".equals(currentCustomization)) {
			currentCustomization = "";
		}

		Set<String> accessories = new HashSet<>();
		if (currentCustomization != null && !currentCustomization.trim().isEmpty()) {
			accessories = Arrays.stream(currentCustomization.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toSet());
		}
		log.debug("2단계: 액세서리 Set으로 파싱: {}", accessories);

		String trimmedAccessory = accessoryToToggle.trim();
		log.debug("3단계: 변경할 액세서리: '{}'", trimmedAccessory);

		if (accessories.contains(trimmedAccessory)) {
			accessories.remove(trimmedAccessory);
			log.debug("4단계: 액세서리 존재. 제거 후: {}", accessories);
		} else {
			accessories.add(trimmedAccessory);
			log.debug("4단계: 액세서리 없음. 추가 후: {}", accessories);
		}

		String newCustomization = String.join(",", accessories);
		log.debug("5단계: DB에 저장될 새 커스터마이징 문자열: '{}'", newCustomization);

		userMapper.updateCharacterCustomization(userId, newCustomization);
		user.setCharacterCustomization(newCustomization);
		log.debug("--- 액세서리 상태 변경 완료 ---");

		return user;
	}

	public byte[] generateCharacterImage(String userId) throws IOException {
		System.setProperty("java.awt.headless", "true");
		User user = userMapper.findById(userId);
		if (user == null) {
			throw new RuntimeException("사용자를 찾을 수 없습니다.");
		}

		ClassPathResource baseImageResource = new ClassPathResource("img/character_base.png");
		BufferedImage combinedImage;
		try (InputStream baseImageStream = baseImageResource.getInputStream()) {
			combinedImage = ImageIO.read(baseImageStream);
		}

		Graphics2D g = combinedImage.createGraphics();

		String customization = user.getCharacterCustomization();
		
		if ("{}".equals(customization)) {
			customization = "";
		}
		
		if (customization != null && !customization.isEmpty()) {
			List<String> accessories = Arrays.stream(customization.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toList());
			for (String accessory : accessories) {
				log.trace("착용할 액세서리: {}", accessory);
				String accessoryImageName = "img/accessory_" + accessory.trim() + "_2.png";
				ClassPathResource accessoryResource = new ClassPathResource(accessoryImageName);
				if (accessoryResource.exists()) {
					try (InputStream accessoryStream = accessoryResource.getInputStream()) {
						BufferedImage accessoryImage = ImageIO.read(accessoryStream);
						g.drawImage(accessoryImage, 0, 0, null);
					}
				} else {
					log.warn("액세서리 이미지를 찾을 수 없습니다: {}", accessoryImageName);
				}
			}
		}

		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(combinedImage, "png", baos);
		return baos.toByteArray();
	}
}
