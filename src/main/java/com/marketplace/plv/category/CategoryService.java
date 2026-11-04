package com.marketplace.plv.category;

import com.marketplace.plv.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public List<CategoryResponse> list() {
		return categoryRepository.findAll().stream()
				.map(categoryMapper::entityToResponse)
				.toList();
	}
}
