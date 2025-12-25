package com.mytripquest.domain.item.service;

import com.mytripquest.domain.item.dto.CreateItemRequest;
import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.UpdateItemRequest; // Added import
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅을 위해 추가
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl; // Added
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors; // Added

@Slf4j // System.err 대신 로그를 남기기 위해 사용
@Service
@RequiredArgsConstructor
@Transactional
public class AdminItemServiceImpl implements AdminItemService {

    private final ItemMapper itemMapper;

    @Value("${file.upload-dir.backend}")
    private String backendUploadDir;

    @Value("${file.upload-dir.frontend}")
    private String frontendUploadDir;

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDto> getAllItems(Pageable pageable) {
        // MyBatis: List 조회 후 PageImpl로 변환
        java.util.List<Item> items = itemMapper.findAll(pageable);
        long total = itemMapper.count();
        return new PageImpl<>(items.stream().map(this::convertToDto).collect(Collectors.toList()), pageable, total);
    }

    @Override
    public ItemDto createItem(CreateItemRequest request, MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_IS_EMPTY);
        }

        // 파일명 중복 방지를 위한 UUID 생성
        String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

        // 백엔드 서버에 실제 저장될 경로
        Path backendPath = Paths.get(backendUploadDir + File.separator + uniqueFileName);

        // DB에 저장하고 프론트엔드에서 접근할 경로
        String frontendPath = frontendUploadDir + "/" + uniqueFileName;

        try {
            // 디렉토리가 없으면 생성
            Files.createDirectories(backendPath.getParent());
            // 파일 저장
            Files.copy(imageFile.getInputStream(), backendPath);
        } catch (IOException e) {
            // 로그 남기기
            log.error("파일 업로드 실패: {}", imageFile.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, e);
        }

        // 엔티티 생성 (Builder 대신 Setter 사용으로 변경)
        Item item = new Item();
        item.setName(request.name());
        item.setSlot(request.slot());
        item.setPrice(request.price());
        item.setPurchasable(request.purchasable());
        item.setImageUrl(frontendPath);

        // DB 저장 (MyBatis insert)
        itemMapper.insertItem(item);
        Item savedItem = item; // insertItem은 void지만 item 객체에 ID가 채워짐 (useGeneratedKeys)

        // 엔티티(Item)를 DTO(ItemDto)로 변환해서 반환
        return convertToDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, UpdateItemRequest request, MultipartFile imageFile) {
        Item item = itemMapper.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        // Update basic item details
        item.setName(request.name());
        item.setSlot(request.slot());
        item.setPrice(request.price());
        item.setPurchasable(request.purchasable());

        // Handle image update if imageChanged flag is true
        if (request.imageChanged()) {
            // Delete old image if it exists
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                try {
                    // Extract filename from the URL path
                    String oldFileName = item.getImageUrl().substring(item.getImageUrl().lastIndexOf('/') + 1);
                    Path oldFilePath = Paths.get(backendUploadDir + File.separator + oldFileName);
                    Files.deleteIfExists(oldFilePath);
                    log.info("Old image deleted: {}", oldFileName);
                } catch (IOException e) {
                    log.error("Failed to delete old item image: {}", item.getImageUrl(), e);
                }
            }

            // Upload new image if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path backendPath = Paths.get(backendUploadDir + File.separator + uniqueFileName);
                String frontendPath = frontendUploadDir + "/" + uniqueFileName;

                try {
                    Files.createDirectories(backendPath.getParent());
                    Files.copy(imageFile.getInputStream(), backendPath);
                    item.setImageUrl(frontendPath); // Update image URL
                    log.info("New image uploaded for item {}: {}", itemId, uniqueFileName);
                } catch (IOException e) {
                    log.error("Failed to upload new image for item {}: {}", itemId, imageFile.getOriginalFilename(), e);
                    throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, e);
                }
            } else {
                // If imageChanged is true but no new file, implies clearing the image
                item.setImageUrl(null);
                log.info("Image cleared for item {}", itemId);
            }
        }

        // DB 수정 (MyBatis update)
        itemMapper.updateItem(item);
        return convertToDto(item); // 수정된 item 객체 반환
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item = itemMapper.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        // 이미지 파일 삭제 시도
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            try {
                // DB에 저장된 경로에서 파일명만 추출
                String fileName = item.getImageUrl().substring(item.getImageUrl().lastIndexOf('/') + 1);
                Path filePath = Paths.get(backendUploadDir + File.separator + fileName);

                // 파일이 존재하면 삭제
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // System.err 대신 log.error 사용 (DB 삭제는 계속 진행)
                log.error("아이템 이미지 삭제 실패 - itemId: {}, path: {}", itemId, item.getImageUrl(), e);
            }
        }

        // TODO: user_items 테이블 등 외래 키 제약 조건이 있다면 여기서 먼저 처리해야 함
        // 예: userItemRepository.deleteByItemId(itemId);

        // MyBatis delete
        itemMapper.deleteItem(itemId);
    }

    private ItemDto convertToDto(Item item) {
        return new ItemDto(
                item.getItemId(),
                item.getName(),
                item.getSlot() != null ? item.getSlot().name() : null,
                item.getImageUrl(),
                item.isPurchasable(),
                item.getPrice(),
                item.getCreatedAt());
    }
}