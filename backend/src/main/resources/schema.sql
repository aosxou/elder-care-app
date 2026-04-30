-- ====================================
-- Elder Care Management System - Database Schema
-- PostgreSQL
-- ====================================

-- TODO: 시퀀스 생성 (UUID 대신 사용하려면)
-- CREATE SEQUENCE IF NOT EXISTS elder_id_seq;
-- CREATE SEQUENCE IF NOT EXISTS guardian_id_seq;

-- ====================================
-- 1. ELDERS (어르신 정보)
-- ====================================
CREATE TABLE IF NOT EXISTS elders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 기본 정보
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    date_of_birth DATE,
    gender CHAR(1),  -- 'M', 'F'
    address TEXT,

    -- 건강 정보
    blood_type VARCHAR(5),
    allergies TEXT,  -- JSON 형식으로 저장 가능
    chronic_diseases TEXT,  -- JSON 형식
    current_medications TEXT,  -- JSON 형식

    -- 상태
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, CRITICAL
    health_status VARCHAR(20) DEFAULT 'GOOD',  -- GOOD, CAUTION, WARNING, CRITICAL

    -- 메타데이터
    profile_image_url VARCHAR(500),
    emergency_contact TEXT,  -- JSON 형식
    notes TEXT,

    -- 타임스탬프
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,

    -- 인덱스
    CONSTRAINT elders_email_check CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'),
    CONSTRAINT elders_gender_check CHECK (gender IN ('M', 'F', NULL)),
    CONSTRAINT elders_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'CRITICAL'))
);

CREATE INDEX idx_elders_email ON elders(email);
CREATE INDEX idx_elders_phone ON elders(phone);
CREATE INDEX idx_elders_status ON elders(status);
CREATE INDEX idx_elders_created_at ON elders(created_at);

COMMENT ON TABLE elders IS '노인 사용자 정보';
COMMENT ON COLUMN elders.status IS 'ACTIVE: 활성, INACTIVE: 비활성, CRITICAL: 응급';
COMMENT ON COLUMN elders.health_status IS 'GOOD: 좋음, CAUTION: 주의, WARNING: 경고, CRITICAL: 위험';

-- ====================================
-- 2. GUARDIANS (보호자/간병인 정보)
-- ====================================
CREATE TABLE IF NOT EXISTS guardians (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 기본 정보
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    relationship VARCHAR(50),  -- 자식, 배우자, 친구 등

    -- 권한 및 역할
    role VARCHAR(20) DEFAULT 'GUARDIAN',  -- ADMIN, GUARDIAN, CAREGIVER
    permissions TEXT,  -- JSON 형식

    -- 상태
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, SUSPENDED
    verification_status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, VERIFIED, REJECTED

    -- 알림 설정
    health_alert_enabled BOOLEAN DEFAULT TRUE,
    medicine_reminder_enabled BOOLEAN DEFAULT TRUE,
    emergency_alert_enabled BOOLEAN DEFAULT TRUE,
    notification_channels TEXT DEFAULT 'EMAIL,SMS,PUSH',  -- JSON 또는 쉼표 구분

    -- 주소 정보
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),

    -- 메타데이터
    profile_image_url VARCHAR(500),
    notes TEXT,
    verification_document_url VARCHAR(500),

    -- 타임스탬프
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,

    -- 제약 조건
    CONSTRAINT guardians_email_check CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'),
    CONSTRAINT guardians_role_check CHECK (role IN ('ADMIN', 'GUARDIAN', 'CAREGIVER')),
    CONSTRAINT guardians_status_check CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_guardians_email ON guardians(email);
CREATE INDEX idx_guardians_phone ON guardians(phone);
CREATE INDEX idx_guardians_role ON guardians(role);
CREATE INDEX idx_guardians_status ON guardians(status);
CREATE INDEX idx_guardians_verification_status ON guardians(verification_status);

COMMENT ON TABLE guardians IS '보호자/간병인 정보';
COMMENT ON COLUMN guardians.role IS 'ADMIN: 관리자, GUARDIAN: 보호자, CAREGIVER: 간병인';

