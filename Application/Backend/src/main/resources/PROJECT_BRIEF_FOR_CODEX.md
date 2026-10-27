# Conservatoire Virtuel — Project brief for Codex in IntelliJ IDEA

> Tài liệu bàn giao độc lập, cập nhật ngày 2026-10-27.
> Mục tiêu: cung cấp đầy đủ đề bài, phạm vi người dùng đã chọn và kiến thức của nhóm để Codex hỗ trợ đúng mức Java OOP nhập môn.
> Phụ lục cuối file chứa toàn bộ nội dung tiếng Anh đã trích từ đề bài PDF 5 trang.

## 1. Cách sử dụng và phân biệt nguồn yêu cầu

Đọc toàn bộ tài liệu trước khi thiết kế hoặc sửa code. Khi có repository, đọc cấu trúc và các hướng dẫn của repository trước; xác định những gì đã có rồi thực hiện yêu cầu mới của người dùng.

Các nhãn dưới đây có ý nghĩa khác nhau:

- **[ĐỀ BÀI]**: nội dung bắt buộc trong Conservatoire-Virtuel-Project.pdf.
- **[BONUS]**: mục tùy chọn trong PDF; không tự biến thành yêu cầu bắt buộc.
- **[NGƯỜI DÙNG]**: lựa chọn và giới hạn đã thống nhất trong cuộc trao đổi.
- **[HƯỚNG DẪN BẢO VỆ]**: nội dung từ các ảnh kế hoạch thuyết trình và rubric do người dùng cung cấp.
- **[GIẢ ĐỊNH HIỆN TẠI]**: chính sách triển khai cho phần đề chưa quy định rõ; có thể thay đổi khi nhóm hoặc giảng viên chốt lại.
- **[THIẾT KẾ THAM KHẢO]**: cách tổ chức của bản mẫu đã có; không phải tên class/bảng hay số lượng tối thiểu do giảng viên bắt buộc.

Không dùng các lựa chọn của bản mẫu để suy ra đề bắt buộc phải có 16 bảng, 3 abstract class hoặc một số lượng phương thức cố định. Số 322 trong một bản thống kê trước là tổng các khai báo public, gồm constructor/getter/hàm khác/interface; không phải 322 constructor hoặc 322 chức năng cần phân công.

Tài liệu này là ngữ cảnh dự án. Yêu cầu công việc cụ thể do người dùng giao trong IntelliJ sẽ quyết định phần cần thực hiện. Không tự tạo lại toàn bộ dự án hoặc thay đổi kiến trúc đang chạy chỉ vì nhận được file này.

## 2. Bối cảnh và mục tiêu

**[ĐỀ BÀI]** Một trường nhạc tư nhân tên Conservatoire Virtuel muốn xây dựng hệ thống thông tin để hỗ trợ hoạt động nội bộ:

1. Quản lý học viên và giáo viên.
2. Quản lý gói học và buổi học lẻ.
3. Xếp lịch học, đặt phòng và nhạc cụ.
4. Theo dõi học phí, thanh toán và số dư còn nợ.
5. Tổ chức kỳ thi và lưu kết quả.

Hệ thống cần thể hiện thiết kế phần mềm và database tốt.

**[NGƯỜI DÙNG]**

- Đây là project môn Java OOP cơ bản, thời gian thực hiện 3 tuần; chưa có ngày bắt đầu/deadline cụ thể được chốt trong tài liệu.
- Code phải dễ đọc, dễ tự sửa và dễ giải thích khi bảo vệ.
- Dùng Java thuần; backend có model, service, controller và chia package rõ ràng.
- Có menu console để chạy và demo theo đề.
- Người dùng đã chọn MySQL.
- Chưa làm GUI.
- Code, tên class/phương thức và các tài liệu kỹ thuật chính dùng tiếng Anh; giải thích học tập có thể dùng tiếng Việt.

## 3. Yêu cầu chức năng đầy đủ

### FR-01. Học viên

**[ĐỀ BÀI — PDF trang 1]**

Mỗi học viên có một ID duy nhất và các thông tin:

- Họ, tên.
- Địa chỉ.
- Ngày sinh.
- Số điện thoại.
- Email.
- Trình độ: Beginner / Intermediate / Advanced.
- Các nhạc cụ yêu thích.

Học viên có thể đăng ký:

- Một Course Package gồm N buổi và các ngày hiệu lực.
- Các buổi học lẻ được tính tiền theo từng buổi.

