package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Slf4j
@RestController
public class CategoryController
  {
    private final CategoryService categoryService;

    @GetMapping("/createCategory")
    public void createCategory()
      {
      }

    @PostMapping("/createCategory")
    public String createCategory(@Valid CategoryDTO categoryDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        if(bindingResult.hasErrors())
          {
            Long CategoryId = categoryService.createCategory(categoryDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/createCategory";
          }

        Long CategoryId = categoryService.createCategory(categoryDTO);
        redirectAttributes.addFlashAttribute("result", "Category created successfully");
        return "redirect:/" + categoryDTO.getPath() +"/board/list/?category="+ categoryDTO.getCategoryName();
      }

  }
