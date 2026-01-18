// ==================== 0. CẤU HÌNH API ====================
const HOST = (window.location.hostname === 'localhost') 
             ? "http://localhost:8082" 
             : `http://${window.location.hostname}:8082`;
const API = {
    BOOKS: `${HOST}/api/books`,
    ADMIN: `${HOST}/api/admin`,
    USER: `${HOST}/api/users`,
    PURCHASE: `${HOST}/api/purchase`,
    PAGES: `${HOST}/api/chapters`
};

let currentBookId = null;
let loggedInUser = JSON.parse(localStorage.getItem("user"));

// ==================== 1. HÀM CHỈ HUY (NAVIGATION) ====================

// --- 1. HÀM CHỈ HUY (NAVIGATION) ---
function showView(viewId) {
    const views = document.querySelectorAll('.view');
    views.forEach(v => v.style.display = 'none');

    const target = document.getElementById(viewId);
    if (target) {
        target.style.display = 'block';
    }

    if(viewId === 'home-view') loadMangaList();
    window.scrollTo(0,0);
}

// --- 2. XỬ LÝ ĐĂNG NHẬP / TÀI KHOẢN ---
function checkLoginStatus() {
    const authSection = document.getElementById("auth-buttons-section");
    const userSection = document.getElementById("user-info-section");
    
    if (loggedInUser) {
        authSection.style.display = "none";
        userSection.style.display = "flex";
        document.getElementById("display-username").innerText = loggedInUser.username;
        // Gọi server lấy số tiền mới nhất thay vì dùng số cũ trong LocalStorage
        refreshBalance();
    } else {
        authSection.style.display = "block";
        userSection.style.display = "none";
    }
}

async function refreshBalance() {
    if (!loggedInUser) return;
    try {
        const res = await fetch(`${API.USER}/${loggedInUser.id}`);
        const user = await res.json();
        document.getElementById("user-balance").innerText = user.balance.toLocaleString();
        // Cập nhật lại bộ nhớ đệm
        localStorage.setItem("user", JSON.stringify(user));
        loggedInUser = user;
    } catch (e) { console.error("Lỗi ví tiền"); }
}

// Hàm NẠP TIỀN (Tính năng bạn đang tìm)
async function handleDeposit() {
    if (!loggedInUser) return alert("Phải đăng nhập mới nạp được vàng!");
    const amount = prompt("Em gái muốn nạp bao nhiêu tiền vào ví?", "50000");
    if (!amount || isNaN(amount)) return;

    try {
        const res = await fetch(`${API.USER}/${loggedInUser.id}/deposit?amount=${amount}`, {method: 'POST'});
        if (res.ok) {
            alert("✨ Chúc mừng! Vàng đã về kho.");
            refreshBalance(); // Cập nhật số tiền trên header ngay
        }
    } catch (e) { alert("Ngân hàng đang bảo trì!"); }
}

async function handleLogin() {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    try {
        const res = await fetch(`${HOST}/api/users/login`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ username, password })
        });
        if (res.ok) {
            const user = await res.json();
            localStorage.setItem("user", JSON.stringify(user));
            location.reload();
        } else alert("Sai tài khoản!");
    } catch (e) { alert("Lỗi server!"); }
}

async function handleRegister() {
    const username = document.getElementById("reg-username").value;
    const password = document.getElementById("reg-password").value;
    const res = await fetch(`${HOST}/api/users/register`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ username, password })
    });
    if(res.ok) { alert("Đã đăng ký!"); showView('login-view'); }
}

function handleLogout() {
    localStorage.removeItem("user");
    location.reload();
}

// --- 3. QUẢN LÝ TRUYỆN (SHOP) ---
async function loadMangaList() {
    const res = await fetch(API.BOOKS);
    const data = await res.json();
    document.getElementById("mangaGrid").innerHTML = data.map(m => `
        <div class="manga-card" onclick="viewBookDetail(${m.id})">
            <img src="${m.imageUrl ? (m.imageUrl.startsWith('http') ? m.imageUrl : HOST + m.imageUrl) : 'https://placehold.jp/300x450.png'}" />
            <div class="manga-info">
                <div class="card-title">${m.title}</div>
                <div class="card-price">${m.price.toLocaleString()} VNĐ</div>
            </div>
        </div>`).join('');
}

