// ==================== 0. CẤU HÌNH HỆ THỐNG (GIỮ ZIN TỪ SERVER) ====================
// Logic: Tự động nhận biết port 5500 (Live Server máy con) để gọi sang 8082
const IS_LOCAL = window.location.port === "5500" || window.location.port === "5501";
// Nếu là Local thì trỏ đích danh, còn trên Server thì để rỗng (Nginx tự lo)
const HOST = IS_LOCAL ? "http://localhost:8082" : ""; 

const API = {
    BOOKS: `${HOST}/api/books`,
    ADMIN: `${HOST}/api/admin`,
    USER: `${HOST}/api/users`,
    PURCHASE: `${HOST}/api/purchase`,
    CHAPTERS: `${HOST}/api/chapters` // Đổi PAGES thành CHAPTERS cho đúng chuẩn API mới
};

let currentBookId = null;
let loggedInUser = JSON.parse(localStorage.getItem("user"));

// --- HÀM GỌI API THÔNG MINH (CHỐNG LỖI NGROK) ---
async function callAPI(url, method = "GET", body = null) {
    const headers = {
        // CÁI NÀY QUAN TRỌNG: Giúp bypass màn hình "Visit Site" của Ngrok với API ngầm
        "ngrok-skip-browser-warning": "true",
        "Content-Type": "application/json"
    };

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);

    try {
        const response = await fetch(url, options);
        return response;
    } catch (error) {
        console.error("API Error:", error);
        alert("⚠️ Mất kết nối Server! Kiểm tra lại đường truyền.");
        throw error;
    }
}

// ==================== 1. ĐIỀU HƯỚNG & GIAO DIỆN ====================

function showView(viewId) {
    document.querySelectorAll('.view').forEach(v => v.style.display = 'none');
    
    const target = document.getElementById(viewId);
    if (target) {
        target.style.display = 'block';
        window.scrollTo(0,0);
    }

    if(viewId === 'home-view') loadMangaList();
}

function checkLoginStatus() {
    const authSection = document.getElementById("auth-buttons-section");
    const userSection = document.getElementById("user-info-section");
    
    if (loggedInUser) {
        authSection.style.display = "none";
        userSection.style.display = "flex";
        document.getElementById("display-username").innerText = loggedInUser.username;
        refreshBalance(); // Lấy lại số tiền mới nhất
    } else {
        authSection.style.display = "block";
        userSection.style.display = "none";
    }
}

// ==================== 2. TÀI KHOẢN & VÍ TIỀN ====================

async function refreshBalance() {
    if (!loggedInUser) return;
    try {
        const res = await callAPI(`${API.USER}/${loggedInUser.id}`);
        if(res.ok) {
            const user = await res.json();
            document.getElementById("user-balance").innerText = user.balance.toLocaleString();
            localStorage.setItem("user", JSON.stringify(user));
            loggedInUser = user;
        }
    } catch(e) { console.log("Lỗi load ví"); }
}

async function handleLogin() {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    const res = await callAPI(`${API.USER}/login`, "POST", { username, password });
    if (res.ok) {
        const user = await res.json();
        localStorage.setItem("user", JSON.stringify(user));
        loggedInUser = user;
        checkLoginStatus();
        showView('home-view');
    } else {
        alert("❌ Sai tài khoản rồi đại ca ơi!");
    }
}

async function handleRegister() {
    const username = document.getElementById("reg-username").value;
    const password = document.getElementById("reg-password").value;

    const res = await callAPI(`${API.USER}/register`, "POST", { username, password });
    if(res.ok) {
        alert("✅ Đăng ký thành công! Đăng nhập ngay thôi.");
        showView('login-view');
    } else alert("❌ Lỗi: Tên này chắc có người lấy rồi.");
}

function handleLogout() {
    if(confirm("Chắc chắn muốn đăng xuất?")) {
        localStorage.removeItem("user");
        location.reload();
    }
}

async function handleDeposit() {
    if (!loggedInUser) return;
    const amount = prompt("💵 Nhập số tiền nạp (VNĐ):", "50000");
    if (!amount || isNaN(amount)) return;

    // Nạp tiền dùng Query Param cho nhanh
    const res = await callAPI(`${API.USER}/${loggedInUser.id}/deposit?amount=${amount}`, "POST");
    if(res.ok) {
        alert("🤑 Nạp tiền thành công!");
        refreshBalance();
    } else alert("Lỗi nạp tiền.");
}

