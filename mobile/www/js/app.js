/**
 * Elder Care Mobile App - 메인 애플리케이션 로직
 *
 * TODO: 인증 관리
 * TODO: 화면 네비게이션
 * TODO: 상태 관리
 * TODO: 오프라인 지원
 * TODO: 로컬 스토리지
 */

// ====================================
// 애플리케이션 상태
// ====================================
const AppState = {
    isLoggedIn: false,
    currentUser: null,
    currentScreen: 'dashboard',
    notifications: [],
    connectionStatus: 'online',
    unreadMessages: 0
};

// ====================================
// 초기화
// ====================================
document.addEventListener('deviceready', function () {
    console.log('Cordova 준비 완료');

    // 앱 초기화
    initializeApp();
    setupEventListeners();
    checkAuthStatus();
    setupConnectionMonitoring();

    console.log('Elder Care Mobile App 시작됨');
}, false);

/**
 * 앱 초기화
 */
function initializeApp() {
    // TODO: 스플래시 화면 숨기기
    if (navigator.splashscreen) {
        navigator.splashscreen.hide();
    }

    // TODO: 로컬 스토리지에서 사용자 정보 로드
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
        AppState.currentUser = JSON.parse(savedUser);
        AppState.isLoggedIn = true;
    }

    // TODO: 스태이터스 바 설정
    if (StatusBar) {
        StatusBar.styleLightContent();
        StatusBar.backgroundColorByHexString('#2c3e50');
    }

    // TODO: 디바이스 정보 로그
    if (device) {
        console.log('Device: ' + device.platform + ' ' + device.version);
        console.log('UUID: ' + device.uuid);
    }
}

/**
 * 이벤트 리스너 설정
 */
function setupEventListeners() {
    // 로그인 폼 제출
    document.getElementById('loginForm').addEventListener('submit', handleLogin);

    // 네비게이션 탭
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const screen = this.dataset.screen;
            navigateToScreen(screen);
        });
    });

    // 빠른 액션 버튼
    document.getElementById('callButton').addEventListener('click', () => {
        // TODO: 음성 통화 시작
        WebRTC.initCall('audio');
    });

    document.getElementById('videoCallButton').addEventListener('click', () => {
        // TODO: 영상 통화 시작
        WebRTC.initCall('video');
    });

    document.getElementById('chatButton').addEventListener('click', () => {
        navigateToScreen('message');
    });

    document.getElementById('healthButton').addEventListener('click', () => {
        navigateToScreen('health');
    });

    // 메뉴 버튼
    document.getElementById('menuButton').addEventListener('click', () => {
        // TODO: 슬라이드 메뉴 열기
        showMenu();
    });

    // 알림 버튼
    document.getElementById('notificationButton').addEventListener('click', () => {
        // TODO: 알림 목록 표시
        showNotifications();
    });

    // TODO: 뒤로가기 버튼
    document.addEventListener('backbutton', onBackButton, false);

    // TODO: 일시 정지/재개
    document.addEventListener('pause', onPause, false);
    document.addEventListener('resume', onResume, false);
}

/**
 * 인증 상태 확인
 */
function checkAuthStatus() {
    if (AppState.isLoggedIn && AppState.currentUser) {
        showScreen('dashboardScreen');
        loadDashboardData();
    } else {
        showScreen('loginScreen');
    }
}

/**
 * 로그인 처리
 */
async function handleLogin(e) {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    showLoading(true);

    try {
        // API를 통해 로그인
        const response = await API.login(email, password);

        if (response.success) {
            // 사용자 정보 저장
            AppState.currentUser = response.user;
            AppState.isLoggedIn = true;

            // 로컬 스토리지에 저장
            localStorage.setItem('user', JSON.stringify(response.user));
            localStorage.setItem('authToken', response.token);

            // WebSocket 연결
            WebSocket.connect();

            // 대시보드로 이동
            showScreen('dashboardScreen');
            loadDashboardData();

            showToast('로그인 성공');
        } else {
            showToast('로그인 실패: ' + response.error);
        }
    } catch (error) {
        console.error('로그인 에러:', error);
        showToast('로그인 중 오류가 발생했습니다');
    } finally {
        showLoading(false);
    }
}

/**
 * 로그아웃
 */
function logout() {
    // TODO: WebSocket 연결 끊기
    WebSocket.disconnect();

    // 사용자 정보 삭제
    AppState.isLoggedIn = false;
    AppState.currentUser = null;
    localStorage.removeItem('user');
    localStorage.removeItem('authToken');

    // 로그인 화면으로 이동
    showScreen('loginScreen');

    showToast('로그아웃되었습니다');
}

/**
 * 화면 전환
 */
function navigateToScreen(screenName) {
    const screenMap = {
        'dashboard': 'dashboardScreen',
        'health': 'healthScreen',
        'message': 'messageScreen',
        'setting': 'settingScreen',
        'call': 'callScreen'
    };

    const screenId = screenMap[screenName];
    if (screenId) {
        showScreen(screenId);
        AppState.currentScreen = screenName;

        // 네비게이션 탭 활성화 상태 업데이트
        updateNavActiveState(screenName);
    }
}

/**
 * 화면 표시
 */
function showScreen(screenId) {
    // 모든 화면 숨기기
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });

    // 해당 화면 표시
    const screen = document.getElementById(screenId);
    if (screen) {
        screen.classList.add('active');
    }
}

/**
 * 네비게이션 활성 상태 업데이트
 */
