/**
 * Elder Care 보호자 대시보드 - 메인 로직
 *
 * TODO: 실시간 데이터 업데이트
 * TODO: 사용자 인터페이스 개선
 * TODO: 데이터 캐싱
 * TODO: 오프라인 지원
 */

// ====================================
// 상태 관리
// ====================================
const DashboardState = {
    currentUser: null,
    elderlies: [],
    currentTab: 'overview',
    charts: {},
    filters: {
        elderly: '',
        metric: 'all',
        period: 'week'
    }
};

// ====================================
// 초기화
// ====================================
document.addEventListener('DOMContentLoaded', function () {
    console.log('대시보드 로드 시작');

    // 인증 확인
    checkAuthentication();

    // 이벤트 리스너 설정
    setupEventListeners();

    // 사용자 정보 로드
    loadUserProfile();

    // 데이터 로드
    loadDashboardData();

    console.log('대시보드 초기화 완료');
});

/**
 * 인증 확인
 */
function checkAuthentication() {
    const authToken = localStorage.getItem('authToken');

    if (!authToken) {
        // 로그인 페이지로 리다이렉트
        window.location.href = 'login.html';
        return;
    }
}

/**
 * 이벤트 리스너 설정
 */
function setupEventListeners() {
    // 탭 네비게이션
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const tabName = item.dataset.tab;
            switchTab(tabName);
        });
    });

    // 알림 버튼
    document.getElementById('notificationBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        const dropdown = document.getElementById('notificationDropdown');
        dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
    });

    // 사용자 메뉴 버튼
    document.getElementById('userMenuBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        const dropdown = document.getElementById('userDropdown');
        dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
    });

    // 문서 클릭 시 드롭다운 닫기
    document.addEventListener('click', () => {
        document.getElementById('notificationDropdown').style.display = 'none';
        document.getElementById('userDropdown').style.display = 'none';
    });

    // 필터 변경
    document.getElementById('healthElderlyFilter')?.addEventListener('change', (e) => {
        DashboardState.filters.elderly = e.target.value;
        loadHealthData();
    });

    document.getElementById('healthMetricFilter')?.addEventListener('change', (e) => {
        DashboardState.filters.metric = e.target.value;
        loadHealthData();
    });

    // 검색
    document.getElementById('searchElderly')?.addEventListener('input', (e) => {
        searchElderlies(e.target.value);
    });
}

/**
 * 사용자 프로필 로드
 */
async function loadUserProfile() {
    try {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            DashboardState.currentUser = JSON.parse(userStr);
            document.getElementById('userName').textContent =
                DashboardState.currentUser.name || '사용자';
        }
    } catch (error) {
        console.error('사용자 정보 로드 실패:', error);
    }
}

/**
 * 대시보드 데이터 로드
 */
async function loadDashboardData() {
    try {
        showLoading(true);

        // 모든 데이터 병렬 로드
        const [elderlies, healthData, activityData, callData, notifications] =
            await Promise.all([
                API.getManagedElderlies(),
                API.getHealthData(),
                API.getActivityData(),
                API.getCallData(),
                API.getNotifications()
            ]);

        DashboardState.elderlies = elderlies;

        // 대시보드 업데이트
        updateDashboardCards(healthData, activityData, callData);
        updateRecentActivity(activityData);
        updateNotifications(notifications);

        // 필터 옵션 업데이트
        updateFilterOptions();

        // 초기 차트 생성
        if (DashboardState.currentTab === 'overview') {
            initializeCharts();
        }
    } catch (error) {
        console.error('대시보드 데이터 로드 실패:', error);
        showError('데이터를 로드할 수 없습니다');
    } finally {
        showLoading(false);
    }
}

/**
 * 대시보드 카드 업데이트
 */
function updateDashboardCards(healthData, activityData, callData) {
    // 관리 대상자 수
    document.getElementById('elderlyCount').textContent = DashboardState.elderlies.length;

    // 건강 경고 수
    const healthAlerts = healthData.filter(h => h.isAlert).length;
    document.getElementById('healthAlertCount').textContent = healthAlerts;

    // 약물 미복용 수
    const missedMedicines = healthData.filter(h => !h.medicineTaken).length;
    document.getElementById('missedMedicineCount').textContent = missedMedicines;

    // 오늘 통화 수
    const todayDate = new Date().toLocaleDateString('en-CA');
    const todayCalls = callData.filter(c => c.date === todayDate).length;
    document.getElementById('todayCallCount').textContent = todayCalls;
}

/**
 * 최근 활동 업데이트
 */
function updateRecentActivity(activities) {
    const container = document.getElementById('recentActivityList');
    container.innerHTML = '';

    // 최근 10개만 표시
    const recentActivities = activities.slice(0, 10);

    recentActivities.forEach(activity => {
        const div = document.createElement('div');
        div.className = 'activity-item';
        div.innerHTML = `
            <strong>${activity.elderlyName}</strong>: ${activity.activity}
            <small>${formatDate(activity.timestamp)}</small>
        `;
        container.appendChild(div);
    });
}

