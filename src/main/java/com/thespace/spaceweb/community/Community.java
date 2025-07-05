package com.thespace.spaceweb.community;

import com.thespace.common.BaseEntity;
import com.thespace.spaceweb.board.Board;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Column(columnDefinition = "longtext")
    private String description;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<Category> category = new ArrayList<>(); //Add limit?

    @OneToMany(mappedBy = "community")
    private List<Board> board = new ArrayList<>();

    public Community() {

    }

    @Builder
    public Community(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void change(String description) {
        this.description = description;
    }
}