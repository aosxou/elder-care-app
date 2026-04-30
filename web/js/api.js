/*
  API 요청 유틸리티

  TODO: API 엔드포인트 정의
  TODO: 인증 토큰 관리
  TODO: 요청/응답 인터셉터
  TODO: 에러 처리
  TODO: 타임아웃 설정
*/

const API_BASE_URL = 'http://localhost:3000/api';

// 기본 헤더 설정
const DEFAULT_HEADERS = {
    'Content-Type': 'application/json'
};

/*
  TODO: 사용자 관련 API
  - 사용자 목록 조회
  - 사용자 상세 정보 조회
  - 사용자 추가
  - 사용자 편집
  - 사용자 삭제
*/
async function getUsers() {
    return fetchAPI(`${API_BASE_URL}/users`, 'GET');
}

async function getUserById(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}`, 'GET');
}

async function createUser(userData) {
    return fetchAPI(`${API_BASE_URL}/users`, 'POST', userData);
}

async function updateUser(userId, userData) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}`, 'PUT', userData);
}

async function deleteUser(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}`, 'DELETE');
}

/*
  TODO: 건강 데이터 관련 API
  - 건강 기록 조회
  - 건강 기록 추가
  - 건강 기록 편집
  - 건강 기록 삭제
  - 건강 지표 통계
*/
async function getHealthRecords(userId, startDate, endDate) {
    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate
    });
    return fetchAPI(`${API_BASE_URL}/users/${userId}/health?${params}`, 'GET');
}

async function addHealthRecord(userId, healthData) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/health`, 'POST', healthData);
}

async function getHealthStats(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/health/stats`, 'GET');
}

/*
  TODO: 약물 복용 관련 API
  - 약물 목록 조회
  - 약물 추가
  - 약물 편집
  - 약물 삭제
  - 복용 기록 조회
  - 복용 여부 기록
*/
async function getMedicines(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/medicines`, 'GET');
}

async function addMedicine(userId, medicineData) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/medicines`, 'POST', medicineData);
}

async function recordMedicineTaken(userId, medicineId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/medicines/${medicineId}/taken`, 'POST');
}

async function getMedicineSchedule(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/medicines/schedule`, 'GET');
}

/*
  TODO: 일정 관련 API
  - 일정 목록 조회
  - 일정 추가
  - 일정 편집
  - 일정 삭제
  - 일정 알림 설정
*/
async function getSchedules(userId, startDate, endDate) {
    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate
    });
    return fetchAPI(`${API_BASE_URL}/users/${userId}/schedules?${params}`, 'GET');
}

async function addSchedule(userId, scheduleData) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/schedules`, 'POST', scheduleData);
}

async function updateSchedule(userId, scheduleId, scheduleData) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/schedules/${scheduleId}`, 'PUT', scheduleData);
}

async function deleteSchedule(userId, scheduleId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/schedules/${scheduleId}`, 'DELETE');
}

/*
  TODO: 활동 관련 API
  - 활동 로그 조회
  - 활동 통계
  - 걸음수 조회
  - 활동 시간 조회
*/
async function getActivityLogs(userId, startDate, endDate) {
    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate
    });
    return fetchAPI(`${API_BASE_URL}/users/${userId}/activities?${params}`, 'GET');
}

async function getActivityStats(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/activities/stats`, 'GET');
}

/*
  TODO: 알림 관련 API
  - 알림 목록 조회
  - 알림 읽음 처리
  - 알림 삭제
  - 알림 설정 변경
*/
async function getNotifications(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/notifications`, 'GET');
}

async function markNotificationAsRead(notificationId) {
    return fetchAPI(`${API_BASE_URL}/notifications/${notificationId}/read`, 'POST');
}

async function updateNotificationSettings(userId, settings) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/notification-settings`, 'PUT', settings);
}

/*
  TODO: 인증 관련 API
  - 로그인
  - 로그아웃
  - 토큰 갱신
  - 비밀번호 변경
*/
async function login(username, password) {
    return fetchAPI(`${API_BASE_URL}/auth/login`, 'POST', {
        username: username,
        password: password
    });
}

async function logout() {
    return fetchAPI(`${API_BASE_URL}/auth/logout`, 'POST');
}

async function changePassword(userId, oldPassword, newPassword) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/change-password`, 'POST', {
        oldPassword: oldPassword,
        newPassword: newPassword
    });
}

/*
  TODO: 의료 데이터 API
  - 의료 기록 조회
  - 처방약 조회
  - 알레르기 정보
  - 건강 조건 조회
*/
async function getMedicalRecords(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/medical-records`, 'GET');
}

async function getAllergies(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/allergies`, 'GET');
}

async function getHealthConditions(userId) {
    return fetchAPI(`${API_BASE_URL}/users/${userId}/health-conditions`, 'GET');
}

/*
  기본 API 요청 함수

  TODO: 요청 재시도 로직
  TODO: 요청 캐싱
  TODO: 요청/응답 로깅
  TODO: 타임아웃 처리
*/
async function fetchAPI(url, method = 'GET', data = null) {
    const options = {
        method: method,
        headers: DEFAULT_HEADERS
    };

    // 인증 토큰 추가
    const token = localStorage.getItem('authToken');
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    // 요청 본문 추가 (POST, PUT의 경우)
    if (data && (method === 'POST' || method === 'PUT')) {
        options.body = JSON.stringify(data);
    }

    try {
        console.log(`API 요청: ${method} ${url}`);

        const response = await fetch(url, options);

        // 응답 처리
        if (!response.ok) {
            // TODO: 401 (인증 실패) 처리
            if (response.status === 401) {
                console.error('인증 실패. 로그인 페이지로 리다이렉트됩니다.');
                // window.location.href = '/login.html';
            }

            // TODO: 다른 에러 코드 처리
            throw new Error(`API 요청 실패: ${response.status} ${response.statusText}`);
        }

        // 응답 파싱
        const responseData = await response.json();
        console.log(`API 응답: ${method} ${url}`, responseData);

        return responseData;
    } catch (error) {
        console.error(`API 요청 중 오류 발생: ${error.message}`);
        // TODO: 에러 처리 및 사용자 알림
        throw error;
    }
}

/*
  TODO: API 요청 재시도 로직
  지수 백오프를 사용한 재시도
*/
async function fetchAPIWithRetry(url, method = 'GET', data = null, maxRetries = 3) {
    let lastError;

    for (let i = 0; i < maxRetries; i++) {
        try {
            return await fetchAPI(url, method, data);
        } catch (error) {
            lastError = error;
            if (i < maxRetries - 1) {
                const delay = Math.pow(2, i) * 1000; // 지수 백오프: 1초, 2초, 4초
                console.log(`${delay}ms 후 재시도합니다. (${i + 1}/${maxRetries})`);
                await new Promise(resolve => setTimeout(resolve, delay));
            }
        }
    }

    throw lastError;
}

/*
  TODO: API 요청 취소 로직
  AbortController를 사용한 요청 취소
*/
const requestControllers = new Map();

function createAbortSignal(requestId) {
    const controller = new AbortController();
    requestControllers.set(requestId, controller);
    return controller.signal;
}

function cancelRequest(requestId) {
    const controller = requestControllers.get(requestId);
    if (controller) {
        controller.abort();
        requestControllers.delete(requestId);
    }
}

console.log('api.js 로드 완료');