Phải theo dõi:

- Số giờ đã mua.
- Số giờ còn lại.
- Lịch sử sử dụng giờ.

### FR-02. Giáo viên

**[ĐỀ BÀI — PDF trang 2]**

Mỗi giáo viên có:

- ID, tên.
- Các chuyên môn nhạc cụ.
- Mức tiền theo giờ.
- Bằng cấp/chứng chỉ.
- Thời gian rảnh.

Một giáo viên có thể dạy nhiều loại nhạc cụ. Khi xếp lịch phải kiểm tra chuyên môn phù hợp và thời gian rảnh.

### FR-03. Gói học và dịch vụ

**[ĐỀ BÀI — PDF trang 2]**

Trường cung cấp:

- Gói học nhạc hữu hạn.
- Gói học không giới hạn.
- Buổi học nhóm hoặc cá nhân.
- Buổi học lẻ trả tiền từng buổi.
- Thuê nhạc cụ.
- Đặt/thuê phòng.

Mỗi dịch vụ được mua phải được theo dõi và có thể tính phí.

### FR-04. Xếp lịch và tài nguyên

**[ĐỀ BÀI — PDF trang 2]**

Cho phép xếp lịch:

- Buổi học có giáo viên + học viên + phòng + nhạc cụ.
- Thuê phòng.
- Kỳ thi.

Mỗi hoạt động lưu:

- Ngày và giờ bắt đầu.
- Thời lượng.
- Các tài nguyên được gán.
- Trạng thái: scheduled / completed / cancelled.

Phải ngăn trùng lịch. Trong thiết kế hiện tại, việc kiểm tra bao gồm giáo viên, phòng, nhạc cụ vật lý và học viên ở các hoạt động liên quan. Thuê nhạc cụ cũng cần được theo dõi để tránh đặt trùng tài nguyên.

### FR-05. Kỳ thi và kết quả

**[ĐỀ BÀI — PDF trang 2]**

- Kỳ thi có tên, nhạc cụ/môn thi, ngày thi và sức chứa.
- Học viên có thể đăng ký.
- Sau đó ghi kết quả: pass/fail và điểm số tùy chọn.

Phải phân biệt chưa có kết quả, kết quả trượt, không nhập điểm và điểm bằng 0.

### FR-06. Thanh toán và học phí

**[ĐỀ BÀI — PDF trang 3]**

Theo dõi:

- Các khoản đã thanh toán.
- Số dư còn nợ.
- Giá gói học, thuê và buổi học.

Cung cấp:

- Tổng tiền một học viên đã chi.
- Tổng hợp theo tháng hoặc theo học viên.

**[THIẾT KẾ THAM KHẢO]** Tách Charge (khoản phải thu) và Payment (khoản thực nhận). Mua gói tạo khoản phải thu; thanh toán là thao tác riêng. Không thu phí một lesson lần nữa nếu lesson đã được tài trợ bằng gói.

## 4. Bốn quy tắc nghiệp vụ bắt buộc

**[ĐỀ BÀI — PDF trang 3]**

- **BR-01:** Giờ trong gói hết hiệu lực ở ngày kết thúc gói.
- **BR-02:** Hủy lesson dưới 24 giờ trước lúc bắt đầu thì vẫn tính là đã sử dụng.
- **BR-03:** Đóng đăng ký thi khi đạt sức chứa tối đa.
- **BR-04:** Một lesson nhóm tiêu thụ một giờ từ mỗi người tham gia.

Các trường hợp biên như đúng 24 giờ, hủy sau khi bắt đầu, cách hoàn tiền và ranh giới ngày cần được phân biệt với nguyên văn đề; các lựa chọn hiện tại nằm ở mục 9.

## 5. Yêu cầu OOP bắt buộc

**[ĐỀ BÀI — PDF trang 3–4]**

### OOP-01. Abstract classes

- Ít nhất **2 abstract class**.
- Dùng đúng để biểu diễn khái quát hóa và hành vi chung.
- Có concrete class triển khai hành vi cụ thể.

### OOP-02. Interfaces

- Ít nhất **2 interface**.
- Biểu diễn các khả năng/hành vi độc lập.
- Có lớp triển khai và chỗ sử dụng thực tế.

### OOP-03. Polymorphism

Phải chứng minh được cả:

- Collection đa hình.
- Lời gọi phương thức đa hình.
- Override và dynamic dispatch.

Không chỉ khai báo extends/implements mà không dùng.

