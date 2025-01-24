package com.thespace.thespace.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String path;

    @Column(unique = true)
    private String categoryName;

    private String categoryType;

    @ManyToOne
    @JoinColumn(name = "Community_Id")
    private Community community;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> board = new ArrayList<>();

    public Category() {
    }

    @Builder
    public Category(String path, String categoryName, String categoryType, Community community, List<Board> board) {
        this.path = path;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.community = community;
        this.board = board;
    }
}
