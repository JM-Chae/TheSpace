package com.thespace.thespace.controller;

import com.thespace.thespace.dto.CategoryDTO;
import com.thespace.thespace.service.CategoryService;
import com.thespace.thespace.service.UserRoleService;
import com.thespace.thespace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController()
public class CategoryController
  {
    private final CategoryService categoryService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    @GetMapping("/getcategory/{path}")
    public List<CategoryDTO> getCategory(@PathVariable(name = "path") String path)
      {
        return categoryService.getAllCategories(path);
      }

    @PostMapping("/category")
    public void createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @RequestParam("userId") String userId)
      {
        if (userService.findUserRoles(userId).contains(userRoleService.findRoleId("ADMIN_" + categoryDTO.getPath())))
          {
            if (bindingResult.hasErrors())
              {
                categoryService.createCategory(categoryDTO);
                redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
              }

            categoryService.createCategory(categoryDTO);
            redirectAttributes.addFlashAttribute("result", "Category created successfully");
          }

        redirectAttributes.addFlashAttribute("result", "You are not this community Admin");
      }

    @DeleteMapping
    public void deleteCategory(@RequestParam("categoryId") Long categoryId, @RequestParam("userId") String userId, @RequestParam("communityName") String communityName, RedirectAttributes redirectAttributes)
      {
        if (userService.findUserRoles(userId).contains(userRoleService.findRoleId("ADMIN_" + communityName)))
          {
            categoryService.deleteCategory(categoryId);
            redirectAttributes.addFlashAttribute("result", "Category deleted successfully");
          } else
          {
            redirectAttributes.addFlashAttribute("result", "You are not this community Admin");
          }
      }
  }