### OOP-04. Copy constructors

- Có copy constructor cho nhiều class quan trọng.
- Hỗ trợ sao chép và bảo vệ trạng thái nội bộ.
- Hiểu rõ sao chép reference, shallow copy và copy các đối tượng sở hữu bên trong.
- Copy giữ ID như một snapshot; muốn thêm một thực thể mới vào hệ thống phải cấp ID mới.

### OOP-05. Good OOP practice

Thể hiện đúng:

- Inheritance.
- Composition.
- Encapsulation.
- Abstract type so với concrete type.

Các yêu cầu OOP phải xuất hiện trong **UML, Java code và phần mô tả báo cáo**.

**[HƯỚNG DẪN BẢO VỆ / RUBRIC]** Chuẩn bị giải thích thêm association, aggregation, cardinalities và defensive copies.

**[THIẾT KẾ THAM KHẢO]**

- Person -> Student, Teacher.
- Activity -> Lesson, Rental, Exam.
- CoursePackage -> FinitePackage, UnlimitedPackage.
- Billable: khả năng có giá/diễn giải.
- Schedulable: khả năng có lịch/tài nguyên.
- Teacher sở hữu các TimeSlot; Enrollment sở hữu bản sao gói đã mua; ExamRegistration sở hữu kết quả tùy chọn.
- Các tên và số lượng trên là lựa chọn hiện tại, không phải bắt buộc phải giữ nguyên nếu nhóm thống nhất thiết kế đơn giản hơn và vẫn đủ đề.

## 6. Sản phẩm phải nộp và tiêu chí đánh giá

**[ĐỀ BÀI — PDF trang 4–5]**

### 6.1. UML

Phải có đủ:

1. Class Diagram.
2. Sequence Diagram cho xếp lịch.
3. Activity Diagram cho đăng ký thi.

ERD là tài liệu database bổ sung, không thay thế ba UML trên.

### 6.2. Java application

Ứng dụng console với text menu hỗ trợ:

1. Học viên và giáo viên.
2. Gói học và lesson cá nhân.
3. Xếp lịch và booking.
4. Thanh toán và học phí.
5. Kỳ thi và kết quả.

Có đủ dữ liệu kiểm thử để demo các chức năng.

### 6.3. Use-case scenario chuẩn bị trước

Phải có dữ liệu ban đầu:

- Học viên.
- Giáo viên.
- Gói học.
- Nhạc cụ.
- Phòng.
- Lesson đã xếp lịch.
- Kỳ thi.

### 6.4. Báo cáo và thuyết trình

Báo cáo phải có:

1. Mô tả dự án.
2. UML.
3. Lý do thiết kế.
4. Ảnh chụp chương trình thực thi.
5. Kịch bản kiểm thử.
6. Thảo luận các yêu cầu OO.

Ảnh thực thi và kết quả thử cần lấy từ chương trình chạy thực tế.

### 6.5. Bonus tùy chọn trong PDF

- Implement database.
- GUI bằng Swing/JavaFX.
- Unit tests.

**[NGƯỜI DÙNG]** MySQL nằm trong phạm vi đã chọn; GUI chưa làm. Bản mẫu có test Java thuần. PDF không chỉ định bắt buộc JUnit.

### 6.6. Tiêu chí đánh giá bằng lời trong PDF

- Tính đúng đắn của mô hình/nghiệp vụ.
- Chất lượng code và thiết kế OO.
- Dùng đúng abstract class, interface, polymorphism và copy constructor.
- UML chuyên nghiệp.
- Báo cáo và thuyết trình rõ ràng, có tổ chức.

## 7. Nội dung và thời gian bảo vệ

**[HƯỚNG DẪN BẢO VỆ — các ảnh người dùng cung cấp, không phải số phút ghi trong PDF]**

1. **Introduction — 1 phút:** bối cảnh trường nhạc, nhu cầu số hóa, nhiệm vụ và sản phẩm đã xây.
2. **System Overview — 2 phút:** chức năng tổng thể, các thực thể chính, lợi ích về lập lịch/theo dõi/học phí.
3. **Architecture & Layering — 3 phút:** module, lý do tách trách nhiệm và luồng tương tác.
4. **UML & OOP Design — 5 phút:** cấu trúc lớp, quan hệ, abstraction/interface/polymorphism/copy/encapsulation; minh họa ngắn, không đọc code từng dòng.
5. **Live Demo — 6–7 phút:** chạy luồng đầu cuối bên dưới.
6. **Management, Testing & Challenges — 2 phút:** giai đoạn phát triển, chia việc, versioning/documentation, cách test, khó khăn và giải pháp thực tế.
7. **Conclusion & Future Work — 1 phút:** kết quả, lợi ích, bài học kỹ thuật và hướng phát triển.
8. **Q&A / Buffer — 2 phút:** trao đổi với hội đồng.

