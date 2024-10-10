package org.zerock.api2.product.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.api2.product.domain.Product;
import org.zerock.api2.product.domain.ProductStatus;
import org.zerock.api2.product.domain.QProduct;
import org.zerock.api2.product.domain.QReview;
import org.zerock.api2.product.dto.ProductListDTO;

import java.util.List;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public Page<Product> list1(Pageable pageable) {

        QProduct product =QProduct.product;
        JPQLQuery<Product> query = from(product);
        query.where(product.status.eq(ProductStatus.SALE));
        query.where(product.pno.gt(0L));

        this.getQuerydsl().applyPagination(pageable, query);

        List<Product> list = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<Product> list2(String keyword, ProductStatus status, Pageable pageable) {
        QProduct product =QProduct.product;
        JPQLQuery<Product> query = from(product);
        query.where(product.pno.gt(0L));

        if(keyword != null) {
            query.where(product.pname.like("%"+keyword+"%"));
        }

        if(status != null) {
            query.where(product.status.eq(status));
        }

        this.getQuerydsl().applyPagination(pageable, query);

        List<Product> list = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<Product> listWithReplyCount(Pageable pageable) {

        QProduct product =QProduct.product;
        QReview review =QReview.review;

        JPQLQuery<Product> query = from(product);
        query.leftJoin(review).on(review.product.eq(product));
        query.groupBy(product);

        this.getQuerydsl().applyPagination(pageable, query);

        //
        JPQLQuery<ProductListDTO> dtoJPQLQuery =query.select(
                Projections.bean(ProductListDTO.class,
                        product.pno,
                        product.pname,
                        product.price,
                        review.count().as("reviewCnt"),
                        review.score.avg().coalesce(0.0).as("avgScore")
                )
        );

        log.info(dtoJPQLQuery);

        java.util.List<ProductListDTO> dtoList = dtoJPQLQuery.fetch();

        dtoList.forEach(log::info);
        return null;

    }
}