-- ====================================
-- 3. GUARDIAN_ELDERLY (다대다 관계)
-- ====================================
CREATE TABLE IF NOT EXISTS guardian_elderly (
    guardian_id UUID NOT NULL REFERENCES guardians(id) ON DELETE CASCADE,
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,

    -- 관계 정보
    relationship VARCHAR(50),
    is_primary BOOLEAN DEFAULT FALSE,

    -- 타임스탬프
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (guardian_id, elderly_id),
    CONSTRAINT guardian_elderly_same_check CHECK (guardian_id != elderly_id)
);

CREATE INDEX idx_guardian_elderly_guardian ON guardian_elderly(guardian_id);
CREATE INDEX idx_guardian_elderly_elderly ON guardian_elderly(elderly_id);
CREATE INDEX idx_guardian_elderly_primary ON guardian_elderly(is_primary);

COMMENT ON TABLE guardian_elderly IS '보호자-어르신 관계 (다대다)';

-- ====================================
-- 4. CALLS (WebRTC 통화 기록)
-- ====================================
CREATE TABLE IF NOT EXISTS calls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 통화자 정보
    caller_id UUID NOT NULL REFERENCES elders(id) ON DELETE SET NULL,
    receiver_id UUID NOT NULL REFERENCES elders(id) ON DELETE SET NULL,

    -- 통화 정보
    call_type VARCHAR(20) NOT NULL,  -- VIDEO, AUDIO, SCREENSHARE
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',  -- INITIATED, RINGING, ACCEPTED, ENDED, REJECTED, MISSED, FAILED

    -- 시간 정보
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,

    -- 통화 품질 지표
    connection_quality VARCHAR(20),  -- EXCELLENT, GOOD, FAIR, POOR
    packet_loss DECIMAL(5, 2),  -- 패킷 손실율 (%)
    latency_ms INTEGER,  -- 지연 (밀리초)
    jitter_ms INTEGER,  -- 떨림
    video_bitrate_kbps INTEGER,
    audio_bitrate_kbps INTEGER,

    -- 거절/실패 사유
    rejection_reason VARCHAR(100),

    -- 통화 기록 (선택적)
    recording_url VARCHAR(500),
    transcript TEXT,

    -- 위치 정보
    location_caller VARCHAR(200),
    location_receiver VARCHAR(200),

    -- 디바이스 정보
    device_caller VARCHAR(100),
    device_receiver VARCHAR(100),

    -- 메타데이터
    notes TEXT,

    -- 타임스탬프
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT calls_type_check CHECK (call_type IN ('VIDEO', 'AUDIO', 'SCREENSHARE')),
    CONSTRAINT calls_status_check CHECK (status IN ('INITIATED', 'RINGING', 'ACCEPTED', 'ENDED', 'REJECTED', 'MISSED', 'FAILED')),
    CONSTRAINT calls_different_users CHECK (caller_id != receiver_id)
);

CREATE INDEX idx_calls_caller ON calls(caller_id);
CREATE INDEX idx_calls_receiver ON calls(receiver_id);
CREATE INDEX idx_calls_status ON calls(status);
CREATE INDEX idx_calls_started_at ON calls(started_at);
CREATE INDEX idx_calls_created_at ON calls(created_at);

COMMENT ON TABLE calls IS 'WebRTC 통화 기록';
COMMENT ON COLUMN calls.status IS '통화 상태 추적';