Tổng các phần cộng lại là **22–23 phút gồm Q&A**, hoặc **20–21 phút chưa gồm Q&A**. Đây là phép cộng từ ảnh hướng dẫn, không xác nhận một giới hạn thời gian chính thức khác; nhóm cần chốt với giảng viên.

### Luồng demo được đề xuất trong ảnh

1. Mở ứng dụng và menu.
2. Hiện dữ liệu sẵn: học viên, giáo viên, phòng.
3. Đăng ký/mua gói cho học viên; hiện số giờ và ngày hiệu lực.
4. Xếp lesson; chứng minh kiểm tra chuyên môn và availability.
5. Cố tình đặt trùng; chứng minh bị từ chối.
6. Hủy dưới 24 giờ; chứng minh quy tắc tính giờ.
7. Đăng ký thi và ghi kết quả.
8. Ghi thanh toán và hiện số dư mới.
9. Gọi phương thức trên collection đa hình chứa nhiều lớp cụ thể.
10. Demo sao chép bằng copy constructor.

Mỗi thao tác phải được liên hệ rõ bằng lời với yêu cầu đang chứng minh. Để demo kết quả thi đúng trình tự, dùng kỳ thi đã hoàn thành hoặc nói rõ sử dụng đồng hồ mô phỏng; không bỏ kiểm tra ngày/giờ chỉ để demo chạy.

## 8. Rubric điểm từ ảnh và điểm chưa rõ

**[HƯỚNG DẪN BẢO VỆ — ảnh rubric số 1]**

Các số trong tiêu đề cột:

- Presentation: 4.
- Design: 8.
- Subscribe/manage candidate registrations: 4.
- Manage candidate payments: 4.
- Manage learning hours schedule: 6.
- Manage school trainers and instruments: 4.
- Manage exams and results: 4.
- Prepared use case: 4.
- Bonus (database + GUI + unit tests): 10, chung một block.
- Report: 2.

Hàng số bên dưới lần lượt ghi: **4, 8, 4, 4, 4, 4, 4, 4, 10, 4**.

Vì thế Schedule là **6 ở tiêu đề / 4 ở hàng số**; Report là **2 / 4**. Giữ sự khác nhau này để xác nhận với giảng viên. Không tự kết luận hàng nào là điểm tối đa hoặc điểm đã đạt.

Cả hai cách cộng đều cho 50, gồm phần không bonus 40 và block bonus 10. Phép cộng không đủ để xác nhận thang /50, tỷ lệ phần trăm hoặc cách quy đổi /20. Không hiểu block 10 là 10 điểm cho từng tính năng bonus.

Candidate trong ảnh được đối chiếu với Student; trainer với Teacher.

## 9. Giả định nghiệp vụ của bản hiện tại

Những mục dưới đây **không phải toàn bộ là quy định nguyên văn của giảng viên**:

