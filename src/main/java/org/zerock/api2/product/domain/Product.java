package org.zerock.api2.product.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "tags")
@Table(name = "tbl_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;

    private ProductStatus status;

    @ElementCollection
    @CollectionTable(name = "tbl_product_tag")
    @Builder.Default
    @BatchSize(size = 100)
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name= "tbl_product_img")
    private Set<ContentImage> images = new HashSet<>();

    public void addTag(String tag) {
        tags.add(tag);
    }
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    public void clearTags() {
        tags.clear();
    }
}
