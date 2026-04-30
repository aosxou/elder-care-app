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

        // 재시도 로직
        let lastError;
        for (let i = 0; i < this.retryAttempts; i++) {
            try {
                console.log(`${method} ${endpoint}`);

                const response = await fetch(url, config);

                if (!response.ok) {
                    // 401 인증 실패
                    if (response.status === 401) {
                        console.error('인증 실패');
                        // TODO: 로그인 페이지로 이동
                        logout();
                        throw new Error('인증 실패');
                    }

                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }

                const result = await response.json();
                return result;
            } catch (error) {
                lastError = error;

                if (i < this.retryAttempts - 1) {
                    const delay = this.retryDelay * Math.pow(2, i);
                    console.log(`재시도 대기: ${delay}ms`);
                    await new Promise(resolve => setTimeout(resolve, delay));
                }
            }
        }

        throw lastError;
    },

    // ====================================
    // 인증 관련
    // ====================================

    /**
     * 로그인
     */
    login: async function (email, password) {
        return this.request('POST', '/auth/login', {
            email: email,
            password: password
        });
    },

    /**
     * 회원가입
     */
    signup: async function (userData) {
        return this.request('POST', '/auth/signup', userData);
    },

    /**
     * 비밀번호 변경
     */
    changePassword: async function (oldPassword, newPassword) {
        return this.request('POST', '/auth/change-password', {
            oldPassword: oldPassword,
            newPassword: newPassword
        });
    },

    // ====================================
    // 사용자 관련
    // ====================================

    /**
     * 사용자 데이터 조회
     */
    getUserData: async function (userId) {
        return this.request('GET', `/users/${userId}/profile`);
    },

    /**
     * 프로필 업데이트
     */
    updateProfile: async function (userId, profileData) {
        return this.request('PUT', `/users/${userId}/profile`, profileData);
    },

    // ====================================
    // 건강 데이터
    // ====================================

    /**
     * 건강 기록 추가
     */
    addHealthRecord: async function (userId, healthData) {
        return this.request('POST', `/users/${userId}/health`, healthData);
    },

    /**
     * 건강 기록 조회
     */
    getHealthRecords: async function (userId, startDate, endDate) {
        const params = new URLSearchParams({
            startDate: startDate,
            endDate: endDate
        });
        return this.request('GET', `/users/${userId}/health?${params}`);
    },

    /**
     * 건강 통계
     */
    getHealthStats: async function (userId) {
        return this.request('GET', `/users/${userId}/health/stats`);
    },

    // ====================================
    // 약물 관리
    // ====================================

    /**
     * 약물 목록 조회
     */
    getMedicines: async function (userId) {
        return this.request('GET', `/users/${userId}/medicines`);
    },

    /**
     * 약물 추가
     */
    addMedicine: async function (userId, medicineData) {
        return this.request('POST', `/users/${userId}/medicines`, medicineData);
    },

    /**
     * 약물 복용 기록
     */
    recordMedicineTaken: async function (userId, medicineId) {
        return this.request('POST', `/users/${userId}/medicines/${medicineId}/taken`);
    },

    // ====================================
    // WebRTC 통화
    // ====================================

    /**
     * 통화 시작
     */
    initiateCall: async function (callData) {
        return this.request('POST', '/webrtc/call/start', callData);
    },

    /**
     * 통화 수락
     */
    answerCall: async function (callData) {
        return this.request('POST', '/webrtc/call/accept', callData);
    },

    /**
     * 통화 거절
     */
    rejectCall: async function (callId) {
        return this.request('POST', '/webrtc/call/reject', {
            callId: callId
        });
    },

    /**
     * 통화 종료
     */
    endCall: async function (callData) {
        return this.request('POST', '/webrtc/call/end', callData);
    },

    /**
     * 통화 기록 조회
     */
    getCallHistory: async function (userId, page = 0, size = 20) {
        return this.request('GET', `/webrtc/calls/history?userId=${userId}&page=${page}&size=${size}`);
    },

    /**
     * 통화 통계
     */
    getCallStats: async function (userId) {
        return this.request('GET', `/webrtc/calls/stats?userId=${userId}`);
    },

    // ====================================
    // 메시지/대화
    // ====================================

    /**
     * 대화 시작
     */
    startConversation: async function (participantId, targetId) {
        return this.request('POST', '/conversations/start', {
            participantId: participantId,
            targetId: targetId
        });
    },

    /**
     * 메시지 전송
     */
    sendMessage: async function (conversationId, senderId, message) {
        return this.request('POST', `/conversations/${conversationId}/message`, {
            senderId: senderId,
            content: message
        });
    },

    /**
     * 대화 히스토리
     */
    getConversationHistory: async function (conversationId, page = 0, size = 50) {
        return this.request('GET', `/conversations/${conversationId}/history?page=${page}&size=${size}`);
    },

    /**
     * 사용자의 모든 대화
     */
    getUserConversations: async function (userId, page = 0, size = 20) {
        return this.request('GET', `/conversations/user/${userId}?page=${page}&size=${size}`);
    },

    /**
     * 메시지 검색
     */
    searchMessages: async function (keyword, conversationId = null) {
        let endpoint = `/conversations/search?keyword=${keyword}`;
        if (conversationId) {
            endpoint += `&conversationId=${conversationId}`;
        }
        return this.request('GET', endpoint);
    },

    /**
     * AI와 대화
     */
    chatWithAI: async function (userId, message) {
        return this.request('POST', '/conversations/ai/chat', {
            userId: userId,
            message: message
        });
    },

    // ====================================
    // 보호자 기능
    // ====================================

    /**
     * 관리 중인 노인 목록
     */
    getManagedElderlies: async function (guardianId) {
        return this.request('GET', `/guardian/${guardianId}/elderly-list`);
    },

    /**
     * 노인 건강 데이터
     */
    getElderlyHealthData: async function (guardianId, elderlyId) {
        return this.request('GET', `/guardian/${guardianId}/elderly/${elderlyId}/health`);
    },

    /**
     * 노인 활동 기록
     */
    getElderlyActivities: async function (guardianId, elderlyId, page = 0, size = 20) {
        return this.request('GET', `/guardian/${guardianId}/elderly/${elderlyId}/activities?page=${page}&size=${size}`);
    },

    /**
     * 알림 설정
     */
    updateNotificationSettings: async function (guardianId, settings) {
        return this.request('POST', `/guardian/${guardianId}/notification-settings`, settings);
    },

    /**
     * 알림 목록
     */
    getNotifications: async function (userId, page = 0, size = 20) {
        return this.request('GET', `/guardian/${userId}/notifications?page=${page}&size=${size}`);
    },

    /**
     * 알림 읽음
     */
    markNotificationAsRead: async function (guardianId, notificationId) {
        return this.request('PUT', `/guardian/${guardianId}/notifications/${notificationId}/read`);
    },

    /**
     * 대시보드
     */
    getDashboard: async function (guardianId) {
        return this.request('GET', `/guardian/${guardianId}/dashboard`);
    },

    // ====================================
    // 일반 유틸리티
    // ====================================

    /**
     * 서버 상태 확인
     */
    healthCheck: async function () {
        return this.request('GET', '/health');
    },

    /**
     * 오프라인 캐싱된 요청 동기화
     *
     * TODO: 오프라인 중 저장된 요청을 온라인 복귀 시 재전송
     */
    syncOfflineRequests: async function () {
        // TODO: IndexedDB 또는 로컬 스토리지에서 캐시된 요청 조회
        // TODO: 각 요청 재전송
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

    if (error.response) {
        // 서버 에러
        const status = error.response.status;
        const data = error.response.data;

        switch (status) {
            case 401:
                showToast('인증이 필요합니다');
                logout();
                break;
            case 403:
                showToast('접근 권한이 없습니다');
                break;
            case 404:
                showToast('요청한 리소스를 찾을 수 없습니다');
                break;
            case 500:
                showToast('서버 에러가 발생했습니다');
                break;
            default:
                showToast(data.error || '요청 처리 중 오류가 발생했습니다');
        }
    } else if (error.request) {
        // 요청 에러
        showToast('요청을 보낼 수 없습니다. 인터넷 연결을 확인하세요');
    } else {
        // 기타 에러
        showToast('오류가 발생했습니다');
    }
}

// TODO: 외부 라이브러리 (Stomp.js, SockJS) 추가 필요
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
// <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

console.log('api.js 로드 완료');