1. Mỗi lesson cá nhân/nhóm dài 60 phút; nhóm có ít nhất 2 người. Rental/exam có thời lượng riêng.
2. Ngày đầu và ngày cuối của gói đều được tính; toàn bộ lesson phải nằm trong khoảng hiệu lực theo Europe/Paris.
3. Chọn rõ một enrollment tài trợ cho từng người tham gia; không tự chia một lesson qua nhiều gói.
4. Gói có thể dùng trước khi trả đủ tiền. Mua tạo Charge, trả tiền tạo Payment.
5. Unlimited không có số giờ giả như 999999; vẫn lưu usage/history; thuê là dịch vụ riêng.
6. Đặt lịch giữ giờ RESERVED; hoàn thành hoặc hủy muộn chuyển CONSUMED; hủy sớm chuyển RELEASED.
7. Đúng 24 giờ được xem là hủy sớm và trả giờ. Hủy tại/sau thời điểm bắt đầu bị từ chối; hủy nhóm áp dụng cho cả nhóm.
8. Không tự hoàn tiền khi hủy. Việc trả giờ của gói khác với việc hoàn tiền; giữ lịch sử khoản phí.
9. Availability là các khoảng ngày/giờ cụ thể, chưa có lịch lặp tự động.
10. Một phòng cho lesson/exam/thuê phòng; thuê riêng nhạc cụ không cần phòng. Giáo viên bắt buộc cho lesson; chưa tự thêm yêu cầu giám khảo kỳ thi.
11. Nhạc cụ là tài sản vật lý của trường; enum loại hiện có PIANO/GUITAR/VIOLIN/FLUTE/DRUMS. Sở thích học viên là thông tin tham khảo.
12. Lesson có ít nhất một nhạc cụ phù hợp; nhóm có thể chia sẻ nhạc cụ theo giả định hiện tại.
13. Kết quả chỉ được ghi cho người đã đăng ký sau khi kỳ thi kết thúc và được đánh dấu completed; pass/fail nhập tường minh, không suy từ điểm.
14. Điểm là tùy chọn, không âm và phù hợp DECIMAL(10,4). Đây là giới hạn lưu trữ, không phải thang điểm hay ngưỡng đỗ.
15. Tiền EUR lưu bằng cents. Giá lesson lẻ tham khảo hiện là 40 EUR/người. Giá thuê prorate theo phút, làm tròn HALF_UP.
16. Rate giáo viên không tự động bằng học phí bán cho học viên.
17. Payment dương, không vượt tổng nợ; chưa có thuế/giảm giá/refund/phân bổ tiền cho từng hóa đơn.
18. ID phân biệt hoa/thường, 1–40 ký tự chữ/số/gạch dưới/gạch ngang. Không xóa lịch sử; chưa có reschedule, thường hủy rồi tạo lịch mới.
19. App là một phiên console, một luồng; lưu/đọc MySQL thủ công cho cả phiên. Không phải hệ thống nhiều người cùng cập nhật.
20. Không thêm login/RBAC, web server, REST API, online payment hay tích hợp lịch nếu người dùng chưa yêu cầu.

Nếu nhóm thay đổi các giả định này, cập nhật code, UML, schema, test và tài liệu cùng nhau.

## 10. Mức kiến thức Java và cách viết code

**[NGƯỜI DÙNG]** Dựa trên các bài thực hành đã học:

- Tuto1_EN (2).pdf: class/object, thuộc tính/phương thức, constructor, this, copy/reference.
- Tuto2.pdf: encapsulation, private, getter/setter, toString, equals/==, copy đối tượng lồng nhau, mảng và for/for-each.
- Tuto3.pdf: inheritance, extends, super, override, overload, polymorphism và tập hợp lớp con.
- Tuto4.pdf: abstract class/method, interface, implements và dynamic dispatch.

Trong PDF đề bài có cụm "advanced OOP concepts"; vẫn phải đủ các yêu cầu OOP cụ thể ở mục 5. Không suy diễn rằng cần thuật toán hoặc framework phức tạp.

Ưu tiên:

- Java thuần; class nhỏ có trách nhiệm rõ.
- ArrayList/List và vòng lặp for/for-each.
- if/else rõ ràng; tìm kiếm tuần tự và so sánh khoảng thời gian.
- Constructor, getter, override và copy constructor tường minh.
- JDBC với PreparedStatement, setters và while(ResultSet.next()).
- Kiểm tra điều kiện rồi báo lỗi có ý nghĩa.

Tránh tự đưa vào Spring/Spring Boot, Hibernate/JPA, microservices, DI framework, custom generic repositories, lambda/Stream/callback phức tạp, concurrency hay design pattern không cần thiết.

Thư viện chuẩn có thể dùng khi cần: java.time, enum, BigDecimal, JDBC và try/catch/try-with-resources. Giải thích ngắn các API thêm ngoài tutorial khi chúng xuất hiện. Không bắt buộc mọi class có default constructor hoặc setter cho mọi field.

Các hướng dẫn "làm cá nhân", "không dùng IDE", "làm trên giấy" trong Tuto là quy định bài thực hành tương ứng; không tự biến chúng thành quy định mới cho project này.

## 11. Cấu trúc và lưu trữ tham khảo

**[THIẾT KẾ THAM KHẢO]** Bản mẫu hiện có dùng Java 17, JDK tools, không cần Maven:

~~~text
src/main/java/com/conservatoire/
  Main.java
  model/
  repository/
  service/
  controller/
  view/
  bootstrap/
  util/
  exception/
src/test/java/com/conservatoire/
database/
docs/
lib/
~~~

