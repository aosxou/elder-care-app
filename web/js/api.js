/**
 * 백엔드 API 호출 유틸리티
 *
 * REST API를 통한 서버 통신
 *
 * TODO: 오프라인 캐싱
 * TODO: 요청 재시도
 * TODO: 타임아웃 처리
 * TODO: 에러 처리
 */

const API = {
    // TODO: 환경 변수로 설정
    baseURL: 'http://localhost:8080/api',
    timeout: 10000,
    retryAttempts: 3,
    retryDelay: 1000,

    /**
     * HTTP 요청 (공통)
     */
    request: async function (method, endpoint, data = null, options = {}) {
        const url = this.baseURL + endpoint;
        const headers = {
            'Content-Type': 'application/json'
        };

        // 인증 토큰 추가
        const token = localStorage.getItem('authToken');
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        const config = {
            method: method,
            headers: headers,
            timeout: options.timeout || this.timeout
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            config.body = JSON.stringify(data);
        }

        try {
            console.log(`${method} ${endpoint}`);

            const response = await fetch(url, config);

            if (!response.ok) {
                // 401 인증 실패
                if (response.status === 401) {
                    console.error('인증 실패');
                    // 로그인 페이지로 이동
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('user');
                    window.location.href = 'login.html';
                }

                throw new Error(`HTTP ${response.status}`);
            }

            const result = await response.json();
            return result;
        } catch (error) {
            console.error('API 요청 실패:', error);
            throw error;
        }
    },

    // ====================================
    // 인증
    // ====================================

    login: async function (email, password) {
        return this.request('POST', '/auth/login', {
            email: email,
            password: password
        });
    },

    // ====================================
    // 보호자 데이터
    // ====================================

    /**
     * 관리 대상자 목록
     */
    getManagedElderlies: async function () {
        return this.request('GET', '/guardian/elderly-list');
    },

    /**
     * 특정 노인의 건강 데이터
     */
    getElderlyHealthData: async function (elderlyId) {
        return this.request('GET', `/guardian/elderly/${elderlyId}/health`);
    },

    /**
     * 특정 노인의 활동 기록
     */
    getElderlyActivities: async function (elderlyId, filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/guardian/elderly/${elderlyId}/activities?${params}`);
    },

    /**
     * 대시보드 데이터 조회
     */
    getDashboardData: async function () {
        return this.request('GET', '/guardian/dashboard');
    },

    /**
     * 알림 목록
     */
    getNotifications: async function (page = 0, size = 20) {
        return this.request('GET', `/guardian/notifications?page=${page}&size=${size}`);
    },

    /**
     * 알림 읽음 처리
     */
    markNotificationAsRead: async function (notificationId) {
        return this.request('PUT', `/guardian/notifications/${notificationId}/read`);
    },

    // ====================================
    // 건강 데이터
    // ====================================

    /**
     * 건강 데이터 조회
     */
    getHealthData: async function (filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/health/records?${params}`);
    },

    /**
     * 건강 데이터 상세 조회
     */
    getHealthRecords: async function (elderlyId, startDate, endDate) {
        return this.request('GET', `/health/records?elderlyId=${elderlyId}&startDate=${startDate}&endDate=${endDate}`);
    },

    /**
     * 건강 지표 통계
     */
    getHealthStats: async function (elderlyId) {
        return this.request('GET', `/health/stats?elderlyId=${elderlyId}`);
    },

    // ====================================
    // 활동 데이터
    // ====================================

    /**
     * 활동 데이터 조회
     */
    getActivityData: async function (filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/activities?${params}`);
    },

    /**
     * 활동 통계
     */
    getActivityStats: async function (elderlyId) {
        return this.request('GET', `/activities/stats?elderlyId=${elderlyId}`);
    },

    // ====================================
    // 약물 관리
    // ====================================

    /**
     * 약물 데이터 조회
     */
    getMedicineData: async function () {
        return this.request('GET', '/medicines');
    },

    /**
     * 약물 복용 기록
     */
    recordMedicineTaken: async function (medicineId) {
        return this.request('POST', `/medicines/${medicineId}/taken`);
    },

    // ====================================
    // 통화 기록
    // ====================================

    /**
     * 통화 데이터 조회
     */
    getCallData: async function (filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/calls?${params}`);
    },

    /**
     * 통화 통계
     */
    getCallStats: async function (elderlyId) {
        return this.request('GET', `/calls/stats?elderlyId=${elderlyId}`);
    },

    // ====================================
    // 보고서
    // ====================================

    /**
     * 보고서 데이터 조회
     */
    getReportData: async function (filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/reports?${params}`);
    },

    /**
     * 보고서 생성 및 다운로드
     */
    generateReport: async function (filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request('GET', `/reports/generate?${params}`);
    },

    // ====================================
    // 사용자 관리
    // ====================================

    /**
     * 사용자 프로필
     */
    getUserProfile: async function () {
        return this.request('GET', '/users/profile');
    },

    /**
     * 프로필 업데이트
     */
    updateProfile: async function (profileData) {
        return this.request('PUT', '/users/profile', profileData);
    },

    /**
     * 알림 설정 조회
     */
    getNotificationSettings: async function () {
        return this.request('GET', '/users/notification-settings');
    },

    /**
     * 알림 설정 업데이트
     */
    updateNotificationSettings: async function (settings) {
        return this.request('PUT', '/users/notification-settings', settings);
    },

    /**
     * 비밀번호 변경
     */
    changePassword: async function (oldPassword, newPassword) {
        return this.request('POST', '/users/change-password', {
            oldPassword: oldPassword,
            newPassword: newPassword
        });
    },

    // ====================================
    // 대상자 관리
    // ====================================

    /**
     * 대상자 추가
     */
    addElderly: async function (elderlyData) {
        return this.request('POST', '/guardian/elderly', elderlyData);
    },

    /**
     * 대상자 정보 업데이트
     */
    updateElderly: async function (elderlyId, elderlyData) {
        return this.request('PUT', `/guardian/elderly/${elderlyId}`, elderlyData);
    },

    /**
     * 대상자 제거
     */
    removeElderly: async function (elderlyId) {
        return this.request('DELETE', `/guardian/elderly/${elderlyId}`);
    },

    // ====================================
    // 약물 관리
    // ====================================

    /**
     * 약물 추가
     */
    addMedicine: async function (medicineData) {
        return this.request('POST', '/medicines', medicineData);
    },

    /**
     * 약물 업데이트
     */
    updateMedicine: async function (medicineId, medicineData) {
        return this.request('PUT', `/medicines/${medicineId}`, medicineData);
    },

    /**
     * 약물 삭제
     */
    removeMedicine: async function (medicineId) {
        return this.request('DELETE', `/medicines/${medicineId}`);
    },

    // ====================================
    // 검색 및 필터
    // ====================================

    /**
     * 건강 데이터 검색
     */
    searchHealthData: async function (keyword, filters = {}) {
        const params = new URLSearchParams({
            q: keyword,
            ...filters
        });
        return this.request('GET', `/health/search?${params}`);
    },

    /**
     * 활동 데이터 검색
     */
    searchActivities: async function (keyword, filters = {}) {
        const params = new URLSearchParams({
            q: keyword,
            ...filters
        });
        return this.request('GET', `/activities/search?${params}`);
    },

    // ====================================
    // 모니터링
    // ====================================

    /**
     * 실시간 알림 구독 (Server-Sent Events)
     */
    subscribeToNotifications: function (callback) {
        const token = localStorage.getItem('authToken');
        const eventSource = new EventSource(
            `${this.baseURL}/notifications/stream?token=${token}`
        );

        eventSource.onmessage = function (event) {
            const notification = JSON.parse(event.data);
            callback(notification);
        };

        eventSource.onerror = function (error) {
            console.error('알림 스트림 오류:', error);
            eventSource.close();
        };

        return eventSource;
    }
};

// ====================================
// 에러 핸들링
// ====================================

/**
 * API 에러 처리
 */
function handleAPIError(error) {
    console.error('API 에러:', error);

    if (error instanceof TypeError) {
        console.error('네트워크 오류: 서버에 연결할 수 없습니다');
        alert('네트워크 오류: 인터넷 연결을 확인하세요');
    } else if (error instanceof SyntaxError) {
        console.error('JSON 파싱 오류');
        alert('서버 응답 오류가 발생했습니다');
    } else {
        alert('오류: ' + error.message);
    }
}

console.log('api.js 로드 완료');