-- ====================================
-- 5. CONVERSATIONS (대화/메시지)
-- ====================================
CREATE TABLE IF NOT EXISTS conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 참여자 정보
    participant_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,
    target_id UUID,  -- NULL이면 AI와의 대화, 아니면 사용자 ID

    -- 대화 메타데이터
    conversation_type VARCHAR(30) DEFAULT 'USER_TO_AI',  -- USER_TO_USER, USER_TO_AI, GROUP
    title VARCHAR(200),
    description TEXT,
    topic VARCHAR(100),

    -- 상태 정보
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, PAUSED, ENDED, ARCHIVED
    is_archived BOOLEAN DEFAULT FALSE,

    -- 메시지 관련
    message_count INTEGER DEFAULT 0,
    last_message_at TIMESTAMP,
    last_message_preview TEXT,

    -- 시간 정보
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    duration_minutes INTEGER,

    -- 분석 정보
    overall_sentiment VARCHAR(20),  -- POSITIVE, NEUTRAL, NEGATIVE
    sentiment_score DECIMAL(3, 2),  -- -1.0 ~ 1.0
    key_topics TEXT,  -- JSON: 추출된 주요 주제
    summary TEXT,  -- AI 생성 요약

    -- 설정
    is_encrypted BOOLEAN DEFAULT FALSE,
    is_private BOOLEAN DEFAULT TRUE,
    allow_search BOOLEAN DEFAULT TRUE,
    muted BOOLEAN DEFAULT FALSE,
    pinned BOOLEAN DEFAULT FALSE,

    -- 참여자 (그룹 대화의 경우)
    participants TEXT,  -- JSON: 모든 참여자 ID

    -- 메타데이터
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,  -- 소프트 삭제

    -- 제약 조건
    CONSTRAINT conversations_type_check CHECK (conversation_type IN ('USER_TO_USER', 'USER_TO_AI', 'GROUP')),
    CONSTRAINT conversations_status_check CHECK (status IN ('ACTIVE', 'PAUSED', 'ENDED', 'ARCHIVED'))
);

CREATE INDEX idx_conversations_participant ON conversations(participant_id);
CREATE INDEX idx_conversations_target ON conversations(target_id);
CREATE INDEX idx_conversations_status ON conversations(status);
CREATE INDEX idx_conversations_started_at ON conversations(started_at);
CREATE INDEX idx_conversations_sentiment ON conversations(overall_sentiment);
CREATE INDEX idx_conversations_archived ON conversations(is_archived) WHERE NOT is_archived;

COMMENT ON TABLE conversations IS '사용자 간 및 AI와의 대화 기록';

-- ====================================
-- 6. MESSAGES (대화 메시지)
-- ====================================
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,

    -- 메시지 정보
    sender_id UUID NOT NULL,  -- elderly_id 또는 guardian_id 또는 'AI_ASSISTANT'
    sender_type VARCHAR(20) NOT NULL,  -- ELDERLY, GUARDIAN, AI
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',  -- TEXT, IMAGE, AUDIO, VIDEO

    -- 메타데이터
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,

    -- 감정 분석
    sentiment VARCHAR(20),  -- POSITIVE, NEUTRAL, NEGATIVE
    sentiment_score DECIMAL(3, 2),

    -- 타임스탬프
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT messages_type_check CHECK (sender_type IN ('ELDERLY', 'GUARDIAN', 'AI')),
    CONSTRAINT messages_message_type_check CHECK (message_type IN ('TEXT', 'IMAGE', 'AUDIO', 'VIDEO'))
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);
CREATE INDEX idx_messages_is_read ON messages(is_read);

COMMENT ON TABLE messages IS '대화 메시지';

-- ====================================
-- 7. EMOTION_ANALYSES (감정 분석)
-- ====================================
CREATE TABLE IF NOT EXISTS emotion_analyses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,
    conversation_id UUID REFERENCES conversations(id) ON DELETE SET NULL,

    -- 감정 분석 결과
    overall_sentiment VARCHAR(20) NOT NULL,  -- POSITIVE, NEUTRAL, NEGATIVE
    sentiment_score DECIMAL(3, 2) NOT NULL,  -- -1.0 ~ 1.0
    confidence DECIMAL(3, 2),  -- 신뢰도

    -- 상세 감정 분석
    emotion_joy DECIMAL(3, 2) DEFAULT 0,
    emotion_sadness DECIMAL(3, 2) DEFAULT 0,
    emotion_anger DECIMAL(3, 2) DEFAULT 0,
    emotion_fear DECIMAL(3, 2) DEFAULT 0,
    emotion_surprise DECIMAL(3, 2) DEFAULT 0,
    emotion_disgust DECIMAL(3, 2) DEFAULT 0,

    -- 키워드 및 주제
    keywords TEXT,  -- JSON: ["키워드1", "키워드2"]
    topics TEXT,  -- JSON: ["주제1", "주제2"]

    -- 분석 기반
    analysis_type VARCHAR(30),  -- TEXT, VOICE, BEHAVIOR, CONVERSATION
    source_data TEXT,

    -- 추천
    recommendation TEXT,

    -- 타임스탬프
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT emotion_analyses_sentiment_check CHECK (overall_sentiment IN ('POSITIVE', 'NEUTRAL', 'NEGATIVE')),
    CONSTRAINT emotion_analyses_score_check CHECK (sentiment_score >= -1.0 AND sentiment_score <= 1.0)
);

