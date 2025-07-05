package com.thespace.spaceweb.category;

import com.thespace.common.BaseEntity;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.community.Community;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @ManyToOne
    @JoinColumn(name = "Community_Id")
    private Community community;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> board = new ArrayList<>();

    public Category() {
    }

    @Builder
    public Category(String name, String type, Community community, List<Board> board) {
        this.name = name;
        this.type = type;
        this.community = community;
        this.board = board;
    }
}
