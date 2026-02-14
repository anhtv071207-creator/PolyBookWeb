CREATE DATABASE BookStoreDB
GO
USE BookStoreDB
GO

CREATE TABLE Users (
    id INT IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ho_ten NVARCHAR(255),
    role VARCHAR(20),
    phone VARCHAR(50),
    trang_thai BIT,
    ngay_tao DATETIME DEFAULT GETDATE()
)
GO
CREATE TABLE Address (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    dia_chi NVARCHAR(255),
    tinh_thanh NVARCHAR(100),
    quoc_gia NVARCHAR(100),
quan_huyen NVARCHAR(100),
phuong_xa NVARCHAR(100),
    CONSTRAINT FK_Address_Users
        FOREIGN KEY (user_id) REFERENCES Users(id)
)
GO

EXEC sp_rename 'Address.thanh_pho', 'tinh_thanh', 'COLUMN';
GO

ALTER TABLE Address
ADD quan_huyen NVARCHAR(100);
GO

ALTER TABLE Address
ADD phuong_xa NVARCHAR(100);
GO
ALTER TABLE Address
DROP COLUMN phone, type;
go
ALTER TABLE Address
DROP COLUMN ho_ten;
go

CREATE TABLE Categories (
    id INT IDENTITY PRIMARY KEY,
    ten_danh_muc NVARCHAR(100) NOT NULL,
    url VARCHAR(100),
    danh_muc_cha INT NULL,

    CONSTRAINT FK_Category_Parent
        FOREIGN KEY (danh_muc_cha) REFERENCES Categories(id)
)
GO

CREATE TABLE Books (
    id INT IDENTITY PRIMARY KEY,
    tieu_de NVARCHAR(255) NOT NULL,
    tac_gia NVARCHAR(255),
    ISBN VARCHAR(255),
    mo_ta NTEXT,
    gia DECIMAL(10,2),
    hang_ton INT,
    avg_rating DECIMAL(3,2),
    total_reviews INT
)
GO

CREATE TABLE Book_Categories (
    book_id INT NOT NULL,
    category_id INT NOT NULL,

    CONSTRAINT PK_Book_Categories PRIMARY KEY (book_id, category_id),

    CONSTRAINT FK_BC_Book
        FOREIGN KEY (book_id) REFERENCES Books(id),

    CONSTRAINT FK_BC_Category
        FOREIGN KEY (category_id) REFERENCES Categories(id)
)
GO

CREATE TABLE Book_Images (
    id INT IDENTITY PRIMARY KEY,
    book_id INT NOT NULL,
    url VARCHAR(255),
    bia_sach BIT,

    CONSTRAINT FK_BookImages_Book
        FOREIGN KEY (book_id) REFERENCES Books(id)
)
GO

CREATE TABLE Carts (
    id INT IDENTITY PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),

    CONSTRAINT FK_Carts_Users
        FOREIGN KEY (user_id) REFERENCES Users(id)
)
GO

ALTER TABLE Carts
DROP COLUMN session_id;

CREATE TABLE Cart_Items (
    id INT IDENTITY PRIMARY KEY,
    cart_id INT NOT NULL,
    book_id INT NOT NULL,
    so_luong INT,
    don_gia DECIMAL(10,2),

    CONSTRAINT FK_CI_Cart
        FOREIGN KEY (cart_id) REFERENCES Carts(id),

    CONSTRAINT FK_CI_Book
        FOREIGN KEY (book_id) REFERENCES Books(id)
)
GO

CREATE TABLE Orders (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    ma_don_hang VARCHAR(50),
    trang_thai BIT,
    tong_tien DECIMAL(10,2),
    dia_chi_nhan INT,
    thoi_gian_nhan DATETIME,

    CONSTRAINT FK_Orders_Users
        FOREIGN KEY (user_id) REFERENCES Users(id),

    CONSTRAINT FK_Orders_Address
        FOREIGN KEY (dia_chi_nhan) REFERENCES Address(id)
)
GO
ALTER TABLE Orders
DROP CONSTRAINT FK_Orders_Address;
GO
ALTER TABLE Orders
DROP COLUMN dia_chi_nhan;
GO
ALTER TABLE Orders
ADD ho_ten_nguoi_nhan NVARCHAR(100);
GO
Select * from Orders
go
ALTER TABLE Orders
DROP CONSTRAINT FK_Orders_Users;
GO
ALTER TABLE Orders
ALTER COLUMN user_id INT NULL;
GO
ALTER TABLE Orders
ADD CONSTRAINT FK_Orders_Users
FOREIGN KEY (user_id) REFERENCES Users(id);
GO
ALTER TABLE Orders
ADD
    quoc_gia NVARCHAR(100),
    tinh_thanh NVARCHAR(100),
    quan_huyen NVARCHAR(100),
    phuong_xa NVARCHAR(100),
    dia_chi_nhan_hang NVARCHAR(255),
    email NVARCHAR(100),
    phone NVARCHAR(20);