CREATE INDEX idx_emotion_analyses_elderly ON emotion_analyses(elderly_id);
CREATE INDEX idx_emotion_analyses_conversation ON emotion_analyses(conversation_id);
CREATE INDEX idx_emotion_analyses_sentiment ON emotion_analyses(overall_sentiment);
CREATE INDEX idx_emotion_analyses_analyzed_at ON emotion_analyses(analyzed_at);

COMMENT ON TABLE emotion_analyses IS '감정 분석 결과';
COMMENT ON COLUMN emotion_analyses.sentiment_score IS '-1.0(부정적) ~ 1.0(긍정적)';

-- ====================================
-- 8. HEALTH_RECORDS (건강 기록)
-- ====================================
CREATE TABLE IF NOT EXISTS health_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,

    -- 건강 지표
    blood_pressure VARCHAR(20),  -- "120/80" 형식
    systolic INTEGER,
    diastolic INTEGER,
    pulse INTEGER,
    blood_sugar INTEGER,
    temperature DECIMAL(4, 1),
    oxygen_saturation INTEGER,  -- SpO2 %
    weight DECIMAL(5, 1),

    -- 상태 판정
    is_alert BOOLEAN DEFAULT FALSE,
    alert_reason VARCHAR(200),
    risk_level VARCHAR(20),  -- LOW, MEDIUM, HIGH, CRITICAL

    -- 추가 정보
    notes TEXT,

    -- 타임스탐프
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT health_records_risk_check CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

CREATE INDEX idx_health_records_elderly ON health_records(elderly_id);
CREATE INDEX idx_health_records_recorded_at ON health_records(recorded_at);
CREATE INDEX idx_health_records_is_alert ON health_records(is_alert);

COMMENT ON TABLE health_records IS '건강 지표 기록';

-- ====================================
-- 9. MEDICINES (약물 관리)
-- ====================================
CREATE TABLE IF NOT EXISTS medicines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,

    -- 약물 정보
    name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    scheduled_time TIME[],  -- 복용 시간 배열
    start_date DATE,
    end_date DATE,

    -- 상태
    status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, PAUSED, COMPLETED

    -- 복용 기록
    taken_count INTEGER DEFAULT 0,
    total_count INTEGER,
    compliance_rate DECIMAL(5, 2),  -- 순응도 %

    -- 메타데이터
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT medicines_status_check CHECK (status IN ('ACTIVE', 'PAUSED', 'COMPLETED'))
);

CREATE INDEX idx_medicines_elderly ON medicines(elderly_id);
CREATE INDEX idx_medicines_status ON medicines(status);
CREATE INDEX idx_medicines_scheduled_time ON medicines(scheduled_time);

COMMENT ON TABLE medicines IS '약물 관리';

-- ====================================
-- 10. MEDICINE_RECORDS (약물 복용 기록)
-- ====================================
CREATE TABLE IF NOT EXISTS medicine_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    medicine_id UUID NOT NULL REFERENCES medicines(id) ON DELETE CASCADE,
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,

    -- 복용 정보
    taken BOOLEAN DEFAULT FALSE,
    taken_time TIMESTAMP,
    scheduled_time TIME,

    -- 기록
    recorded_by VARCHAR(50),  -- ELDERLY, GUARDIAN, SYSTEM
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE INDEX idx_medicine_records_medicine ON medicine_records(medicine_id);
CREATE INDEX idx_medicine_records_elderly ON medicine_records(elderly_id);
CREATE INDEX idx_medicine_records_taken ON medicine_records(taken);
CREATE INDEX idx_medicine_records_recorded_at ON medicine_records(recorded_at);