// ==================== 3. TRUYỆN TRANH & MUA BÁN ====================

async function loadMangaList() {
    try {
        const res = await callAPI(API.BOOKS);
        const data = await res.json();
        
        document.getElementById("mangaGrid").innerHTML = data.map(m => `
            <div class="manga-card" onclick="viewBookDetail(${m.id})">
                <div class="card-thumb" style="height:280px; overflow:hidden;">
                    <img src="${m.imageUrl && m.imageUrl.includes('http') ? m.imageUrl : HOST + m.imageUrl}" 
                         style="width:100%; height:100%; object-fit:cover;" 
                         onerror="this.src='https://placehold.jp/300x450.png'">
                </div>
                <div class="manga-info">
                    <div class="card-title">${m.title}</div>
                    <div class="card-price" style="color:#27ae60; font-weight:bold">${m.price.toLocaleString()} đ</div>
                </div>
            </div>`).join('');
    } catch(e) { document.getElementById("mangaGrid").innerHTML = "<p>Lỗi kết nối Backend.</p>"; }
}

async function viewBookDetail(id) {
    currentBookId = id;
    const userIdPart = loggedInUser ? `?userId=${loggedInUser.id}` : '';
    
    const res = await callAPI(`${API.BOOKS}/${id}${userIdPart}`);
    const book = await res.json();

    // Fill data
    document.getElementById("detail-cover").src = book.imageUrl;
    document.getElementById("detail-author").innerText = book.author;
    
    // NÚT EDIT (CHỈ CHO ADMIN)
    let editBtn = "";
    if (loggedInUser && loggedInUser.role === 'ADMIN') {
        editBtn = ` <button onclick="openEditManga(${book.id})" style="background:#e67e22; border:none; padding:4px 8px; color:white; border-radius:4px; font-size:12px; cursor:pointer;">
                        <i class="fa-solid fa-pen"></i> Sửa
                    </button>`;
    }
    document.getElementById("detail-title").innerHTML = book.title + editBtn;

    // Render Chapters
    document.getElementById("chapterList").innerHTML = book.chapters.sort((a,b)=>a.chapterIndex - b.chapterIndex).map(c => `
        <div class="chapter-item" style="display:flex; justify-content:space-between; margin-bottom:10px; background:#222; padding:12px; border-radius:6px; align-items:center;">
            <span style="font-weight:600; color:#ddd;">Chương ${c.chapterIndex}: ${c.title}</span>
            ${c.bought 
                ? `<button class="btn-primary" style="background:#3498db" onclick="openReader(${c.id}, '${c.title}')"><i class="fa-solid fa-book-open"></i> Đọc</button>` 
                : `<button class="btn-primary" style="background:#f1c40f; color:black;" onclick="handleBuy(${c.id})">🔓 Mua ${c.price.toLocaleString()}đ</button>`}
        </div>`).join('');
    
    showView('detail-view');
}

async function handleBuy(chapId) {
    if (!loggedInUser) {
        alert("🔒 Đăng nhập để mua em ơi!");
        return showView('login-view');
    }
    if (!confirm("Mua chương này nhé?")) return;

    try {
        const res = await callAPI(`${API.PURCHASE}?userId=${loggedInUser.id}&chapterId=${chapId}`, "POST");
        const text = await res.text();
        
        if (res.ok) {
            alert("✅ " + text);
            await refreshBalance(); 
            await viewBookDetail(currentBookId); // Reload lại trang chi tiết để đổi nút Mua thành Đọc
            openReader(chapId, "Đang mở...");
        } else alert("❌ " + text);
    } catch (e) { alert("Lỗi thanh toán."); }
}

async function openReader(chapterId, title) {
    // Luôn gửi kèm UserID để Backend xác thực quyền sở hữu
    const uid = loggedInUser ? loggedInUser.id : 0;
    try {
        const res = await callAPI(`${API.CHAPTERS}/${chapterId}/pages?userId=${uid}`);
        if(res.ok) {
            const pages = await res.json();
            document.getElementById("reading-title").innerText = title;
            document.getElementById("imageLoader").innerHTML = pages
                .sort((a,b)=>a.pageOrder - b.pageOrder)
                .map(p => `<img src="${HOST}${p.imageUrl}" loading="lazy" style="width:100%; display:block; margin:0 auto; margin-bottom:5px;">`)
                .join('');
            showView('reader-view');
        } else alert("Bạn chưa mua chương này!");
    } catch(e) { alert("Lỗi tải trang truyện."); }
}