- Controller nhận input/menu rồi gọi service.
- Service kiểm tra quy tắc và điều phối thao tác.
- SchoolStore giữ các ArrayList có kiểu cụ thể và bảo vệ dữ liệu bằng bản copy.
- SchoolApplication tạo đối tượng bằng new và truyền qua constructor.
- MySQL dùng JDBC để Save/Load thủ công.
- Mỗi thao tác nghiệp vụ thay đổi RAM trước. Chưa Save mà thoát thì thay đổi RAM mất.
- Save thay dữ liệu của ứng dụng trong database riêng bằng một transaction; lỗi thì rollback.
- Load dựng store mới đầy đủ rồi mới thay phiên hiện tại.
- Không ghi mật khẩu vào source. Bản hiện tại đọc CV_DB_URL, CV_DB_USER, CV_DB_PASSWORD.
- Không giả định có HTTP controller, server hoặc account tự phục vụ của học viên.

### Database hiện có

Schema tham khảo tên conservatoire_oop, gồm:

1. student: hồ sơ học viên.
2. student_preference: các loại nhạc cụ học viên thích.
3. teacher: hồ sơ, rate và bằng cấp giáo viên.
4. teacher_specialization: các chuyên môn nhạc cụ của giáo viên.
5. teacher_availability: các khoảng thời gian rảnh.
6. room: phòng, sức chứa và giá thuê.
7. instrument: từng nhạc cụ vật lý, loại và giá thuê.
8. package_offer: gói đang bán.
9. enrollment: học viên mua gói; lưu bản sao điều kiện lúc mua và ngày hiệu lực.
10. activity: lesson/exam/rental cùng lịch, tài nguyên và trạng thái.
11. activity_instrument: nhạc cụ gắn với hoạt động.
12. lesson_participation: từng người tham gia lesson, gói tài trợ và trạng thái sử dụng giờ.
13. exam_registration: học viên đăng ký kỳ thi.
14. exam_result: kết quả tùy chọn của đăng ký thi.
15. charge: khoản phải thu.
16. payment: tiền thực nhận.

Có 20 khóa ngoại trong schema hiện tại. Đây là một thiết kế tham khảo; **16 bảng và 20 quan hệ không phải yêu cầu của PDF**. Chỉ đơn giản hóa schema khi hiểu đầy đủ tác động đến các quy tắc và thống nhất với nhóm; đừng tự tái thiết kế chỉ để đạt một số bảng khác.

## 12. Kế hoạch chia cho hai người

**[NGƯỜI DÙNG ĐANG CÂN NHẮC / ĐỀ XUẤT PHÂN CÔNG]**

Nhóm muốn dựng phần khung chung trước, đưa lên main rồi chia branch.

Khung chung nên thống nhất:

- Model, interface, constructor và chữ ký service.
- SchoolStore và đơn vị phút/cents.
- Schema, kết nối JDBC, cấu hình mẫu không chứa mật khẩu.
- Main/menu, lệnh build/run và dữ liệu mẫu nhỏ.
- Các lỗi cần từ chối và nguyên tắc không ghi dữ liệu một phần.

Phân công tham khảo:

- Người A: CatalogService, EnrollmentService, BillingService; controller tương ứng; JDBC/schema/ERD.
- Người B: SchedulingService, ExamService; controller tương ứng; demo; UML sequence/activity.
- Cùng rà soát model, Class Diagram, yêu cầu OOP và kiểm thử tích hợp.
- Thống nhất người phụ trách các file chung như Main, SchoolApplication, ConsoleController.
- Người A ưu tiên validateForLesson để người B tích hợp xếp lịch sớm.
- Ghép từng chức năng nhỏ vào main sau khi review; thường xuyên cập nhật main vào branch.
- Tuần 1: khung + luồng cơ bản; tuần 2: hoàn thiện/tích hợp/kiểm thử; tuần 3: sửa lỗi, UML, báo cáo và luyện bảo vệ.

Đây là gợi ý tổ chức, chưa phải một lệnh yêu cầu Codex tự commit, push hoặc tạo branch.

## 13. Checklist nghiệm thu chức năng và OOP