COMMENT ON TABLE medicine_records IS '약물 복용 기록';

-- ====================================
-- 11. ACTIVITIES (활동 기록)
-- ====================================
CREATE TABLE IF NOT EXISTS activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 관계
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,

    -- 활동 정보
    activity_type VARCHAR(50),  -- 보행, 운동, 휴식 등
    distance DECIMAL(6, 2),  -- km
    duration INTEGER,  -- 분
    calories_burned INTEGER,
    steps INTEGER,

    -- 시간 정보
    activity_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,

    -- 강도
    intensity VARCHAR(20),  -- LOW, MEDIUM, HIGH

    -- 메타데이터
    location VARCHAR(200),
    notes TEXT,

    -- 타임스탬프
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 제약 조건
    CONSTRAINT activities_intensity_check CHECK (intensity IN ('LOW', 'MEDIUM', 'HIGH', NULL))
);

CREATE INDEX idx_activities_elderly ON activities(elderly_id);
CREATE INDEX idx_activities_activity_date ON activities(activity_date);
CREATE INDEX idx_activities_recorded_at ON activities(recorded_at);

COMMENT ON TABLE activities IS '활동 기록';

-- ====================================
-- 12. ALERTS (알림 기록)
-- ====================================
CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 대상
    elderly_id UUID NOT NULL REFERENCES elders(id) ON DELETE CASCADE,
    guardian_id UUID REFERENCES guardians(id) ON DELETE SET NULL,

    -- 알림 정보
    alert_type VARCHAR(50) NOT NULL,  -- HEALTH_WARNING, MEDICINE_REMINDER, EMERGENCY, etc
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,

    -- 심각도
    severity VARCHAR(20) NOT NULL,  -- LOW, MEDIUM, HIGH, CRITICAL

    -- 상태
    status VARCHAR(20) DEFAULT 'UNREAD',  -- UNREAD, READ, ACKNOWLEDGED, RESOLVED

    -- 채널
    notification_channels TEXT,  -- JSON: ["EMAIL", "SMS", "PUSH"]
    sent_channels TEXT,  -- JSON: 실제 전송된 채널

    -- 시간
    triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    acknowledged_at TIMESTAMP,
    resolved_at TIMESTAMP,

    -- 메타데이터
    related_data TEXT,  -- JSON: 관련 데이터
    notes TEXT,

    -- 제약 조건
    CONSTRAINT alerts_severity_check CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT alerts_status_check CHECK (status IN ('UNREAD', 'READ', 'ACKNOWLEDGED', 'RESOLVED'))
);

CREATE INDEX idx_alerts_elderly ON alerts(elderly_id);
CREATE INDEX idx_alerts_guardian ON alerts(guardian_id);
CREATE INDEX idx_alerts_type ON alerts(alert_type);
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_triggered_at ON alerts(triggered_at);

COMMENT ON TABLE alerts IS '알림 기록';
COMMENT ON COLUMN alerts.alert_type IS '알림 유형: 건강 경고, 약물 복용, 긴급 등';

-- ====================================
-- 13. NOTIFICATIONS (사용자 알림 설정)
-- ====================================
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- 대상
    user_id UUID NOT NULL,
    user_type VARCHAR(20) NOT NULL,  -- ELDERLY, GUARDIAN

    -- 알림 설정
    health_alert_enabled BOOLEAN DEFAULT TRUE,
    medicine_reminder_enabled BOOLEAN DEFAULT TRUE,
    emergency_alert_enabled BOOLEAN DEFAULT TRUE,
    activity_update_enabled BOOLEAN DEFAULT FALSE,

    -- 채널별 활성화
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT TRUE,
    push_enabled BOOLEAN DEFAULT TRUE,

    -- 시간대 설정
    quiet_hours_start TIME,  -- 조용한 시간 시작
    quiet_hours_end TIME,

    -- 메타데이터
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id, user_type);