/**
 * 알림 업데이트
 */
function updateNotifications(notifications) {
    const badge = document.getElementById('notificationBadge');
    const list = document.getElementById('notificationList');

    // 배지 업데이트
    const unreadCount = notifications.filter(n => !n.read).length;
    if (unreadCount > 0) {
        badge.textContent = unreadCount;
        badge.style.display = 'inline-flex';
    } else {
        badge.style.display = 'none';
    }

    // 목록 업데이트
    list.innerHTML = '';
    notifications.slice(0, 5).forEach(notification => {
        const div = document.createElement('div');
        div.className = 'notification-item';
        div.innerHTML = `
            <div><strong>${notification.title}</strong></div>
            <small>${notification.message}</small>
        `;
        div.addEventListener('click', () => markNotificationAsRead(notification.id));
        list.appendChild(div);
    });
}

/**
 * 탭 전환
 */
function switchTab(tabName) {
    DashboardState.currentTab = tabName;

    // 탭 콘텐츠 전환
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.getElementById(tabName)?.classList.add('active');

    // 네비게이션 아이템 활성화
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`)?.classList.add('active');

    // 해당 탭의 데이터 로드
    loadTabData(tabName);
}

/**
 * 탭별 데이터 로드
 */
async function loadTabData(tabName) {
    try {
        showLoading(true);

        switch (tabName) {
            case 'overview':
                if (Object.keys(DashboardState.charts).length === 0) {
                    initializeCharts();
                }
                break;
            case 'elderlies':
                loadElderliesList();
                break;
            case 'health':
                loadHealthData();
                break;
            case 'activities':
                loadActivityData();
                break;
            case 'medicines':
                loadMedicineData();
                break;
            case 'calls':
                loadCallData();
                break;
            case 'reports':
                loadReportData();
                break;
        }
    } finally {
        showLoading(false);
    }
}

/**
 * 노인 목록 로드
 */
async function loadElderliesList() {
    const container = document.getElementById('elderlyListContainer');
    container.innerHTML = '';

    DashboardState.elderlies.forEach(elderly => {
        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <h3>${elderly.name}</h3>
            <p><strong>나이:</strong> ${elderly.age}세</p>
            <p><strong>상태:</strong> <span class="status-badge ${elderly.status.toLowerCase()}">${elderly.status}</span></p>
            <p><strong>마지막 연락:</strong> ${formatDate(elderly.lastContact)}</p>
            <button class="btn btn-primary" onclick="viewElderly('${elderly.id}')">
                상세 보기
            </button>
        `;
        container.appendChild(card);
    });
}

/**
 * 건강 데이터 로드
 */