- [ ] Đủ thông tin học viên/giáo viên; ID không trùng.
- [ ] Giáo viên có nhiều chuyên môn và availability; đặt lịch kiểm tra cả hai.
- [ ] Có gói finite/unlimited, mua gói có ngày hiệu lực, lesson lẻ và lesson nhóm.
- [ ] Theo dõi grant/reserved/consumed/released và lịch sử; kiểm tra expiry.
- [ ] Ngăn trùng giáo viên/phòng/nhạc cụ/học viên; lịch nối tiếp được phép.
- [ ] Nhóm trừ 60 phút từng người; một người không hợp lệ thì không lưu một phần.
- [ ] Hủy <24h tiêu thụ; chính sách đúng 24h được ghi rõ và test.
- [ ] Thuê phòng/nhạc cụ được đặt và tính phí.
- [ ] Kỳ thi có capacity, chống đăng ký trùng/quá chỗ và lưu pass/fail/optional score.
- [ ] Phí và tiền nhận tách biệt; tổng tiền, dư nợ, tổng hợp tháng/học viên đúng.
- [ ] Ít nhất 2 abstract class và 2 interface có ý nghĩa.
- [ ] Có mixed collection và lời gọi override thực sự.
- [ ] Có nhiều copy constructor và bằng chứng bảo vệ trạng thái.
- [ ] Đủ 3 UML; code, UML và báo cáo khớp nhau.
- [ ] Console có các menu yêu cầu và đủ dữ liệu demo.
- [ ] MySQL trong phạm vi người dùng chạy được; không chỉ có sơ đồ.
- [ ] Có tình huống thành công/lỗi/biên và kết quả thực tế.
- [ ] Báo cáo đủ 6 nội dung; demo đủ 10 bước và đã luyện thời gian.

## 14. Các việc cần xác nhận thay vì tự suy đoán

- Giới hạn thời gian bảo vệ chính thức và cách chia Q&A.
- Giá trị Schedule/Report và cách quy đổi điểm trong rubric.
- Chính sách thanh toán trước khi dùng gói.
- Chính sách refund/no-show/hủy khi đã bắt đầu.
- Thang điểm thi và việc cho phép sửa kết quả.
- Những giả định tại mục 9 nếu giảng viên có quy định khác.
- Mục tiêu công việc cụ thể ở repository đang nhận: dựng khung, hoàn thiện module, kiểm thử hay chỉnh thiết kế.

Không biến các câu chưa rõ này thành lý do dừng mọi công việc: có thể tiếp tục phần độc lập, ghi rõ giả định và hỏi khi quyết định đó ảnh hưởng đến hành vi cần triển khai.

## 15. Lệnh chạy của bản tham khảo

Khi các script này có trong repository:

~~~powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Demo
powershell -NoProfile -ExecutionPolicy Bypass -File .\build.ps1 -Test
~~~

Trong IntelliJ: chọn JDK phù hợp (bản mẫu dùng Java 17), đánh dấu src/main/java là source root và chạy com.conservatoire.Main. Muốn dùng JDBC MySQL, thêm Connector/J trong lib vào classpath và đặt biến môi trường tương ứng.

Trước khi kết luận hoàn thành, Codex cần thực sự build/chạy/test trong repository hiện tại và báo rõ phần đã kiểm tra, phần chưa kiểm tra.

## 16. Prompt gợi ý để gửi kèm cho Codex

~~~text
Hãy đọc toàn bộ PROJECT_BRIEF_FOR_CODEX.md, gồm phụ lục đề bài gốc.
Kiểm tra source hiện có và tóm tắt những gì đã có, những gì còn thiếu.
Giữ mức Java OOP nhập môn, nhưng phải đủ yêu cầu OOP và nghiệp vụ của đề.
Phân biệt yêu cầu gốc, phạm vi của tôi, giả định và thiết kế tham khảo.
Sau đó thực hiện công việc tôi giao ở tin nhắn này, theo từng phần nhỏ có thể chạy và kiểm tra.
Nếu sửa API/model/schema dùng chung, giải thích ảnh hưởng đến phần của thành viên còn lại.
~~~

---

## PHỤ LỤC A — Toàn bộ nội dung đề bài gốc tiếng Anh

Nguồn: Conservatoire-Virtuel-Project.pdf, 5 trang, do người dùng cung cấp. Nội dung dưới đây là bản trích chữ; nhãn trang/ngắt dòng và ký hiệu bullet được chuẩn hóa để dễ đọc. Những quy định trong phần này phải được đọc cùng các nhãn nguồn và phạm vi ở đầu file.

### Original PDF — page 1

