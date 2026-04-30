/**
 * WebSocket 연결 관리
 *
 * 서버와의 실시간 통신 (STOMP over WebSocket)
 *
 * TODO: 자동 재연결
 * TODO: 하트비트 관리
 * TODO: 메시지 큐
 * TODO: 구독 관리
 */

const WebSocket = {
    stompClient: null,
    isConnected: false,
    serverUrl: 'ws://localhost:8080/ws',  // TODO: 환경 변수로 설정
    reconnectAttempts: 0,
    maxReconnectAttempts: 10,
    reconnectDelay: 3000,
    subscriptions: {},
    messageQueue: [],

    /**
     * 서버에 연결
     */
    connect: function () {
        if (this.isConnected) {
            console.log('이미 연결되어 있습니다');
            return;
        }

        try {
            console.log('WebSocket 연결 중: ' + this.serverUrl);

            // SockJS 사용 (WebSocket 대체)
            const socket = new SockJS(this.serverUrl);
            this.stompClient = Stomp.over(socket);

            const self = this;

            // 연결 성공
            this.stompClient.connect(
                {
                    // 인증 헤더
                    'Authorization': 'Bearer ' + localStorage.getItem('authToken')
                },
                function (frame) {
                    console.log('WebSocket 연결 성공');
                    self.isConnected = true;
                    self.reconnectAttempts = 0;

                    // 모든 구독 설정
                    self.setupSubscriptions();

                    // 메시지 큐 처리
                    self.processMessageQueue();
                },
                function (error) {
                    console.error('WebSocket 연결 실패:', error);
                    self.handleConnectionError();
                }
            );
        } catch (error) {
            console.error('WebSocket 연결 중 오류:', error);
            this.handleConnectionError();
        }
    },

    /**
     * 연결 끊기
     */
    disconnect: function () {
        if (this.stompClient && this.isConnected) {
            this.stompClient.disconnect(function () {
                console.log('WebSocket 연결 끊김');
            });
            this.isConnected = false;
        }
    },

    /**
     * 구독 설정
     */
    setupSubscriptions: function () {
        // 개인 메시지 구독
        this.subscribe('/user/queue/messages', function (message) {
            const data = JSON.parse(message.body);
            console.log('메시지 수신:', data);

            // TODO: 메시지 처리
            Notification.show('메시지', data.content);
        });

        // 알림 구독
        this.subscribe('/user/queue/notifications', function (notification) {
            const data = JSON.parse(notification.body);
            console.log('알림 수신:', data);

            // TODO: 알림 처리
            handleNotification(data);
        });

        // 건강 경고 구독
        this.subscribe('/user/queue/health-alerts', function (alert) {
            const data = JSON.parse(alert.body);
            console.log('건강 경고 수신:', data);

            // TODO: 건강 경고 처리
            Notification.showHealthAlert('건강 경고', data.message, data.level);
        });

        // 통화 요청 구독
        this.subscribe('/user/queue/call-requests', function (request) {
            const data = JSON.parse(request.body);
            console.log('통화 요청 수신:', data);

            // TODO: 통화 요청 처리
            handleIncomingCall(data);
        });
    },

    /**
     * 특정 주제 구독
     */
    subscribe: function (destination, callback) {
        if (!this.stompClient || !this.isConnected) {
            console.warn('WebSocket 연결이 없습니다');
            return;
        }

        try {
            const subscription = this.stompClient.subscribe(destination, callback);
            this.subscriptions[destination] = subscription;
            console.log('구독 성공: ' + destination);
        } catch (error) {
            console.error('구독 실패:', error);
        }
    },

    /**
     * 특정 주제 구독 취소
     */
    unsubscribe: function (destination) {
        if (this.subscriptions[destination]) {
            this.subscriptions[destination].unsubscribe();
            delete this.subscriptions[destination];
            console.log('구독 취소: ' + destination);
        }
    },

    /**
     * 메시지 전송
     */
    send: function (destination, payload) {
        if (!this.stompClient || !this.isConnected) {
            console.warn('WebSocket 연결이 없습니다. 메시지를 큐에 추가합니다.');
            this.messageQueue.push({ destination, payload });
            return;
        }

        try {
            this.stompClient.send(destination, {}, JSON.stringify(payload));
            console.log('메시지 전송:', destination);
        } catch (error) {
            console.error('메시지 전송 실패:', error);
        }
    },

    /**
     * 메시지 큐 처리
     */
    processMessageQueue: function () {
        while (this.messageQueue.length > 0) {
            const { destination, payload } = this.messageQueue.shift();
            this.send(destination, payload);
        }
    },

    /**
     * 연결 에러 처리
     */
    handleConnectionError: function () {
        this.isConnected = false;

        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);

            console.log(`${delay}ms 후 재연결 시도 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

            setTimeout(() => {
                this.connect();
            }, delay);
        } else {
            console.error('최대 재연결 횟수 초과');
            showToast('서버 연결에 실패했습니다');
        }
    }
};

/**
 * 수신 메시지 처리
 */
function handleNotification(data) {
    console.log('알림 처리:', data);

    // TODO: 알림 유형별 처리
    switch (data.type) {
        case 'HEALTH_ALERT':
            Notification.showHealthAlert(data.title, data.message, data.level);
            break;
        case 'MEDICINE_REMINDER':
            Notification.show(data.title, data.message);
            break;
        case 'EMERGENCY':
            Notification.showEmergencyAlert(data.message);
            break;
        default:
            Notification.show(data.title, data.message);
    }

    // 대시보드 업데이트
    if (AppState.currentScreen === 'dashboard') {
        loadDashboardData();
    }
}

/**
 * 수신 통화 처리
 */
function handleIncomingCall(data) {
    console.log('수신 통화:', data);

    // TODO: 통화 UI 표시
    // TODO: 수락/거절 옵션 제공

    const accept = confirm(`${data.callerName}에게서 통화 요청이 있습니다. 수락하시겠습니까?`);

    if (accept) {
        WebRTC.answerCall(data.callId);
    } else {
        WebRTC.rejectCall(data.callId);
    }
}

// 앱 시작 시 WebSocket 연결 (로그인 후)
document.addEventListener('deviceready', function () {
    // TODO: 로그인 후에만 연결
    if (AppState.isLoggedIn) {
        WebSocket.connect();
    }
}, false);

console.log('websocket.js 로드 완료');
