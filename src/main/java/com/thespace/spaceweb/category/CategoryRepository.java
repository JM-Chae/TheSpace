package com.thespace.spaceweb.category;

import com.thespace.spaceweb.community.Community;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCommunity(Community community);
}