function updateNavActiveState(screenName) {
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    const activeBtn = document.querySelector(`.nav-btn[data-screen="${screenName}"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }
}

/**
 * 대시보드 데이터 로드
 */
async function loadDashboardData() {
    try {
        showLoading(true);

        // TODO: API에서 대시보드 데이터 로드
        const userData = await API.getUserData(AppState.currentUser.id);

        // TODO: 건강 지표 업데이트
        updateHealthIndicators(userData.health);

        // TODO: 활동 데이터 표시
        updateActivitySummary(userData.activities);

        // TODO: 약물 목록 표시
        updateMedicineList(userData.medicines);

        // TODO: 알림 업데이트
        updateNotificationBadge(userData.unreadNotifications);

    } catch (error) {
        console.error('대시보드 로드 실패:', error);
        showToast('데이터를 로드할 수 없습니다');
    } finally {
        showLoading(false);
    }
}

/**
 * 건강 지표 업데이트
 */
function updateHealthIndicators(healthData) {
    if (healthData) {
        document.getElementById('bloodPressure').textContent =
            healthData.bloodPressure || '--';
        document.getElementById('pulse').textContent =
            healthData.pulse || '--';
        document.getElementById('bloodSugar').textContent =
            healthData.bloodSugar || '--';
    }
}

/**
 * 활동 요약 업데이트
 */
function updateActivitySummary(activities) {
    const container = document.getElementById('activitySummary');
    if (!container || !activities) return;

    container.innerHTML = '';

    activities.forEach(activity => {
        const div = document.createElement('div');
        div.className = 'activity-item';
        div.innerHTML = `
            <span>${activity.name}</span>
            <span>${activity.duration}분</span>
        `;
        container.appendChild(div);
    });
}

/**
 * 약물 목록 업데이트
 */
function updateMedicineList(medicines) {
    const container = document.getElementById('medicineList');
    if (!container || !medicines) return;

    container.innerHTML = '';

    medicines.forEach(medicine => {
        const div = document.createElement('div');
        div.className = 'medicine-item';
        div.innerHTML = `
            <div>
                <div class="name">${medicine.name}</div>
                <div class="time">${medicine.time}</div>
            </div>
            <span>${medicine.taken ? '✓' : '◯'}</span>
        `;
        container.appendChild(div);
    });
}

/**
 * 알림 배지 업데이트
 */
function updateNotificationBadge(count) {
    const badge = document.getElementById('notificationBadge');
    if (count > 0) {
        badge.textContent = count;
        badge.style.display = 'flex';
    } else {
        badge.style.display = 'none';
    }
}

/**
 * 로딩 표시
 */
function showLoading(show = true) {
    const spinner = document.getElementById('loadingSpinner');
    if (show) {
        spinner.style.display = 'flex';
    } else {
        spinner.style.display = 'none';
    }
}

/**
 * 토스트 알림 표시
 */
function showToast(message, duration = 2000) {
    const toast = document.getElementById('toastNotification');
    toast.textContent = message;
    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
    }, duration);
}

/**
 * 메뉴 표시
 */
function showMenu() {
    // TODO: 슬라이드 메뉴 구현
    alert('메뉴\n- 프로필\n- 설정\n- 로그아웃');
}

/**
 * 알림 목록 표시
 */
function showNotifications() {
    // TODO: 알림 목록 모달 구현
    alert('알림 목록');
}

/**
 * 뒤로가기 버튼 처리
 */
function onBackButton() {
    // TODO: 뒤로가기 처리 (화면에 따라 다름)
    // 로그인 화면에서는 앱 종료, 다른 화면에서는 대시보드로 이동

    if (AppState.currentScreen === 'dashboard' || !AppState.isLoggedIn) {
        // TODO: 앱 종료 확인
        navigator.app.exitApp();
    } else {
        navigateToScreen('dashboard');
    }
}

/**
 * 앱 일시 정지
 */
function onPause() {
    console.log('앱 일시 정지');
    // TODO: WebSocket 연결 끊기 (배터리 절약)
    // TODO: 백그라운드 작업 중단
}

/**
 * 앱 재개
 */
function onResume() {
    console.log('앱 재개');
    // TODO: WebSocket 재연결
    // TODO: 데이터 새로고침
    if (AppState.isLoggedIn) {
        loadDashboardData();
        WebSocket.connect();
    }
}

/**
 * 연결 상태 모니터링
 */
function setupConnectionMonitoring() {
    document.addEventListener('online', function () {
        console.log('인터넷 연결됨');
        AppState.connectionStatus = 'online';
        showToast('인터넷 연결됨');

        // TODO: 오프라인 중 저장된 데이터 동기화
        if (AppState.isLoggedIn) {
            loadDashboardData();
            WebSocket.connect();
        }
    }, false);

    document.addEventListener('offline', function () {
        console.log('인터넷 연결 끊김');
        AppState.connectionStatus = 'offline';
        showToast('인터넷 연결이 끊겼습니다');

        // TODO: WebSocket 연결 끊기
        WebSocket.disconnect();
    }, false);
}

// ====================================
// 유틸리티 함수
// ====================================

/**
 * 날짜 포맷팅
 */
function formatDate(date) {
    return new Date(date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * 시간 포맷팅
 */
function formatTime(date) {
    return new Date(date).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * 로컬 스토리지 헬퍼
 */
const Storage = {
    set: (key, value) => {
        localStorage.setItem(key, JSON.stringify(value));
    },
    get: (key) => {
        const value = localStorage.getItem(key);
        return value ? JSON.parse(value) : null;
    },
    remove: (key) => {
        localStorage.removeItem(key);
    },
    clear: () => {
        localStorage.clear();
    }
};

console.log('app.js 로드 완료');