COMMENT ON TABLE notifications IS '사용자별 알림 설정';

-- ====================================
-- 함수: updated_at 자동 업데이트
-- ====================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 생성
CREATE TRIGGER update_elders_updated_at BEFORE UPDATE ON elders
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_guardians_updated_at BEFORE UPDATE ON guardians
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_calls_updated_at BEFORE UPDATE ON calls
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_conversations_updated_at BEFORE UPDATE ON conversations
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_medicines_updated_at BEFORE UPDATE ON medicines
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notifications_updated_at BEFORE UPDATE ON notifications
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ====================================
-- 뷰: 대시보드 요약
-- ====================================

/**
 * 어르신별 건강 현황 요약
 */
CREATE OR REPLACE VIEW vw_elderly_health_summary AS
SELECT
    e.id,
    e.name,
    e.email,
    e.health_status,
    (SELECT COUNT(*) FROM health_records WHERE elderly_id = e.id AND is_alert = TRUE) AS alert_count,
    (SELECT COUNT(*) FROM medicines WHERE elderly_id = e.id AND status = 'ACTIVE') AS active_medicines,
    (SELECT COUNT(*) FROM activities WHERE elderly_id = e.id AND activity_date = CURRENT_DATE) AS today_activities,
    (SELECT MAX(recorded_at) FROM health_records WHERE elderly_id = e.id) AS last_health_check,
    e.updated_at
FROM elders e
WHERE e.status = 'ACTIVE'
ORDER BY e.updated_at DESC;

COMMENT ON VIEW vw_elderly_health_summary IS '어르신별 건강 현황 요약';

/**
 * 보호자별 관리 현황 요약
 */
CREATE OR REPLACE VIEW vw_guardian_management_summary AS
SELECT
    g.id,
    g.name,
    g.email,
    g.role,
    (SELECT COUNT(*) FROM guardian_elderly WHERE guardian_id = g.id) AS managed_elderly_count,
    (SELECT COUNT(*) FROM alerts WHERE guardian_id = g.id AND status = 'UNREAD') AS unread_alerts,
    (SELECT COUNT(*) FROM calls WHERE (caller_id IN (SELECT elderly_id FROM guardian_elderly WHERE guardian_id = g.id) OR receiver_id IN (SELECT elderly_id FROM guardian_elderly WHERE guardian_id = g.id)) AND DATE(started_at) = CURRENT_DATE) AS today_calls,
    g.last_login,
    g.updated_at
FROM guardians g
WHERE g.status = 'ACTIVE'
ORDER BY g.updated_at DESC;

COMMENT ON VIEW vw_guardian_management_summary IS '보호자별 관리 현황 요약';

-- ====================================
-- 초기 데이터 (선택사항)
-- ====================================

-- TODO: 테스트 데이터 추가
-- INSERT INTO elders (name, email, password, phone, date_of_birth, gender, health_status)
-- VALUES ('홍길동', 'hong@example.com', 'hashed_password', '010-1234-5678', '1945-01-01', 'M', 'GOOD');
--
-- INSERT INTO guardians (name, email, password, phone, role)
-- VALUES ('홍은서', 'hongs@example.com', 'hashed_password', '010-9876-5432', 'GUARDIAN');

-- ====================================
-- 마이그레이션 메타테이블 (선택사항)
-- ====================================

CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INTEGER,
    version VARCHAR(50),
    description VARCHAR(255),
    type VARCHAR(20),
    script VARCHAR(1000),
    checksum INTEGER,
    installed_by VARCHAR(100),
    installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    execution_time INTEGER,
    success BOOLEAN
);

-- ====================================
-- 권한 관리 (선택사항)
-- ====================================

-- TODO: 데이터베이스 사용자 생성 및 권한 설정
-- CREATE ROLE elder_care_app WITH PASSWORD 'secure_password';
-- GRANT CONNECT ON DATABASE eldercare_db TO elder_care_app;
-- GRANT USAGE ON SCHEMA public TO elder_care_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO elder_care_app;
-- GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO elder_care_app;

-- ====================================
-- 스키마 생성 완료
-- ====================================

COMMIT;
