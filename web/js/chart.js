/*
  차트 및 데이터 시각화 유틸리티

  TODO: Chart.js 라이브러리 통합
  TODO: 다양한 차트 타입 구현 (선, 막대, 원형, 영역)
  TODO: 건강 지표 시각화
  TODO: 활동 통계 시각화
  TODO: 약물 복용 현황 시각화
  TODO: 반응형 차트
  TODO: 차트 인터랙션 (드릴다운, 필터링)
*/

// 차트 설정
const CHART_CONFIG = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            position: 'top',
        },
        title: {
            display: true,
            padding: 20
        }
    }
};

/*
  TODO: 건강 지표 차트
  - 혈압 변화 추이
  - 맥박 변화 추이
  - 혈당 변화 추이
  - 체중 변화 추이
*/
function initializeHealthChart(canvasId, healthData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 차트 생성
    // const chart = new Chart(ctx, {
    //     type: 'line',
    //     data: {
    //         labels: healthData.dates,
    //         datasets: [
    //             {
    //                 label: '혈압 (수축기)',
    //                 data: healthData.systolicBP,
    //                 borderColor: '#e74c3c',
    //                 backgroundColor: 'rgba(231, 76, 60, 0.1)',
    //                 tension: 0.3
    //             },
    //             {
    //                 label: '맥박',
    //                 data: healthData.pulse,
    //                 borderColor: '#3498db',
    //                 backgroundColor: 'rgba(52, 152, 219, 0.1)',
    //                 tension: 0.3
    //             }
    //         ]
    //     },
    //     options: {
    //         ...CHART_CONFIG,
    //         plugins: {
    //             ...CHART_CONFIG.plugins,
    //             title: {
    //                 ...CHART_CONFIG.plugins.title,
    //                 text: '건강 지표 추이'
    //             }
    //         },
    //         scales: {
    //             y: {
    //                 beginAtZero: true
    //             }
    //         }
    //     }
    // });
    // return chart;
}

/*
  TODO: 활동 통계 차트
  - 일일 보행 거리
  - 일일 활동 시간
  - 주간 활동 패턴
  - 월간 활동 비교
*/
function initializeActivityChart(canvasId, activityData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 막대 차트 생성
    // const chart = new Chart(ctx, {
    //     type: 'bar',
    //     data: {
    //         labels: activityData.days,
    //         datasets: [
    //             {
    //                 label: '보행 거리 (km)',
    //                 data: activityData.distance,
    //                 backgroundColor: '#2ecc71'
    //             },
    //             {
    //                 label: '활동 시간 (분)',
    //                 data: activityData.duration,
    //                 backgroundColor: '#f39c12'
    //             }
    //         ]
    //     },
    //     options: {
    //         ...CHART_CONFIG,
    //         plugins: {
    //             ...CHART_CONFIG.plugins,
    //             title: {
    //                 ...CHART_CONFIG.plugins.title,
    //                 text: '일일 활동 통계'
    //             }
    //         },
    //         scales: {
    //             y: {
    //                 beginAtZero: true
    //             }
    //         }
    //     }
    // });
    // return chart;
}

/*
  TODO: 약물 복용 현황 차트
  - 약물별 복용률
  - 월간 복용 기록
  - 약물 종류별 분포
*/
function initializeMedicineChart(canvasId, medicineData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 원형 차트 생성
    // const chart = new Chart(ctx, {
    //     type: 'doughnut',
    //     data: {
    //         labels: medicineData.medicineNames,
    //         datasets: [
    //             {
    //                 data: medicineData.adherenceRate,
    //                 backgroundColor: [
    //                     '#3498db',
    //                     '#2ecc71',
    //                     '#f39c12',
    //                     '#e74c3c',
    //                     '#9b59b6'
    //                 ]
    //             }
    //         ]
    //     },
    //     options: {
    //         ...CHART_CONFIG,
    //         plugins: {
    //             ...CHART_CONFIG.plugins,
    //             title: {
    //                 ...CHART_CONFIG.plugins.title,
    //                 text: '약물 복용 현황'
    //             }
    //         }
    //     }
    // });
    // return chart;
}

/*
  TODO: 혈압 분포 차트
  - 수축기 혈압 분포
  - 이완기 혈압 분포
  - 정상/주의/위험 범위 표시
*/
function initializeBPDistributionChart(canvasId, bpData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 히스토그램 또는 스캐터 차트 생성
    // 정상: 120/80 이하
    // 주의: 120-139/80-89
    // 고혈압: 140 이상
}

/*
  TODO: 시간대별 활동 패턴 차트
  - 시간대별 활동량
  - 시간대별 활동 강도
  - 시간대별 활동 위험도
*/
function initializeTimeSeriesChart(canvasId, timeSeriesData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 영역 차트 생성
}

/*
  TODO: 사용자별 비교 차트
  - 여러 사용자의 건강 지표 비교
  - 그룹 통계 비교
*/
function initializeComparisonChart(canvasId, comparisonData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 다중 데이터셋 차트 생성
}

/*
  TODO: 레이더 차트
  - 다양한 건강 지표를 한눈에 비교
  - 목표치 대비 현황 비교
*/
function initializeRadarChart(canvasId, radarData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error(`캔버스 요소를 찾을 수 없습니다: ${canvasId}`);
        return;
    }

    // TODO: Chart.js 레이더 차트 생성
}

/*
  데이터 포맷팅 함수
*/

// 날짜 배열 생성 (지난 N일)
function generateDateLabels(days = 7) {
    const labels = [];
    const today = new Date();

    for (let i = days - 1; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i);
        labels.push(date.toLocaleDateString('ko-KR', {
            month: 'short',
            day: 'numeric'
        }));
    }

    return labels;
}

// 시간 배열 생성 (00:00 ~ 23:00)
function generateHourLabels() {
    const labels = [];
    for (let i = 0; i < 24; i++) {
        labels.push(`${String(i).padStart(2, '0')}:00`);
    }
    return labels;
}

// 건강 데이터 포맷팅
function formatHealthData(rawData) {
    return {
        dates: rawData.map(d => new Date(d.timestamp).toLocaleDateString('ko-KR')),
        systolicBP: rawData.map(d => d.bloodPressure.systolic),
        diastolicBP: rawData.map(d => d.bloodPressure.diastolic),
        pulse: rawData.map(d => d.pulse),
        bloodSugar: rawData.map(d => d.bloodSugar)
    };
}

// 활동 데이터 포맷팅
function formatActivityData(rawData) {
    return {
        days: rawData.map(d => new Date(d.date).toLocaleDateString('ko-KR', { weekday: 'short' })),
        distance: rawData.map(d => d.distance),
        duration: rawData.map(d => d.duration),
        calories: rawData.map(d => d.calories)
    };
}

// 약물 데이터 포맷팅
function formatMedicineData(rawData) {
    return {
        medicineNames: rawData.map(d => d.name),
        adherenceRate: rawData.map(d => (d.takenDays / d.totalDays) * 100)
    };
}

/*
  차트 업데이트 함수
*/

function updateChartData(chart, newData) {
    chart.data = newData;
    chart.update();
}

function updateChartOptions(chart, newOptions) {
    Object.assign(chart.options, newOptions);
    chart.update();
}

/*
  차트 내보내기 함수
*/

// 차트를 이미지로 다운로드
function downloadChartAsImage(chart, fileName = 'chart.png') {
    const url = chart.toBase64Image();
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
}

// 차트를 PDF로 내보내기
function exportChartAsPDF(chart, fileName = 'chart.pdf') {
    // TODO: jsPDF 또는 다른 라이브러리 사용
}

console.log('chart.js 로드 완료');