```text
Conservatoire Virtuel – Music School Management 
System 
Context 
A private music school called Conservatoire Virtuel wishes to build its information system. 
The objective is to design and implement an application to support the school’s internal 
activities. 
The system must manage: 
- Students and teachers, 
- Course packages and individual lessons, 
- Lesson scheduling and room/instrument booking, 
- Payments and billing, 
- Official exams and their results. 
The system should follow good software and database design practices. 
 
Functional Requirements 
1. Students 
A student is identified by a unique ID. 
Attributes include: 
- Last name, first name, 
- Address, date of birth, phone, email, 
- Level (Beginner/Intermediate/Advanced), 
- Preferred instruments. 
Students can enroll in: 
- A Course Package (N lessons and validity dates), 
- Individual lessons (billed per lesson). 
The system must keep track of: 
- Purchased hours, 
- Remaining hours, 
- Usage history. 
 
2. Teachers
```

### Original PDF — page 2

```text
Each teacher has an ID, name, specializations, hourly rate, qualifications and availability. 
A teacher can teach multiple instruments. Scheduling must validate specialization and 
availability. 
 
3. Course Packages & Services 
The school offers: 
- Music packages, 
- Unlimited lessons packages, 
- Group or individual lessons, 
- Single paid lessons, 
- Instrument rental, 
- Room booking. 
Each purchased service must be tracked and billable. 
 
4. Scheduling System 
The system must allow scheduling of: 
- Lessons (teacher + student + room + instrument), 
- Room rentals, 
- Exam sessions. 
For each scheduled activity we keep: 
- Date and start time, 
- Duration, 
- Assigned resources, 
- Status (scheduled/completed/cancelled). 
Schedule conflicts must be prevented. 
 
5. Exams 
The school organizes official exams: 
- Exams have a name, instrument, date, and capacity. 
- Students can register. 
- Results are later recorded: pass/fail and optional score.
```

### Original PDF — page 3

```text
6. Payments and Billing 
The school must keep track of: 
- Payments, 
- Outstanding balances, 
- Prices of packages, rentals and lessons. 
The system must provide: 
- Total amount spent by a student, 
- Summary per month or per student. 
 
Additional Business Rules 
1. Package hours expire at the package end date. 
2. If a lesson is cancelled less than 24h before, it counts as consumed. 
3. Exam registration closes when maximum capacity is reached. 
4. A group lesson consumes one hour from each participant. 
 
 
Object-Oriented Design Requirements 
(mandatory) 
Your design and implementation must apply advanced OOP concepts. 
1. Abstract Classes 
The solution must include at least two abstract classes used appropriately to model 
generalization and shared behavior. 
2. Interfaces 
At least two interfaces must be used to represent independent capabilities or behaviors. 
3. Polymorphism 
You must demonstrate the use of: 
- Polymorphic collections, 
- Polymorphic method calls, 
- Overridden methods and dynamic dispatch.
```

### Original PDF — page 4

```text
4. Copy Constructors 
You must implement copy constructors for multiple key classes to support duplication and 
protect internal state. 
5. Good OOP practice 
Your design must show correct use of: 
- Inheritance, 
- Composition, 
- Encapsulation, 
- Abstract types versus concrete types. 
These constraints must appear in: 
- UML diagrams, 
- Java code, 
- Final report description. 
 
 
Deliverables 
1. UML Design 
Include: 
- Class Diagram, 
- Sequence Diagram for scheduling, 
- Activity Diagram for exam registration. 
2. Java Application 
Create a console-based application with a text menu supporting: 
a. Manage students and teachers 
b. Manage course packages and individual lessons 
c. Manage scheduling and booking 
d. Manage payments and billing 
e. Manage exams and results 
The application must contain enough test data to demonstrate functionalities.
```

### Original PDF — page 5

```text
3. Use Case Preparation 
Prepare a scenario with initial data: 
- Students, teachers, 
- Packages, 
- Instruments, 
- Rooms, 
- Scheduled lessons, 
- Exams. 
 
4. Report + Presentation 
Your report must contain: 
- Project description, 
- UML, 
- Design rationale, 
- Screenshots of execution, 
- Testing scenario, 
- Discussion of OO requirements. 
 
5. Bonus (optional) 
- Implement a database, 
- Add a GUI (Swing/JavaFX), 
- Add unit tests, 
 
Grading Criteria 
- Correctness of domain implementation, 
- Code quality and object-oriented design, 
- Correct use of abstract classes, interfaces, polymorphism and copy constructors, 
- Professional UML, 
- Clarity and organization of report and presentation.
```
