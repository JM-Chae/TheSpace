package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController()
public class CategoryController
  {
    private final CategoryService categoryService;

    @GetMapping("/getcategory/{path}")
    public List<CategoryDTO> getCategory(@PathVariable(name = "path") String path)
      {
        return categoryService.getAllCategories(path);
      }

    @PostMapping("/{community}/category")
    public void createCategory(@Valid CategoryDTO categoryDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @PathVariable("community") String communityName)
      {
        if (bindingResult.hasErrors())
          {
            Long CategoryId = categoryService.createCategory(categoryDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
          }

        Long CategoryId = categoryService.createCategory(categoryDTO);
        redirectAttributes.addFlashAttribute("result", "Category created successfully");
      }
  }