async function loadHealthData() {
    try {
        const data = await API.getHealthData(DashboardState.filters);

        // 테이블 업데이트
        const tbody = document.getElementById('healthDataTable');
        tbody.innerHTML = '';

        data.forEach(record => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${record.elderlyName}</td>
                <td>${formatDate(record.timestamp)}</td>
                <td>${record.bloodPressure}</td>
                <td>${record.pulse}</td>
                <td>${record.bloodSugar}</td>
                <td>
                    <span class="status-badge ${record.status.toLowerCase()}">
                        ${record.status}
                    </span>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // 차트 업데이트
        updateHealthChart(data);
    } catch (error) {
        console.error('건강 데이터 로드 실패:', error);
    }
}

/**
 * 활동 데이터 로드
 */
async function loadActivityData() {
    try {
        const data = await API.getActivityData(DashboardState.filters);

        // 테이블 업데이트
        const tbody = document.getElementById('activityTable');
        tbody.innerHTML = '';

        data.forEach(record => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${record.elderlyName}</td>
                <td>${formatDate(record.date)}</td>
                <td>${record.activity}</td>
                <td>${record.distance} km</td>
                <td>${record.duration} 분</td>
            `;
            tbody.appendChild(tr);
        });

        // 차트 업데이트
        updateActivityChart(data);
    } catch (error) {
        console.error('활동 데이터 로드 실패:', error);
    }
}

/**
 * 약물 데이터 로드
 */
async function loadMedicineData() {
    try {
        const data = await API.getMedicineData();

        const tbody = document.getElementById('medicineTable');
        tbody.innerHTML = '';

        data.forEach(medicine => {
            const tr = document.createElement('tr');
            const status = medicine.taken ? '복용' : '미복용';
            const statusClass = medicine.taken ? 'success' : 'warning';

            tr.innerHTML = `
                <td>${medicine.elderlyName}</td>
                <td>${medicine.name}</td>
                <td>${medicine.time}</td>
                <td>${medicine.taken ? '✓' : '✗'}</td>
                <td><span class="status-badge ${statusClass}">${status}</span></td>
                <td>
                    ${!medicine.taken ? `<button class="btn btn-small" onclick="markMedicineTaken('${medicine.id}')">복용</button>` : ''}
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('약물 데이터 로드 실패:', error);
    }
}

/**
 * 통화 데이터 로드
 */
async function loadCallData() {
    try {
        const data = await API.getCallData(DashboardState.filters);

        const tbody = document.getElementById('callTable');
        tbody.innerHTML = '';

        data.forEach(call => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${call.elderlyName}</td>
                <td>${formatDateTime(call.timestamp)}</td>
                <td>${call.type === 'AUDIO' ? '음성' : '영상'}</td>
                <td>${call.duration} 분</td>
                <td><span class="status-badge ${call.status.toLowerCase()}">${call.status}</span></td>
            `;
            tbody.appendChild(tr);
        });

        // 차트 업데이트
        updateCallChart(data);
    } catch (error) {
        console.error('통화 데이터 로드 실패:', error);
    }
}

/**
 * 보고서 데이터 로드
 */
async function loadReportData() {
    try {
        const data = await API.getReportData(DashboardState.filters);

        // 요약 정보 업데이트
        const summaryDiv = document.getElementById('healthSummary');
        summaryDiv.innerHTML = `
            <p><strong>평균 혈압:</strong> ${data.avgBloodPressure}</p>
            <p><strong>평균 맥박:</strong> ${data.avgPulse}</p>
            <p><strong>평균 혈당:</strong> ${data.avgBloodSugar}</p>
            <p><strong>총 활동 거리:</strong> ${data.totalDistance} km</p>
            <p><strong>총 활동 시간:</strong> ${data.totalActivityTime} 시간</p>
        `;

        // 차트 업데이트
        updateReportChart(data);
    } catch (error) {
        console.error('보고서 데이터 로드 실패:', error);
    }
}

/**
 * 필터 옵션 업데이트
 */
function updateFilterOptions() {
    // 노인 선택 옵션 업데이트
    const selects = document.querySelectorAll('[id$="ElderlyFilter"]');
    selects.forEach(select => {
        select.innerHTML = '<option value="">대상자 선택...</option>';
        DashboardState.elderlies.forEach(elderly => {
            const option = document.createElement('option');
            option.value = elderly.id;
            option.textContent = elderly.name;
            select.appendChild(option);
        });
    });
}

/**
 * 노인 검색
 */
function searchElderlies(query) {
    const container = document.getElementById('elderlyListContainer');
    const cards = container.querySelectorAll('.card');

    cards.forEach(card => {
        const name = card.querySelector('h3').textContent;
        card.style.display = name.includes(query) ? 'block' : 'none';
    });
}

/**
 * 차트 초기화
 */
function initializeCharts() {
    console.log('차트 초기화 중...');

    // 차트 데이터 준비
    const chartData = generateChartData();

    // 혈압 차트
    createChart('bpChart', {
        type: 'line',
        data: chartData.bloodPressure
    });

    // 맥박 차트
    createChart('pulseChart', {
        type: 'line',
        data: chartData.pulse
    });

    // 감정 추이 차트
    createChart('emotionChart', {
        type: 'line',
        data: chartData.emotionTrend
    });

    // 감정 분포 차트
    createChart('emotionDistChart', {
        type: 'pie',
        data: chartData.emotionDist
    });
}

/**
 * 모달 열기/닫기
 */
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

/**
 * 로딩 표시
 */
function showLoading(show = true) {
    document.getElementById('loadingOverlay').style.display = show ? 'flex' : 'none';
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
    alert('오류: ' + message);  // TODO: 더 나은 에러 UI 사용
}

/**
 * 로그아웃
 */
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

/**
 * 유틸리티 함수
 */
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

function formatDateTime(dateStr) {
    return new Date(dateStr).toLocaleString('ko-KR');
}

/**
 * TODO: 구현할 함수
 */
function viewElderly(elderlyId) {
    // TODO: 노인 상세 정보 보기
}

function addElderly(event) {
    // TODO: 노인 추가
}

function addMedicine(event) {
    // TODO: 약물 추가
}

function markMedicineTaken(medicineId) {
    // TODO: 약물 복용 표시
}

function markNotificationAsRead(notificationId) {
    // TODO: 알림 읽음 처리
}

function saveSetting() {
    // TODO: 설정 저장
}

function generateReport() {
    // TODO: 보고서 생성
}

function generateChartData() {
    // TODO: 실제 데이터 기반 차트 데이터 생성
    return {
        bloodPressure: {},
        pulse: {},
        emotionTrend: {},
        emotionDist: {}
    };
}

console.log('dashboard.js 로드 완료');
