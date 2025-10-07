# English Learning Platform - Authentication System

## Tổng quan
Hệ thống xác thực hoàn chỉnh với các tính năng:
- ✅ Đăng ký tài khoản với xác thực email
- ✅ Đăng nhập với JWT token
- ✅ Gửi email xác thực qua Gmail
- ✅ Bảo mật với Spring Security
- ✅ Validation dữ liệu đầu vào
- ✅ Exception handling toàn cục

## Cấu hình Email Gmail

### 1. Tạo App Password cho Gmail
1. Đăng nhập Gmail → Quản lý tài khoản Google
2. Bảo mật → Xác minh 2 bước (bật nếu chưa có)
3. Mật khẩu ứng dụng → Chọn "Mail" → Tạo
4. Sao chép mật khẩu 16 ký tự được tạo

### 2. Cập nhật application-dev.yml
```yaml
spring:
  mail:
    username: your-email@gmail.com     # Email thật của bạn
    password: your-16-char-app-password # App password vừa tạo
```

## API Endpoints

### 1. Đăng ký tài khoản
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@gmail.com",
  "password": "123456",
  "fullname": "Nguyen Van Test"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.",
  "data": null
}
```

### 2. Đăng nhập
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrUsername": "test@gmail.com",
  "password": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "testuser",
    "email": "test@gmail.com",
    "fullname": "Nguyen Van Test",
    "role": "USER",
    "emailVerified": false
  }
}
```

### 3. Xác thực email
```http
GET /api/auth/verify-email?token=uuid-token-here
```

### 4. Gửi lại email xác thực
```http
POST /api/auth/resend-verification?email=test@gmail.com
```

### 5. Sử dụng JWT Token
Thêm header Authorization cho các API cần xác thực:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Cơ sở dữ liệu

### Bảng user (đã cập nhật)
- Thêm `is_email_verified` (BOOLEAN)
- Thêm `email_verification_token` (VARCHAR)
- Thêm `email_verification_token_expires_at` (DATETIME)

### Bảng email_verification_token (mới)
- `id` (PRIMARY KEY)
- `token` (UNIQUE)
- `user_id` (FOREIGN KEY)
- `expires_at`
- `confirmed_at`
- `created_at`

## Kiểm tra hoạt động

### 1. Khởi động ứng dụng
```bash
mvn spring-boot:run
```

### 2. Truy cập Swagger UI
http://localhost:8081/swagger-ui/html

### 3. Test flow hoàn chỉnh
1. Đăng ký tài khoản mới
2. Kiểm tra email nhận được
3. Click link xác thực trong email
4. Đăng nhập với tài khoản đã xác thực
5. Sử dụng JWT token để truy cập API protected

## Bảo mật

### JWT Configuration
- Secret key: 64 ký tự mạnh
- Expiration: 24 giờ
- Algorithm: HS256

### Password Encoding
- BCrypt với strength mặc định
- Salt tự động sinh

### CORS Policy
- Cho phép tất cả origins trong development
- Cần cấu hình cụ thể cho production

## Lưu ý quan trọng

1. **Email Configuration**: Phải cấu hình đúng Gmail App Password
2. **Database**: Đảm bảo MySQL đang chạy và có database `english_app`
3. **JWT Secret**: Thay đổi secret key trong production
4. **CORS**: Cấu hình CORS phù hợp với frontend domain
5. **SSL**: Bật HTTPS trong production

## Troubleshooting

### Lỗi email không gửi được
- Kiểm tra Gmail App Password
- Kiểm tra cấu hình SMTP
- Xem logs để debug

### Lỗi JWT
- Kiểm tra secret key
- Kiểm tra expiration time
- Verify token format

### Lỗi database
- Kiểm tra connection string
- Đảm bảo user có quyền CREATE TABLE
- Xem logs Hibernate
