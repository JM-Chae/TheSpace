package com.thespace.thespace.controller;

import com.thespace.thespace.dto.category.CategoryCreateDTO;
import com.thespace.thespace.dto.category.CategoryListDTO;
import com.thespace.thespace.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public List<CategoryListDTO> list(@RequestParam(name = "path") String path) {
        return categoryService.list(path);
    }

    @PostMapping("/admin")
    public void create(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO,
        Authentication authentication) {
        categoryService.create(categoryCreateDTO, authentication);
    }

    @DeleteMapping("/{categoryId}/admin")
    public void delete(@PathVariable("categoryId") Long categoryId,
        Authentication authentication,
        @RequestParam("communityName") String communityName) {
        categoryService.delete(categoryId, authentication, communityName);
    }
}
