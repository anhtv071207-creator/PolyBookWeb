package com.example.polybookbe.dto;

import com.example.polybookbe.entity.BookImage;
import com.example.polybookbe.entity.OrderItem;
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

    public OrderItemResponse(OrderItem item) {
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
        this.thanhTien = item.getDonGia()
                .multiply(BigDecimal.valueOf(item.getSoLuong()));
    }
}
