package com.example.polybookbe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBookRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;

    @NotBlank(message = "Tác giả không được để trống")
    private String tacGia;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải > 0")
    private BigDecimal gia;

    @NotBlank(message = "ISBN không được để trống")
    @Pattern(
            regexp = "^(\\d{10}|\\d{13})$",
            message = "ISBN phải gồm 10 hoặc 13 chữ số"
    )
    private String isbn;

    @NotNull(message = "Hàng tồn không được để trống")
    @Min(value = 0, message = "Hàng tồn phải >= 0")
    private Integer hangTon;
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String moTa;

    @NotBlank(message = "Ảnh bìa không được để trống")
    private String coverImageUrl;

    @NotEmpty(message = "Phải chọn ít nhất 1 danh mục")
    private List<Integer> categoryIds;

    @NotNull(message = "Số trang không thể để trống")
    @Min(value = 1, message = "Số trang phải > 0")
    private Integer soTrang;

    @NotBlank(message = "NXB không được để trống")
    private String nxb;

    @NotNull(message = "Năm xuất bản không thể để trống")
    @Min(value = 1900, message = "Năm xuất bản không hợp lệ")
    private Integer namXuatBan;

    @NotBlank(message = "Kích thước không thể để trống")
    private String kichThuoc;

    @NotBlank(message = "Hình thức không thể để trống")
    private String hinhThuc;

    @NotNull(message = "Trọng lượng không thể để trống")
    @DecimalMin(value = "0.0", message = "Trọng lượng phải >= 0")
    private Integer trongLuong;

    @NotBlank(message = "Tên nhà cung cấp không thể để trống")
    private String tenNhaCungCap;


    private String ngonNgu;

    private String dichGia;
}