// ==================== 4. QUẢN TRỊ ADMIN (EDIT / DELETE / UPLOAD) ====================

// --- MỞ VIEW SỬA ---
async function openEditManga(id) {
    currentBookId = id;
    try {
        const res = await callAPI(`${API.BOOKS}/${id}`);
        const book = await res.json();
        
        // Đổ dữ liệu cũ vào ô input
        document.getElementById("edit-title").value = book.title;
        document.getElementById("edit-author").value = book.author;
        document.getElementById("edit-price").value = book.price;
        document.getElementById("edit-imageUrl").value = book.imageUrl;
        
        showView('edit-view');
    } catch(e) { alert("Lỗi tải thông tin sách!"); }
}

// --- LƯU THAY ĐỔI (PUT) ---
async function saveEditManga() {
    if(!currentBookId) return;
    const updated = {
        title: document.getElementById("edit-title").value,
        author: document.getElementById("edit-author").value,
        price: Number(document.getElementById("edit-price").value),
        imageUrl: document.getElementById("edit-imageUrl").value
    };

    if(!confirm("Cập nhật lại thông tin truyện nhé?")) return;

    const res = await callAPI(`${API.BOOKS}/${currentBookId}`, "PUT", updated);
    if(res.ok) {
        alert("✅ Đã cập nhật xong!");
        loadMangaList();
        showView('home-view');
    } else alert("Lỗi Server Update!");
}

// --- XÓA TRUYỆN (DELETE) ---
async function deleteManga() {
    if(!currentBookId) return;
    const code = prompt("⚠️ NHẬP 'OK' ĐỂ XÁC NHẬN XÓA VĨNH VIỄN:");
    if(code !== "OK") return;

    const res = await callAPI(`${API.BOOKS}/${currentBookId}`, "DELETE");
    if(res.ok) {
        alert("🗑 Đã xóa truyện!");
        loadMangaList();
        showView('home-view');
    } else alert("Lỗi: Có thể truyện đang có người mua, không xóa được.");
}

// --- TẠO TRUYỆN MỚI ---
async function addManga() {
    const data = {
        title: document.getElementById("add-title").value,
        author: document.getElementById("add-author").value,
        price: Number(document.getElementById("add-price").value),
        imageUrl: document.getElementById("add-imageUrl").value
    };
    if(!data.title) return alert("Thiếu tên truyện!");

    const res = await callAPI(API.BOOKS, "POST", data);
    if(res.ok) {
        alert("✅ Tạo xong! Giờ vào nạp PDF nhé.");
        document.getElementById("add-title").value = ""; // Xóa form
        loadMangaList();
        showView('home-view');
    }
}

// --- UPLOAD PDF (Cắt ảnh) ---
async function uploadMangaPdf() {
    if(!currentBookId) return alert("❗ Chọn 1 truyện ở trang chủ trước!");
    const fileBox = document.getElementById("pdfFile");
    if(fileBox.files.length === 0) return alert("Chưa chọn file PDF!");

    const formData = new FormData();
    formData.append("file", fileBox.files[0]);
    formData.append("bookId", currentBookId);

    document.getElementById("status").innerText = "⏳ Đang xẻ ảnh PDF...";
    
    // Upload File phải dùng fetch trần (vì không dùng Content-Type: json)
    try {
        const res = await fetch(`${HOST}/api/admin/upload-oneshot`, {
            method: "POST",
            headers: { "ngrok-skip-browser-warning": "true" },
            body: formData
        });
        
        if(res.ok) {
            document.getElementById("status").innerText = "✅ Xong!";
            alert("Đã upload và cắt ảnh thành công!");
            viewBookDetail(currentBookId);
        } else {
            document.getElementById("status").innerText = "❌ Lỗi!";
            alert("Lỗi Backend xử lý PDF.");
        }
    } catch(e) { alert("Lỗi mạng upload!"); }
}

// ==================== START ====================
window.onload = () => {
    checkLoginStatus();
    loadMangaList();
};