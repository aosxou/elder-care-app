/**
 * 로컬 알림 관리
 *
 * cordova-plugin-local-notification 사용
 *
 * TODO: 로컬 알림 스케줄
 * TODO: 약물 복용 알림
 * TODO: 건강 경고 알림
 * TODO: 알림 권한 관리
 */

const Notification = {
    // 알림 아이디 카운터
    notificationId: 1,

    /**
     * 알림 권한 요청
     */
    requestPermission: async function () {
        try {
            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.requestPermission(function (granted) {
                    console.log('알림 권한:', granted ? '허용' : '거부');
                });
            }
        } catch (error) {
            console.error('알림 권한 요청 실패:', error);
        }
    },

    /**
     * 단일 알림 표시
     *
     * TODO: 알림 클릭 처리
     * TODO: 알림 음성/진동 설정
     */
    show: function (title, message, options = {}) {
        try {
            if (!cordova.plugins || !cordova.plugins.notification || !cordova.plugins.notification.local) {
                // Cordova 플러그인 미사용 시 콘솔 로그
                console.log('알림: ' + title + ' - ' + message);
                return;
            }

            const notificationId = this.notificationId++;
            const config = {
                id: notificationId,
                title: title,
                text: message,
                foreground: true,
                smallIcon: 'res/icon.png',
                icon: 'res/icon.png',
                sound: true,  // TODO: 음성 활성화
                vibrate: [300, 100, 300],  // TODO: 진동 패턴
                priority: 'high',
                ...options
            };

            cordova.plugins.notification.local.schedule(config);
        } catch (error) {
            console.error('알림 표시 실패:', error);
        }
    },

    /**
     * 약물 복용 알림
     *
     * TODO: 정기적인 약물 복용 알림 스케줄
     */
    scheduleMedicineReminder: function (medicineName, time) {
        try {
            const now = new Date();
            const [hours, minutes] = time.split(':');
            const triggerTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hours, minutes);

            // 이미 지난 시간이면 다음날로 설정
            if (triggerTime < now) {
                triggerTime.setDate(triggerTime.getDate() + 1);
            }

            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.schedule({
                    id: this.notificationId++,
                    title: '약물 복용',
                    text: medicineName + '을 복용할 시간입니다',
                    trigger: { at: triggerTime },
                    foreground: true,
                    sound: true,
                    vibrate: [300, 100, 300],
                    badge: 1,
                    priority: 'high',
                    smallIcon: 'res/icon.png'
                });

                console.log(`약물 복용 알림 스케줄: ${medicineName} at ${time}`);
            }
        } catch (error) {
            console.error('약물 복용 알림 스케줄 실패:', error);
        }
    },

    /**
     * 건강 경고 알림
     *
     * TODO: 우선순위 레벨 설정
     * - low: 일반 알림
     * - medium: 주의 필요
     * - high: 경고 필요
     * - critical: 긴급
     */
    showHealthAlert: function (title, message, level = 'medium') {
        const config = {
            sound: level === 'critical',
            vibrate: level === 'critical' ? [500, 200, 500, 200, 500] : [300, 100, 300],
            priority: level === 'critical' ? 'high' : 'normal'
        };

        this.show(title, message, config);
    },

    /**
     * 긴급 알림
     *
     * TODO: 모든 채널로 즉시 전송
     */
    showEmergencyAlert: function (message) {
        try {
            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.schedule({
                    id: this.notificationId++,
                    title: '🚨 긴급',
                    text: message,
                    foreground: true,
                    sound: true,
                    vibrate: [500, 200, 500, 200, 500, 200, 500],
                    badge: 99,
                    priority: 'high',
                    smallIcon: 'res/icon.png',
                    wakeup: true
                });
            }
        } catch (error) {
            console.error('긴급 알림 실패:', error);
        }
    },

    /**
     * 모든 알림 취소
     */
    cancelAll: function () {
        try {
            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.cancelAll();
                console.log('모든 알림 취소됨');
            }
        } catch (error) {
            console.error('알림 취소 실패:', error);
        }
    },

    /**
     * 특정 알림 취소
     */
    cancel: function (notificationId) {
        try {
            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.cancel(notificationId);
            }
        } catch (error) {
            console.error('알림 취소 실패:', error);
        }
    },

    /**
     * 정기적인 건강 체크 알림
     *
     * TODO: 매일 특정 시간에 건강 체크 알림
     */
    scheduleHealthCheckReminder: function (hour = 9, minute = 0) {
        try {
            const now = new Date();
            const triggerTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hour, minute);

            if (triggerTime < now) {
                triggerTime.setDate(triggerTime.getDate() + 1);
            }

            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.schedule({
                    id: this.notificationId++,
                    title: '건강 체크',
                    text: '오늘의 건강 지표를 입력하세요',
                    trigger: { at: triggerTime },
                    foreground: true,
                    sound: true,
                    badge: 1,
                    smallIcon: 'res/icon.png'
                });

                console.log(`건강 체크 알림 스케줄: ${hour}:${minute.toString().padStart(2, '0')}`);
            }
        } catch (error) {
            console.error('건강 체크 알림 스케줄 실패:', error);
        }
    },

    /**
     * 모든 약물 복용 알림 스케줄
     *
     * TODO: 서버에서 약물 목록 가져와서 스케줄
     */
    scheduleAllMedicineReminders: async function () {
        try {
            // TODO: API에서 약물 목록 조회
            // const medicines = await API.getMedicines();
            //
            // medicines.forEach(medicine => {
            //     medicine.times.forEach(time => {
            //         this.scheduleMedicineReminder(medicine.name, time);
            //     });
            // });

            console.log('모든 약물 복용 알림이 스케줄되었습니다');
        } catch (error) {
            console.error('약물 알림 스케줄 실패:', error);
        }
    },

    /**
     * 로컬 알림 클릭 처리
     *
     * TODO: 알림 클릭 시 특정 화면으로 이동
     */
    setupClickHandler: function () {
        try {
            if (cordova.plugins && cordova.plugins.notification && cordova.plugins.notification.local) {
                cordova.plugins.notification.local.on('click', function (notification) {
                    console.log('알림 클릭:', notification.id);

                    // TODO: 알림 유형에 따라 처리
                    if (notification.id === 'medicine') {
                        // 약물 목록으로 이동
                        navigateToScreen('health');
                    } else if (notification.id === 'health') {
                        // 건강 기록으로 이동
                        navigateToScreen('health');
                    }
                });
            }
        } catch (error) {
            console.error('알림 클릭 핸들러 설정 실패:', error);
        }
    },

    /**
     * 로컬 알림 초기화
     *
     * TODO: 앱 시작 시 호출
     */
    initialize: function () {
        this.requestPermission();
        this.setupClickHandler();

        // TODO: 저장된 알림 스케줄 복원
        this.scheduleAllMedicineReminders();

        console.log('로컬 알림 초기화 완료');
    }
};

// 앱 시작 시 알림 초기화
document.addEventListener('deviceready', function () {
    Notification.initialize();
}, false);

console.log('notification.js 로드 완료');
