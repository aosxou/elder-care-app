/*
  TODO: DOM 요소 선택 및 초기화
  TODO: 이벤트 리스너 등록
  TODO: 페이지 로드 시 데이터 로드
  TODO: 폼 제출 처리
  TODO: 실시간 데이터 업데이트 (WebSocket 또는 폴링)
*/

// 상수 정의
const API_BASE_URL = 'http://localhost:3000/api';
const REFRESH_INTERVAL = 5000; // 5초마다 데이터 새로고침

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('페이지 로드 완료');

    // TODO: 인증 확인
    // TODO: 사용자 정보 로드

    initializeEventListeners();
    loadDashboardData();
    startAutoRefresh();
});

/*
  TODO: 이벤트 리스너 초기화
  - 모든 버튼 클릭 이벤트
  - 폼 제출 이벤트
  - 네비게이션 클릭 이벤트
  - 검색 입력 이벤트
*/
function initializeEventListeners() {
    // 건강 기록 폼 제출
    const healthForm = document.getElementById('healthForm');
    if (healthForm) {
        healthForm.addEventListener('submit', handleHealthFormSubmit);
    }

    // 일정 추가 폼 제출
    const scheduleForm = document.getElementById('scheduleForm');
    if (scheduleForm) {
        scheduleForm.addEventListener('submit', handleScheduleFormSubmit);
    }

    // 설정 폼 제출
    const settingsForm = document.getElementById('settingsForm');
    if (settingsForm) {
        settingsForm.addEventListener('submit', handleSettingsFormSubmit);
    }

    // 사용자 추가 버튼
    const addUserBtn = document.querySelector('.btn-primary');
    if (addUserBtn) {
        // TODO: 사용자 추가 모달 열기
    }
}

/*
  TODO: 대시보드 데이터 로드
  - 실시간 건강 지표 조회
  - 최근 활동 로그 로드
  - 긴급 알림 확인
  - 다음 약물 복용 시간 조회
*/
function loadDashboardData() {
    console.log('대시보드 데이터 로드 중...');

    // TODO: API 호출로 데이터 가져오기
    // const healthData = await fetchHealthData();
    // const activities = await fetchActivities();
    // const alerts = await fetchAlerts();

    // TODO: UI 업데이트
    // updateHealthIndicators(healthData);
    // updateRecentActivities(activities);
    // displayAlerts(alerts);
}

/*
  TODO: 건강 기록 폼 제출 처리
  - 입력값 검증
  - API에 데이터 전송
  - 성공/실패 메시지 표시
  - 폼 초기화
*/
function handleHealthFormSubmit(e) {
    e.preventDefault();
    console.log('건강 기록 제출됨');

    // TODO: 폼 데이터 수집
    // const formData = new FormData(e.target);
    // const healthData = {
    //     bloodPressure: formData.get('bloodPressure'),
    //     pulse: formData.get('pulse'),
    //     bloodSugar: formData.get('bloodSugar'),
    //     notes: formData.get('notes'),
    //     timestamp: new Date().toISOString()
    // };

    // TODO: API 호출
    // submitHealthData(healthData)
    //     .then(() => {
    //         showSuccessMessage('건강 기록이 저장되었습니다.');
    //         e.target.reset();
    //         loadDashboardData(); // 데이터 새로고침
    //     })
    //     .catch(error => {
    //         showErrorMessage('저장 중 오류가 발생했습니다.');
    //     });
}

/*
  TODO: 일정 추가 폼 제출 처리
  - 입력값 검증 (시간 형식, 필수 필드)
  - API에 일정 데이터 전송
  - 캘린더 UI 업데이트
  - 알림 설정
*/
function handleScheduleFormSubmit(e) {
    e.preventDefault();
    console.log('일정 추가됨');

    // TODO: 폼 데이터 수집
    // const formData = new FormData(e.target);
    // const scheduleData = {
    //     title: formData.get('title'),
    //     dateTime: formData.get('datetime'),
    //     type: formData.get('type'),
    //     userId: getCurrentUserId()
    // };

    // TODO: API 호출
    // addSchedule(scheduleData)
    //     .then(() => {
    //         showSuccessMessage('일정이 추가되었습니다.');
    //         e.target.reset();
    //         // TODO: 캘린더 새로고침
    //     })
    //     .catch(error => {
    //         showErrorMessage('일정 추가 중 오류가 발생했습니다.');
    //     });
}

/*
  TODO: 설정 폼 제출 처리
  - 사용자 설정 저장
  - 알림 설정 업데이트
  - 시스템 설정 변경
  - 데이터 백업
*/
function handleSettingsFormSubmit(e) {
    e.preventDefault();
    console.log('설정 저장됨');

    // TODO: 폼 데이터 수집
    // const formData = new FormData(e.target);
    // const settings = {
    //     healthAlerts: formData.get('healthAlerts'),
    //     medicineReminders: formData.get('medicineReminders'),
    //     emergencyAlerts: formData.get('emergencyAlerts'),
    //     language: formData.get('language'),
    //     theme: formData.get('theme')
    // };

    // TODO: API 호출
    // updateSettings(settings)
    //     .then(() => {
    //         showSuccessMessage('설정이 저장되었습니다.');
    //     })
    //     .catch(error => {
    //         showErrorMessage('설정 저장 중 오류가 발생했습니다.');
    //     });
}

/*
  TODO: 실시간 데이터 자동 업데이트 시작
  - setInterval 또는 WebSocket 사용
  - 서버 연결 상태 확인
  - 업데이트 실패 시 재시도 로직
*/
function startAutoRefresh() {
    console.log('자동 새로고침 시작');

    // TODO: 실시간 데이터 업데이트
    // setInterval(() => {
    //     loadDashboardData();
    // }, REFRESH_INTERVAL);
}

/*
  TODO: UI 업데이트 함수
  - 건강 지표 표시
  - 활동 로그 업데이트
  - 알림 표시
*/
function updateHealthIndicators(healthData) {
    // TODO: 건강 지표 카드 업데이트
}

function updateRecentActivities(activities) {
    // TODO: 최근 활동 목록 업데이트
}

function displayAlerts(alerts) {
    // TODO: 긴급 알림 표시
}

/*
  TODO: 메시지 표시 함수
  - 성공 메시지
  - 에러 메시지
  - 경고 메시지
  - 정보 메시지
*/
function showSuccessMessage(message) {
    console.log('성공:', message);
    // TODO: 토스트 알림 또는 모달로 메시지 표시
}

function showErrorMessage(message) {
    console.error('에러:', message);
    // TODO: 토스트 알림 또는 모달로 에러 메시지 표시
}

// 사용자 정보 관련 함수
function getCurrentUserId() {
    // TODO: 현재 로그인한 사용자 ID 반환
    return localStorage.getItem('userId');
}

// 로그아웃 함수
function logout() {
    // TODO: 세션 삭제
    // TODO: 로그인 페이지로 리다이렉트
    console.log('로그아웃됨');
    // window.location.href = '/login.html';
}

// 페이지 내 네비게이션
function navigateToSection(sectionId) {
    // TODO: 부드러운 스크롤
    const section = document.getElementById(sectionId);
    if (section) {
        section.scrollIntoView({ behavior: 'smooth' });
    }
}

console.log('main.js 로드 완료');