GO
ALTER TABLE Orders
ALTER COLUMN trang_thai SMALLINT;
GO
ALTER TABLE Orders
ADD ngay_tao DATETIME;
GO
ALTER TABLE Orders
ADD CONSTRAINT DF_Orders_NgayTao
DEFAULT GETDATE() FOR ngay_tao;
GO
ALTER TABLE Orders
ALTER COLUMN ma_don_hang VARCHAR(50) NOT NULL;
go
ALTER TABLE Orders
ADD CONSTRAINT UQ_Orders_MaDonHang UNIQUE (ma_don_hang);
Go

CREATE TABLE Order_Items (
    id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    so_luong INT,
    don_gia DECIMAL(10,2),

    CONSTRAINT FK_OI_Order
        FOREIGN KEY (order_id) REFERENCES Orders(id),

    CONSTRAINT FK_OI_Book
        FOREIGN KEY (book_id) REFERENCES Books(id)
)
GO

CREATE TABLE Payment (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    so_tien DECIMAL(10,2),
    phuong_thuc VARCHAR(50),
    trang_thai BIT,

    CONSTRAINT FK_Payment_Users
        FOREIGN KEY (user_id) REFERENCES Users(id)
)
GO
ALTER TABLE Payment
DROP CONSTRAINT FK_Payment_Users;
GO
ALTER TABLE Payment
DROP COLUMN user_id;
GO
ALTER TABLE Payment
ADD order_id INT NOT NULL;
GO
ALTER TABLE Payment
ADD CONSTRAINT FK_Payment_Orders
FOREIGN KEY (order_id) REFERENCES Orders(id);
GO
ALTER TABLE Payment
ADD ma_giao_dich VARCHAR(100) NOT NULL;
GO


CREATE TABLE Review (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    rating INT,
    noi_dung TEXT,
    phe_duyet BIT,

    CONSTRAINT FK_Review_User
        FOREIGN KEY (user_id) REFERENCES Users(id),

    CONSTRAINT FK_Review_Book
        FOREIGN KEY (book_id) REFERENCES Books(id)
)
GO

SELECt * From Users

INSERT INTO Users (email, password, ho_ten, role, phone, trang_thai)
VALUES
('admin@gmail.com', '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa', N'Admin', 'ADMIN', '0900000000', 1),
('user1@gmail.com', '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa', N'Dương Đỉnh Thiên', 'USER', '0911111111', 1),
('user2@gmail.com', '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa', N'Lưu Vũ Nguyệt', 'USER', '0922222222', 1)
Go
<--password = 123456-->
update Users set password = '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa' where id =4;
UPDATE [Users]
SET [password] = '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa'
WHERE [email] = 'admin@gmail.com';
SELECT * FROM Users
INSERT INTO Users (email, password, ho_ten, role, phone, trang_thai)
VALUES
('staff1@gmail.com', '$2a$10$618VhxEtwtnuyedIv4UzVebdUru.V68hIrOIrSluc./RDm3j5/QVa', N'Triệu Vô Cực', 'STAFF', '0933333333', 1)
Go

INSERT INTO Address (user_id,dia_chi, tinh_thanh, quoc_gia, quan_huyen, phuong_xa )
Values 
(2,N'123 Lý Thường Kiệt',N'Thành phố Hà Nội', N'Việt Nam', N'Quận Hoàn Kiếm', N'Phường Cửa Nam');
select * from Address;
SELECT * FROM Categories
GO
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Văn học', 'van-hoc', NULL), 
(N'Kinh tế', 'kinh-te', NULL),
(N'Kỹ năng', 'ky-nang', NULL),
(N'Thiếu nhi', 'thieu-nhi', NULL),
(N'Chuyên ngành', 'chuyen-nganh', NULL),
(N'Giáo khoa - Giáo trình', 'giao-khoa-giao-trinh', NULL),
(N'Tiểu sử - Hồi ký', 'tieu-su-hoi-ky', NULL),
(N'Học ngoại ngữ', 'hoc-ngoai-ngu', NULL),
(N'Lịch sử', 'lich-su', NULL)
Go

INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Tiểu thuyết', 'tieu-thuyet', 1),
(N'Truyện ngắn', 'truyen-ngan', 1),
(N'Tản văn', 'tan-van', 1),
(N'Kịch', 'kich', 1),
(N'Thơ', 'tho', 1),
(N'Phê bình văn học', 'phe-binh-van-hoc', 1),
(N'Huyền Huyễn', 'huyen-huyen', 1),
(N'Lãng mạn', 'lang-man', 1),
(N'Khoa học viễn tưởng', 'khoa-hoc-vien-tuong', 1),
(N'Tội phạm', 'toi-pham', 1),
(N'Hài hước', 'hai-huoc', 1),
(N'Kinh dị', 'kinh-di', 1)
Go
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Quản trị', 'quan-tri', 2),
(N'Tiếp thị - Bán hàng', 'tiep-thi-ban-hang', 2),
(N'Khởi nghiệp', 'khoi-nghiep', 2),
(N'Chứng khoán', 'chung-khoan', 2),
(N'Tài chính - Đầu tư', 'tai-chinh-dau-tu', 2),
(N'Tài chính - Kế toán', 'tai-chinh-ke-toan', 2),
(N'Nhân vật - Bài hoc kinh doanh', 'tai-chinh-dau-tu', 2),
(N'Phân tích kinh tế', 'phan-tich-kinh-te', 2)
Go
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Tâm lý', 'tam-ly', 3),
(N'Kỹ năng sống', 'ky-nang-song', 3),
(N'Phát triển bản thân', 'phat-trien-ban-than', 3)
Go
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Truyện tranh', 'truyen-tranh', 4),
(N'Khoa học tự nhiên', 'khoa-hoc-tu-nhien', 4),
(N'Khoa học xã hội', 'khoa-hoc-xa-hoi', 4),
(N'Kỹ năng cho trẻ', 'ky-nang-cho-tre', 4)
GO
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Âm nhạc', 'am-nhac', 5),
(N'Y học', 'y-hoc', 5),
(N'Pháp luật', 'ky-nang-cho-tre', 5),
(N'Chính trị, Triết học', 'chinh-tri-triet-hoc', 5),
(N'Tôn giáo', 'ton-giao', 5),
(N'Thể thao', 'the-thao', 5),
(N'Du lịch', 'du-lich', 5),
(N'Mỹ thuật, Kiến trúc', 'my-thuat-kien-truc', 5)
Go
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Khoa học cơ bản', 'khoa-hoc-co-ban', 5),
(N'Khoa học công nghệ', 'khoa-hoc-cong-nghe', 5),
(N'Khoa học Tự nhiên - Xã hội', 'khoa-hoc-tu-nhien-xa-hoi', 5)
GO
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Cấp 1', 'cap-1', 6),
(N'Cấp 2', 'cap-2', 6),
(N'Cấp 3', 'cap-3', 6),
(N'Đại học', 'dai-hoc', 6),
(N'Tham khảo', 'tham-khao', 6)
Go
INSERT INTO Categories (ten_danh_muc, url, danh_muc_cha)
VALUES
(N'Tiếng Anh', 'tieng-anh', 8),
(N'Tiếng Hoa', 'tieng-hoa', 8),
(N'Tiếng Nhật', 'tieng-nhat', 8),
(N'Tiếng Hàn', 'tieng-han', 8),
(N'Tiếng Pháp', 'tieng-phap', 8),
(N'Tiếng Đức', 'tieng-duc', 8),
(N'Ngoại ngữ khác', 'ngoai-ngu-khac', 8)
Go
INSERT INTO Books (tieu_de, tac_gia, ISBN, mo_ta, gia, hang_ton, avg_rating, total_reviews)
VALUES
(N'Nhà giả kim', N'Paulo Coelho', '	9786043199789', N'Hành trình theo đuổi vận mệnh của chàng chăn cừu Santiago. Một cuốn sách truyền cảm hứng mãnh liệt về việc lắng nghe trái tim và đừng bao giờ từ bỏ ước mơ.', 89000, 50, 4.5, 2),
(N'Đắc nhân tâm', N'Dale Carnegie', '9786043351668', N'Cuốn sách nghệ thuật ứng xử kinh điển mọi thời đại. Cung cấp những nguyên tắc vàng để thu phục lòng người và xây dựng mối quan hệ bền vững trong cuộc sống.', 110000, 100, 4.8, 3),
(N'Tư duy nhanh và chậm', N'Daniel Kahneman', '	9780141033570', N'Cuốn sách khám phá hai hệ thống vận hành bộ não gồm phản xạ bản năng và suy luận logic. Tác giả chỉ ra các sai lầm trong nhận thức giúp bạn đưa ra những quyết định sáng suốt và lý trí hơn.', 339000, 30, 4.6, 2),
(N'Lược sử thời gian', N'Stephen Hawking', '9786041204232', N'Cuốn sách mở ra hành trình khám phá các bí ẩn của vũ trụ từ vụ nổ Big Bang đến các lỗ đen kỳ bí. giải thích những khái niệm phức tạp về không gian và thời gian bằng ngôn ngữ đơn giản và dễ hiểu nhất.', 115000, 100, 4.7, 4),
(N'Người Bán Hàng Vĩ Đại Nhất Thế Giới', N'Og Mandino', '9786048896454', N'Cuốn sách chia sẻ những triết lý sống và nguyên tắc thành công thông qua câu chuyện về 10 cuốn da dê cổ xưa. Đây là nguồn cảm hứng mạnh mẽ giúp bạn thay đổi tư duy, rèn luyện tính kỷ luật và xây dựng những thói quen tốt để đạt được mục tiêu trong cuộc sống.', 148000, 100, 4.5, 3),
(N'Giết Con Chim Nhại', N'Harper Lee', '9786049769443', N'Câu chuyện được kể qua góc nhìn của cô bé Scout về những vấn đề nhạy cảm như phân biệt chủng tộc và sự bất công tại miền Nam nước Mỹ. Cuốn sách là bài học sâu sắc về lòng nhân ái, sự tử tế và lòng dũng cảm bảo vệ lẽ phải trước những định kiến khắc nghiệt của xã hội.', 129000, 100, 4.6, 5)
Go
INSERT INTO Book_Categories (book_id, category_id)
SELECT 1, id FROM Categories WHERE url = 'tieu-thuyet';
GO
INSERT INTO Book_Categories (book_id, category_id)
SELECT 2, id FROM Categories WHERE url = 'phat-trien-ban-than';
GO
INSERT INTO Book_Categories (book_id, category_id)
SELECT 3, id FROM Categories WHERE url = 'tam-ly';
GO
INSERT INTO Book_Categories (book_id, category_id)
SELECT 4, id FROM Categories WHERE url = 'khoa-hoc-tu-nhien';
Go
INSERT INTO Book_Categories (book_id, category_id)
SELECT 5, id FROM Categories WHERE url = 'tiep-thi-ban-hang';
Go
INSERT INTO Book_Categories (book_id, category_id)
SELECT 6, id FROM Categories WHERE url = 'tieu-thuyet';
Go

