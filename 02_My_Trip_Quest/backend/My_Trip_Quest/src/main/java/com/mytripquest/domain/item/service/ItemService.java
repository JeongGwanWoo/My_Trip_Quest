package com.mytripquest.domain.item.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional(readOnly = true)
    public List<ItemDto> findAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ItemDto convertToDto(Item item) {
        return new ItemDto(
                item.getItemId(),
                item.getName(),
                item.getSlot() != null ? item.getSlot().name() : null,
                item.getImageUrl(),
                item.isPurchasable(),
                item.getPrice(),
                item.getCreatedAt()
        );
    }
    
    public List<UserItem> findMyItems(Long userId) {
        return itemMapper.findUserItemsByUserId(userId);
    }
}
