package com.example.polybookbe.dto;

import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.entity.OrderItem;
import com.example.polybookbe.entity.Promotion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {

    private Integer bookId;
    private String tieuDe;
    private String anhBia;

    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private BigDecimal giaGoc;
    private Boolean isPromotion;

    public OrderItemResponse(OrderItem item, Promotion promotion) {
        this.bookId = item.getBook().getId();
        this.tieuDe = item.getBook().getTieuDe();

        this.anhBia = item.getBook()
                .getImages()
                .stream()
                .filter(BookImage::getBiaSach)
                .map(BookImage::getUrl)
                .findFirst()
                .orElse(null);

        this.soLuong = item.getSoLuong();
        this.donGia = item.getDonGia();

        // ✅ LOGIC KHUYẾN MÃI
        if (promotion != null) {
            this.isPromotion = true;
            this.giaGoc = item.getBook().getGia();
        } else {
            this.isPromotion = false;
            this.giaGoc = null;
        }

        this.thanhTien = item.getDonGia()
                .multiply(BigDecimal.valueOf(item.getSoLuong()));
    }
}
