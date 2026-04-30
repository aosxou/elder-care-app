/**
 * Chart.js 기반 차트 관리
 *
 * TODO: 다양한 차트 타입 지원
 * TODO: 감정 분석 시각화
 * TODO: 실시간 차트 업데이트
 * TODO: 차트 내보내기
 */

// Chart.js 기본 설정
Chart.defaults.font.family = "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif";
Chart.defaults.color = '#7f8c8d';

const ChartManager = {
    charts: {},

    /**
     * 차트 생성
     */
    create: function (elementId, config) {
        const canvas = document.getElementById(elementId);
        if (!canvas) {
            console.warn(`Canvas not found: ${elementId}`);
            return;
        }

        // 기존 차트 제거
        if (this.charts[elementId]) {
            this.charts[elementId].destroy();
        }

        const defaultConfig = {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 15
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        };

        const mergedConfig = {
            type: config.type || 'line',
            data: config.data || {},
            options: { ...defaultConfig, ...config.options }
        };

        const ctx = canvas.getContext('2d');
        this.charts[elementId] = new Chart(ctx, mergedConfig);

        console.log(`Chart created: ${elementId}`);
    },

    /**
     * 차트 업데이트
     */
    update: function (elementId, data) {
        if (this.charts[elementId]) {
            this.charts[elementId].data = data;
            this.charts[elementId].update();
            console.log(`Chart updated: ${elementId}`);
        }
    },

    /**
     * 차트 제거
     */
    destroy: function (elementId) {
        if (this.charts[elementId]) {
            this.charts[elementId].destroy();
            delete this.charts[elementId];
        }
    }
};

// ====================================
// 라인 차트 (건강 지표)
// ====================================

/**
 * 혈압 차트
 */
function createBloodPressureChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const systolic = data.map(d => d.systolic);
    const diastolic = data.map(d => d.diastolic);

    ChartManager.create('bpChart', {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '수축기 (Systolic)',
                    data: systolic,
                    borderColor: '#e74c3c',
                    backgroundColor: 'rgba(231, 76, 60, 0.1)',
                    tension: 0.3,
                    fill: false,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#e74c3c'
                },
                {
                    label: '이완기 (Diastolic)',
                    data: diastolic,
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    tension: 0.3,
                    fill: false,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#3498db'
                }
            ]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: '혈압 추이'
                }
            },
            scales: {
                y: {
                    min: 60,
                    max: 180,
                    ticks: {
                        callback: function (value) {
                            return value + ' mmHg';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 맥박 차트
 */
function createPulseChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const pulses = data.map(d => d.pulse);

    ChartManager.create('pulseChart', {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '맥박 (BPM)',
                    data: pulses,
                    borderColor: '#2ecc71',
                    backgroundColor: 'rgba(46, 204, 113, 0.1)',
                    tension: 0.3,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    pointBackgroundColor: '#2ecc71'
                }
            ]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: '맥박 변화'
                }
            },
            scales: {
                y: {
                    min: 40,
                    max: 120,
                    ticks: {
                        callback: function (value) {
                            return value + ' bpm';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 혈당 차트
 */
function createBloodSugarChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const sugars = data.map(d => d.sugar);

    ChartManager.create('bloodSugarChart', {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '혈당 (mg/dL)',
                    data: sugars,
                    backgroundColor: [
                        ...sugars.map(s => s < 100 ? '#2ecc71' : s < 126 ? '#f39c12' : '#e74c3c')
                    ]
                }
            ]
        },
        options: {
            indexAxis: 'x',
            scales: {
                y: {
                    min: 70,
                    max: 200,
                    ticks: {
                        callback: function (value) {
                            return value + ' mg/dL';
                        }
                    }
                }
            }
        }
    });
}

// ====================================
// 감정 분석 차트
// ====================================

/**
 * 감정 추이 차트 (라인)
 */
function createEmotionTrendChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const scores = data.map(d => d.emotionScore * 100);  // -1 ~ 1을 0 ~ 200으로 변환

    ChartManager.create('emotionChart', {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '감정 점수',
                    data: scores,
                    borderColor: '#9b59b6',
                    backgroundColor: 'rgba(155, 89, 182, 0.1)',
                    tension: 0.3,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: function (context) {
                        const value = context.raw;
                        if (value < 70) return '#e74c3c';  // 부정적
                        if (value > 130) return '#2ecc71';  // 긍정적
                        return '#f39c12';  // 중립
                    }
                }
            ]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: '주간 감정 추이'
                },
                annotation: {
                    annotations: {
                        positive: {
                            type: 'box',
                            yMin: 130,
                            yMax: 200,
                            backgroundColor: 'rgba(46, 204, 113, 0.1)'
                        },
                        neutral: {
                            type: 'box',
                            yMin: 70,
                            yMax: 130,
                            backgroundColor: 'rgba(243, 156, 18, 0.1)'
                        },
                        negative: {
                            type: 'box',
                            yMin: 0,
                            yMax: 70,
                            backgroundColor: 'rgba(231, 76, 60, 0.1)'
                        }
                    }
                }
            },
            scales: {
                y: {
                    min: 0,
                    max: 200,
                    ticks: {
                        callback: function (value) {
                            if (value < 70) return '부정적';
                            if (value > 130) return '긍정적';
                            return '중립';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 감정 분포 차트 (파이)
 */
function createEmotionDistributionChart(data) {
    const emotions = {
        positive: 0,
        neutral: 0,
        negative: 0
    };

    data.forEach(d => {
        const score = d.emotionScore;
        if (score > 0.2) emotions.positive++;
        else if (score < -0.2) emotions.negative++;
        else emotions.neutral++;
    });

    ChartManager.create('emotionDistChart', {
        type: 'doughnut',
        data: {
            labels: ['긍정적', '중립', '부정적'],
            datasets: [
                {
                    data: [emotions.positive, emotions.neutral, emotions.negative],
                    backgroundColor: [
                        '#2ecc71',  // 초록색 (긍정)
                        '#f39c12',  // 주황색 (중립)
                        '#e74c3c'   // 빨간색 (부정)
                    ],
                    borderColor: 'white',
                    borderWidth: 2
                }
            ]
        },
        options: {
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return label + ': ' + percentage + '%';
                        }
                    }
                }
            }
        }
    });
}

