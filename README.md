# 노인 돌봄 관리 애플리케이션 (Elder Care Management System)

노인 돌봄 관리 애플리케이션은 노인의 건강을 모니터링하고 돌봄 서비스를 효율적으로 관리하는 통합 시스템입니다.

## 📋 목차

- [프로젝트 구조](#프로젝트-구조)
- [주요 기능](#주요-기능)
- [설치 및 실행](#설치-및-실행)
- [기술 스택](#기술-스택)
- [개발 가이드](#개발-가이드)
- [API 문서](#api-문서)
- [기여 방법](#기여-방법)

## 📁 프로젝트 구조

```
elder-care-app/
├── web/                      # 웹 대시보드 (프론트엔드)
│   ├── index.html           # 메인 페이지
│   ├── css/
│   │   └── style.css        # 스타일시트
│   └── js/
│       ├── main.js          # 메인 스크립트 (DOM 조작, 이벤트 처리)
│       ├── api.js           # API 요청 유틸리티
│       └── chart.js         # 데이터 시각화 (차트, 그래프)
├── config.xml               # 애플리케이션 설정 파일
├── package.json             # Node.js 의존성 설정
└── README.md                # 프로젝트 설명 문서
```

## 🎯 주요 기능

### 1. **대시보드**
- 실시간 건강 지표 모니터링 (혈압, 맥박, 혈당 등)
- 최근 활동 로그 표시
- 긴급 알림 및 경고

### 2. **노인 사용자 관리**
- 사용자 목록 조회
- 사용자 추가/편집/삭제
- 사용자 검색 및 상세 정보 조회

### 3. **건강 관리**
- 건강 기록 입력
- 건강 지표 그래프 (시간별, 날짜별, 월별)
- 의료 기록 관리
- 처방약 관리
- 알레르기 및 건강 조건 기록

### 4. **일정 관리**
- 약물 복용 일정 설정
- 병원 방문 일정
- 운동/활동 일정
- 일정 알림 설정
- 캘린더 기반 일정 관리

### 5. **설정 관리**
- 사용자 프로필 편집
- 비밀번호 변경
- 알림 설정 (건강 지표, 약물 복용, 비상 연락)
- 시스템 설정 (언어, 테마)
- 데이터 백업

## 🚀 설치 및 실행

### 사전 요구 사항
- Node.js 14.0 이상
- npm 또는 yarn

### 설치 단계

1. **리포지토리 클론**
   ```bash
   git clone https://github.com/yourname/elder-care-app.git
   cd elder-care-app
   ```

2. **의존성 설치**
   ```bash
   npm install
   ```

3. **환경 변수 설정**
   ```bash
   cp .env.example .env
   # .env 파일 수정
   ```

4. **데이터베이스 초기화**
   ```bash
   npm run db:init
   ```

5. **개발 서버 실행**
   ```bash
   npm run dev
   ```

   서버는 `http://localhost:3000`에서 실행됩니다.

6. **웹 인터페이스 접속**
   ```
   http://localhost:3000/web
   ```

## 🛠️ 기술 스택

### 프론트엔드
- **HTML5**: 마크업
- **CSS3**: 반응형 디자인
- **JavaScript (Vanilla)**: DOM 조작, 이벤트 처리
- **Chart.js**: 데이터 시각화 (차트, 그래프)
- **Fetch API**: 비동기 HTTP 요청

### 백엔드 (예정)
- **Node.js**: 런타임
- **Express.js**: 웹 프레임워크
- **SQLite**: 데이터베이스
- **CORS**: 크로스 오리진 요청 처리

### 추가 라이브러리 (예정)
- **FullCalendar**: 캘린더 기능
- **Chart.js**: 차트 라이브러리
- **Nodemon**: 개발 서버 자동 재시작

## 📖 개발 가이드

### 파일 구조 설명

#### `index.html`
메인 페이지로, 5개의 섹션으로 구성:
- **대시보드**: 건강 지표 및 활동 요약
- **노인 사용자 관리**: 사용자 목록 및 관리 기능
- **건강 관리**: 건강 기록 및 시각화
- **일정 관리**: 약물 복용 및 병원 방문 일정
- **설정**: 사용자 및 시스템 설정

#### `css/style.css`
- 반응형 디자인 구현
- 다크 테마 지원
- 접근성 개선 (WCAG 2.1 준수)
- 모바일 최적화
- 애니메이션 및 전환 효과

#### `js/main.js`
DOM 조작 및 이벤트 처리:
- 초기화 및 이벤트 리스너 등록
- 폼 제출 처리
- 실시간 데이터 업데이트
- UI 업데이트 함수

#### `js/api.js`
API 요청 관련 유틸리티:
- REST API 엔드포인트 정의
- 인증 토큰 관리
- 요청/응답 인터셉터
- 에러 처리
- 재시도 로직

#### `js/chart.js`
데이터 시각화:
- 건강 지표 차트 (라인 차트)
- 활동 통계 (막대 차트)
- 약물 복용 현황 (원형 차트)
- 혈압 분포 (히스토그램)
- 시간대별 활동 패턴

#### `config.xml`
애플리케이션 설정:
- 서버 설정 (포트, 호스트)
- 데이터베이스 설정
- 사용자 권한 설정
- 알림 설정
- 외부 API 설정

#### `package.json`
프로젝트 메타데이터 및 의존성:
- 프로젝트 정보
- npm 스크립트
- 의존성 목록

### 개발 워크플로우

1. **기능 추가**
   - HTML에 마크업 추가
   - CSS에 스타일 추가
   - main.js에 이벤트 처리 추가
   - api.js에 API 함수 추가
   - chart.js에 차트 구현 추가

2. **테스트**
   ```bash
   npm run test
   ```

3. **코드 스타일 확인**
   ```bash
   npm run lint
   ```

4. **빌드**
   ```bash
   npm run build
   ```

## 📡 API 문서

### 기본 설정
- **Base URL**: `http://localhost:3000/api`
- **Content-Type**: `application/json`
- **인증**: Bearer Token (Authorization 헤더)

### 주요 엔드포인트

#### 사용자 관리
- `GET /users` - 사용자 목록 조회
- `GET /users/{id}` - 사용자 상세 정보
- `POST /users` - 사용자 추가
- `PUT /users/{id}` - 사용자 편집
- `DELETE /users/{id}` - 사용자 삭제

#### 건강 데이터
- `GET /users/{id}/health` - 건강 기록 조회
- `POST /users/{id}/health` - 건강 기록 추가
- `GET /users/{id}/health/stats` - 건강 지표 통계

#### 약물 관리
- `GET /users/{id}/medicines` - 약물 목록
- `POST /users/{id}/medicines` - 약물 추가
- `POST /users/{id}/medicines/{id}/taken` - 복용 기록

#### 일정 관리
- `GET /users/{id}/schedules` - 일정 목록
- `POST /users/{id}/schedules` - 일정 추가
- `PUT /users/{id}/schedules/{id}` - 일정 편집
- `DELETE /users/{id}/schedules/{id}` - 일정 삭제

#### 인증
- `POST /auth/login` - 로그인
- `POST /auth/logout` - 로그아웃

### 요청/응답 예제

**건강 기록 추가**
```javascript
POST /api/users/1/health
{
    "bloodPressure": {
        "systolic": 120,
        "diastolic": 80
    },
    "pulse": 72,
    "bloodSugar": 100,
    "notes": "좋은 상태입니다"
}

응답:
{
    "id": 1,
    "userId": 1,
    "timestamp": "2024-01-15T10:30:00Z",
    "bloodPressure": {...},
    "pulse": 72,
    "bloodSugar": 100,
    "notes": "좋은 상태입니다"
}
```

## 🔐 보안 고려사항

- [ ] HTTPS 적용
- [ ] 입력 값 검증 및 새니타이제이션
- [ ] CSRF 토큰 구현
- [ ] 권한 검사 (RBAC)
- [ ] 데이터 암호화
- [ ] SQL Injection 방지
- [ ] XSS 방지
- [ ] Rate Limiting 구현
- [ ] 감사 로그 기록

## 🧪 테스트

```bash
# 단위 테스트 실행
npm run test

# 통합 테스트
npm run test:integration

# 커버리지 확인
npm run test:coverage
```

## 📝 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.

## 🤝 기여 방법

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 연락처

- **이메일**: your-email@example.com
- **GitHub Issues**: [Report issues here](https://github.com/yourname/elder-care-app/issues)

## 🗺️ 로드맵

### v1.0 (현재)
- [x] 기본 대시보드
- [x] 건강 데이터 관리
- [x] 일정 관리
- [ ] 웹 인터페이스 완성
- [ ] 모바일 앱 개발

### v1.1 (예정)
- [ ] 리포팅 기능 강화
- [ ] AI 기반 건강 예측
- [ ] 웨어러블 기기 연동
- [ ] 다중 언어 지원

### v2.0 (계획 중)
- [ ] 모바일 네이티브 앱
- [ ] IoT 기기 지원
- [ ] 의료 기관 연동
- [ ] 텔레메디신 기능

---

**마지막 업데이트**: 2024년 1월 15일