INSERT INTO Book_Images (book_id, url, bia_sach)
VALUES 
(1, '/books/nha-gia-kim.jpg', 1),
(2, '/books/dac-nhan-tam.jpg', 1),
(3, '/books/tu-duy-nhanh-va-cham.jpg', 1),
(4, '/books/luoc-su-thoi-gian.jpg', 1),
(5, '/books/nguoi-ban-hang-vi-dai-nhat-the-gioi.jpg', 1),
(6, '/books/giet-con-chim-nhai.jpg', 1)
Use BookStoreDB
SELECT * FROM Books;
SELECT * FROM Orders;
SELECT * FROM Order_Items;
SELECT * from users

SELECT 
    c.name AS column_name,
    dc.name AS default_name,
    dc.definition AS default_definition
FROM sys.columns c
LEFT JOIN sys.default_constraints dc 
    ON c.default_object_id = dc.object_id
WHERE c.object_id = OBJECT_ID('Orders')
  AND c.name = 'ngay_tao';

  SELECT 
    name, 
    is_nullable
FROM sys.columns
WHERE object_id = OBJECT_ID('Orders')
AND name = 'ngay_tao';
go
UPDATE Orders
SET ngay_tao = GETDATE()
WHERE ngay_tao IS NULL;
go
ALTER TABLE Orders
ALTER COLUMN ngay_tao DATETIME NOT NULL;
go
ALTER TABLE Orders
DROP COLUMN thoi_gian_nhan;
GO
USE BookStoreDB
SELECT * From Books
SELECT * FROM Users
SELECT * From Address
SELECT * From Carts
SELECT * From Cart_Items





