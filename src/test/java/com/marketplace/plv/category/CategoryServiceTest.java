package com.marketplace.plv.category;

import com.marketplace.plv.category.dto.CategoryResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

	@Test
	void listMapsAllCategories() {
		var repository = mock(CategoryRepository.class);
		var mapper = mock(CategoryMapper.class);
		var category = mock(Category.class);
		var response = new CategoryResponse(1L, "Books", "Course materials");
		when(repository.findAll()).thenReturn(List.of(category));
		when(mapper.entityToResponse(category)).thenReturn(response);

		var result = new CategoryService(repository, mapper).list();

		assertEquals(List.of(response), result);
		verify(mapper).entityToResponse(category);
	}
}
