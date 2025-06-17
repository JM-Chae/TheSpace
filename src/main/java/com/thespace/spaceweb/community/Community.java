package com.thespace.spaceweb.community;

import com.thespace.common.BaseEntity;
import com.thespace.spaceweb.category.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;


@Getter
@Entity
public class Community extends BaseEntity {

    // lombok을 jpa와 쓸 때 주의해야할 점
    // 1. NoArgsConstructor를 왜 붙이는가? 그리고 어떻게 붙여야하는가?
    // 1-1. JPA의 어떤 성질 때문일까?
    // 2. ToString()을 어떻게 사용해야할까? 왜 조심해야할까?
    // 3.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @Column(unique = true)
    @NotNull
    private String communityName;

    @Column(columnDefinition = "longtext")
    private String description;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<Category> category = new ArrayList<>(); //Add limit?

    public Community() {

    }

    @Builder
    public Community(String communityName, String description) {
        this.communityName = communityName;
        this.description = description;
    }

    public void change(String description) {
        this.description = description;
    }
}