async function viewBookDetail(id) {
    currentBookId = id;
    // Gửi thêm userId nếu đã đăng nhập để Java biết đường mà Check sở hữu
    const url = `${HOST}/api/books/${id}${loggedInUser ? '?userId=' + loggedInUser.id : ''}`;
    
    const res = await fetch(url);
    const book = await res.json();
    
    document.getElementById("detail-title").innerText = book.title;
    document.getElementById("target-book-label").innerText = "Sẵn sàng nạp PDF cho: " + book.title;
    
    document.getElementById("chapterList").innerHTML = book.chapters.sort((a,b)=>a.chapterIndex - b.chapterIndex).map(c => `
        <div class="chapter-item" style="display:flex; justify-content:space-between; margin-bottom:10px; background:#111; padding:10px; border-radius:8px; align-items:center;">
            <span>Tập ${c.chapterIndex}: ${c.title}</span>
            <button class="btn-primary" onclick="${c.bought ? `openReader(${c.id}, '${c.title}')` : `handleBuy(${c.id})`}">
                ${c.bought ? "📖 Đọc ngay" : `💰 Mua ${c.price.toLocaleString()}đ`}
            </button>
        </div>`).join('');
    showView('detail-view');
}


async function openReader(id, title) {
    // Luôn gửi kèm userId để Backend cho phép xem ảnh (vì chúng ta đã thêm logic bảo mật)
    const res = await fetch(`${API.PAGES}/${id}/pages?userId=${loggedInUser.id}`);
    const pages = await res.json();
    
    document.getElementById("reading-title").innerText = title;
    document.getElementById("imageLoader").innerHTML = pages.sort((a,b)=>a.pageOrder-b.pageOrder)
        .map(p => `<img src="${HOST}${p.imageUrl}" style="width:100%; display:block; margin: 0 auto; max-width:800px;">`).join('');
    showView('reader-view');
}
async function openReader(id, title) {
    const res = await fetch(`${HOST}/api/chapters/${id}/pages`);
    const pages = await res.json();
    document.getElementById("reading-title").innerText = title;
    document.getElementById("imageLoader").innerHTML = pages.sort((a,b)=>a.pageOrder-b.pageOrder)
        .map(p => `<img src="${HOST}${p.imageUrl}" style="width:100%; display:block;">`).join('');
    showView('reader-view');
}

// --- 4. ADMIN & PDF ---
async function addManga() {
    const bookData = {
        title: document.getElementById("add-title").value,
        author: document.getElementById("add-author").value,
        price: document.getElementById("add-price").value,
        imageUrl: document.getElementById("add-imageUrl").value
    };
    await fetch(`${HOST}/api/books`, {method:"POST", headers:{"Content-Type":"application/json"}, body:JSON.stringify(bookData)});
    alert("Xong!"); showView('home-view');
}

async function uploadMangaPdf() {
    const formData = new FormData();
    formData.append("file", document.getElementById("pdfFile").files[0]);
    formData.append("bookId", currentBookId);
    document.getElementById("status").innerText = "⏳ Đang mổ xẻ...";
    await fetch(`${HOST}/api/admin/upload-oneshot`, {method:"POST", body:formData});
    alert("Xẻ xong!"); showView('detail-view');
}
// --- HÀM MUA TRUYỆN CHỐT HẠ ---
async function handleBuy(chapId) {
    if(!loggedInUser) {
        alert("Em gái ơi, phải đăng nhập mới mua được nhé!");
        return showView('login-view');
    }

    if(!confirm("Xác nhận dùng xu để mở khóa chương này?")) return;

    try {
        const url = `${HOST}/api/purchase?userId=${loggedInUser.id}&chapterId=${chapId}`;
        const res = await fetch(url, { method: 'POST' });
        const msg = await res.text();

        if (res.ok) {
            alert("Hệ thống: " + msg);
            // Sau khi mua xong -> Refresh ví và Vẽ lại danh sách chương để nút biến thành "Đọc"
            await refreshBalance(); 
            await viewBookDetail(currentBookId);
            
            // TỰ ĐỘNG MỞ READER LUÔN (UX Đỉnh cao)
            openReader(chapId, "Đang mở nội dung vừa mua...");
        } else {
            // Trường hợp Java báo "Bạn đã sở hữu"
            if (msg.includes("sở hữu")) {
                openReader(chapId, "Đang mở nội dung...");
            } else {
                alert("Lỗi thanh toán: " + msg);
            }
        }
    } catch (e) {
        alert("Server thanh toán đang bận, em thử lại sau!");
    }
}

// Khởi chạy
window.onload = () => {
    checkLoginStatus();
    loadMangaList();
};