// ====================================
// 활동 차트
// ====================================

/**
 * 보행 거리 차트
 */
function createDistanceChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const distances = data.map(d => d.distance);

    ChartManager.create('distanceChart', {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '거리 (km)',
                    data: distances,
                    backgroundColor: '#3498db',
                    borderColor: '#2980b9',
                    borderWidth: 1
                }
            ]
        },
        options: {
            scales: {
                y: {
                    ticks: {
                        callback: function (value) {
                            return value + ' km';
                        }
                    }
                }
            }
        }
    });
}

/**
 * 활동 시간 차트
 */
function createDurationChart(data) {
    const labels = data.map(d => formatDate(d.date));
    const durations = data.map(d => d.duration);

    ChartManager.create('durationChart', {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '시간 (분)',
                    data: durations,
                    backgroundColor: '#f39c12',
                    borderColor: '#d68910',
                    borderWidth: 1
                }
            ]
        },
        options: {
            scales: {
                y: {
                    ticks: {
                        callback: function (value) {
                            return value + ' 분';
                        }
                    }
                }
            }
        }
    });
}

// ====================================
// 통화 차트
// ====================================

/**
 * 통화 횟수 차트
 */
function createCallCountChart(data) {
    const labels = data.map(d => d.elderlyName);
    const counts = data.map(d => d.callCount);

    ChartManager.create('callCountChart', {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '통화 횟수',
                    data: counts,
                    backgroundColor: '#9b59b6',
                    borderColor: '#8e44ad',
                    borderWidth: 1
                }
            ]
        }
    });
}

/**
 * 평균 통화 시간 차트
 */
function createCallDurationChart(data) {
    const labels = data.map(d => d.elderlyName);
    const durations = data.map(d => d.avgDuration);

    ChartManager.create('callDurationChart', {
        type: 'radar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '평균 통화 시간 (분)',
                    data: durations,
                    borderColor: '#16a085',
                    backgroundColor: 'rgba(22, 160, 133, 0.2)',
                    pointBackgroundColor: '#16a085'
                }
            ]
        }
    });
}

// ====================================
// 보고서 차트
// ====================================

/**
 * 월별 혈압 차트
 */
function createMonthlyBPChart(data) {
    const labels = data.map(d => d.month);
    const systolic = data.map(d => d.avgSystolic);
    const diastolic = data.map(d => d.avgDiastolic);

    ChartManager.create('monthlyBPChart', {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '평균 수축기',
                    data: systolic,
                    borderColor: '#e74c3c',
                    tension: 0.3
                },
                {
                    label: '평균 이완기',
                    data: diastolic,
                    borderColor: '#3498db',
                    tension: 0.3
                }
            ]
        }
    });
}

/**
 * 월별 활동 차트
 */
function createMonthlyActivityChart(data) {
    const labels = data.map(d => d.month);
    const distances = data.map(d => d.totalDistance);
    const durations = data.map(d => d.totalDuration);

    ChartManager.create('monthlyActivityChart', {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '보행 거리 (km)',
                    data: distances,
                    backgroundColor: '#2ecc71'
                },
                {
                    label: '활동 시간 (시간)',
                    data: durations,
                    backgroundColor: '#f39c12'
                }
            ]
        }
    });
}

// ====================================
// 유틸리티 함수
// ====================================

/**
 * 날짜 포맷팅
 */
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('ko-KR', {
        month: 'short',
        day: 'numeric'
    });
}

/**
 * 차트 내보내기 (PNG)
 */
function exportChartAsImage(chartId) {
    const chart = ChartManager.charts[chartId];
    if (chart) {
        const link = document.createElement('a');
        link.href = chart.toBase64Image();
        link.download = `${chartId}_${new Date().getTime()}.png`;
        link.click();
    }
}

// TODO: 추가 기능
// - 실시간 차트 업데이트
// - 차트 여러 개 비교
// - 커스텀 범위 선택
// - 차트 애니메이션

console.log('chart.js 로드 완료');
