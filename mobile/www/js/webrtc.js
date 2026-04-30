/**
 * WebRTC 통화 관리
 *
 * Cordova를 통한 음성/영상 통화
 *
 * TODO: SDP/ICE 교환
 * TODO: 통화 상태 관리
 * TODO: 오디오/비디오 제어
 * TODO: 통화 기록
 */

const WebRTC = {
    peerConnection: null,
    localStream: null,
    remoteStream: null,
    currentCall: null,
    callType: 'audio',  // audio 또는 video
    iceServers: [
        { urls: ['stun:stun.l.google.com:19302'] },
        { urls: ['stun:stun1.l.google.com:19302'] }
    ],

    /**
     * 통화 시작
     *
     * TODO: 상대방 선택 UI
     * TODO: 통화 타입 설정 (음성/영상)
     */
    initCall: function (type = 'audio') {
        this.callType = type;

        // TODO: 상대방 목록에서 선택
        const recipientId = prompt('수신자 ID를 입력하세요:');
        if (!recipientId) return;

        console.log(`${type} 통화 시작: ${recipientId}`);

        this.startCall(recipientId, type);
    },

    /**
     * 통화 시작 (내부 함수)
     */
    startCall: async function (recipientId, type = 'audio') {
        try {
            showLoading(true);

            // 로컬 미디어 스트림 획득
            const constraints = {
                audio: true,
                video: type === 'video' ? {
                    width: { ideal: 1280 },
                    height: { ideal: 720 }
                } : false
            };

            this.localStream = await navigator.mediaDevices.getUserMedia(constraints);
            console.log('로컬 미디어 스트림 획득');

            // PeerConnection 생성
            this.createPeerConnection();

            // 로컬 스트림 추가
            this.localStream.getTracks().forEach(track => {
                this.peerConnection.addTrack(track, this.localStream);
            });

            // SDP 오퍼 생성
            const offer = await this.peerConnection.createOffer();
            await this.peerConnection.setLocalDescription(offer);

            // 서버에 통화 요청 전송
            await API.initiateCall({
                recipientId: recipientId,
                type: type,
                sdpOffer: offer.sdp
            });

            this.currentCall = {
                recipientId: recipientId,
                type: type,
                startTime: new Date(),
                status: 'calling'
            };

            // TODO: 통화 UI 표시
            showCallUI();

            showToast('통화 중입니다...');
        } catch (error) {
            console.error('통화 시작 실패:', error);
            showToast('통화 시작에 실패했습니다');
            showLoading(false);
        }
    },

    /**
     * 통화 수락
     */
    answerCall: async function (callId) {
        try {
            showLoading(true);

            // 로컬 미디어 스트림 획득
            const constraints = {
                audio: true,
                video: this.callType === 'video'
            };

            this.localStream = await navigator.mediaDevices.getUserMedia(constraints);

            // PeerConnection 생성
            this.createPeerConnection();

            // 로컬 스트림 추가
            this.localStream.getTracks().forEach(track => {
                this.peerConnection.addTrack(track, this.localStream);
            });

            // SDP 응답 생성
            const answer = await this.peerConnection.createAnswer();
            await this.peerConnection.setLocalDescription(answer);

            // 서버에 수락 전송
            await API.answerCall({
                callId: callId,
                sdpAnswer: answer.sdp
            });

            this.currentCall = {
                callId: callId,
                status: 'connected'
            };

            showCallUI();
            showToast('통화가 연결되었습니다');
        } catch (error) {
            console.error('통화 수락 실패:', error);
            showToast('통화 수락에 실패했습니다');
        } finally {
            showLoading(false);
        }
    },

    /**
     * 통화 거절
     */
    rejectCall: async function (callId) {
        try {
            await API.rejectCall(callId);
            console.log('통화 거절');
        } catch (error) {
            console.error('통화 거절 실패:', error);
        }
    },

    /**
     * 통화 종료
     */
    endCall: async function () {
        try {
            // 미디어 스트림 중단
            if (this.localStream) {
                this.localStream.getTracks().forEach(track => {
                    track.stop();
                });
            }

            // PeerConnection 종료
            if (this.peerConnection) {
                this.peerConnection.close();
                this.peerConnection = null;
            }

            // 서버에 통화 종료 전송
            if (this.currentCall) {
                const duration = Math.floor(
                    (new Date() - this.currentCall.startTime) / 1000
                );

                await API.endCall({
                    callId: this.currentCall.callId,
                    duration: duration
                });
            }

            // UI 숨기기
            hideCallUI();

            showToast('통화가 종료되었습니다');
            this.currentCall = null;
        } catch (error) {
            console.error('통화 종료 실패:', error);
        }
    },

    /**
     * PeerConnection 생성
     */
    createPeerConnection: function () {
        const config = {
            iceServers: this.iceServers
        };

        this.peerConnection = new RTCPeerConnection(config);

        const self = this;

        // ICE 후보자 이벤트
        this.peerConnection.onicecandidate = function (event) {
            if (event.candidate) {
                // 서버에 ICE 후보자 전송
                WebSocket.send('/app/webrtc/ice-candidate', {
                    candidate: event.candidate,
                    callId: self.currentCall.callId
                });
            }
        };

        // 원격 스트림 이벤트
        this.peerConnection.ontrack = function (event) {
            console.log('원격 스트림 수신');
            self.remoteStream = event.streams[0];

            // TODO: 원격 비디오 표시
            if (self.callType === 'video') {
                displayRemoteVideo(self.remoteStream);
            }
        };

        // 연결 상태 변화
        this.peerConnection.onconnectionstatechange = function () {
            console.log('연결 상태:', self.peerConnection.connectionState);

            switch (self.peerConnection.connectionState) {
                case 'connected':
                    console.log('통화 연결됨');
                    showToast('통화가 연결되었습니다');
                    break;
                case 'disconnected':
                    console.log('통화 연결 끊김');
                    showToast('통화가 끊겼습니다');
                    break;
                case 'failed':
                    console.error('통화 연결 실패');
                    showToast('통화 연결에 실패했습니다');
                    self.endCall();
                    break;
                case 'closed':
                    console.log('통화 종료');
                    break;
            }
        };

        console.log('PeerConnection 생성됨');
    },

    /**
     * SDP 오퍼 수신
     */
    receiveSdpOffer: async function (sdpOffer) {
        try {
            const offer = new RTCSessionDescription({
                type: 'offer',
                sdp: sdpOffer
            });

            if (!this.peerConnection) {
                this.createPeerConnection();
            }

            await this.peerConnection.setRemoteDescription(offer);
            console.log('SDP 오퍼 설정됨');
        } catch (error) {
            console.error('SDP 오퍼 설정 실패:', error);
        }
    },

    /**
     * SDP 응답 수신
     */
    receiveSdpAnswer: async function (sdpAnswer) {
        try {
            const answer = new RTCSessionDescription({
                type: 'answer',
                sdp: sdpAnswer
            });

            await this.peerConnection.setRemoteDescription(answer);
            console.log('SDP 응답 설정됨');
        } catch (error) {
            console.error('SDP 응답 설정 실패:', error);
        }
    },

    /**
     * ICE 후보자 추가
     */
    addIceCandidate: async function (candidate) {
        try {
            if (this.peerConnection && candidate) {
                await this.peerConnection.addIceCandidate(
                    new RTCIceCandidate(candidate)
                );
                console.log('ICE 후보자 추가됨');
            }
        } catch (error) {
            console.error('ICE 후보자 추가 실패:', error);
        }
    },

    /**
     * 마이크 음소거
     */
    toggleMute: function (mute = null) {
        if (!this.localStream) return;

        const audioTracks = this.localStream.getAudioTracks();
        if (audioTracks.length > 0) {
            const isMuted = audioTracks[0].enabled === false;

            if (mute === null) {
                // 토글
                audioTracks.forEach(track => track.enabled = isMuted);
            } else {
                // 명시적 설정
                audioTracks.forEach(track => track.enabled = !mute);
            }

            console.log('마이크 ' + (audioTracks[0].enabled ? '활성' : '비활성'));
        }
    },

    /**
     * 카메라 끄기
     */
    toggleVideo: function (enabled = null) {
        if (!this.localStream) return;

        const videoTracks = this.localStream.getVideoTracks();
        if (videoTracks.length > 0) {
            const isEnabled = videoTracks[0].enabled;

            if (enabled === null) {
                // 토글
                videoTracks.forEach(track => track.enabled = !isEnabled);
            } else {
                // 명시적 설정
                videoTracks.forEach(track => track.enabled = enabled);
            }

            console.log('카메라 ' + (videoTracks[0].enabled ? '활성' : '비활성'));
        }
    },

    /**
     * 통화 통계 가져오기
     */
    getCallStats: async function () {
        if (!this.peerConnection) return null;

        try {
            const stats = await this.peerConnection.getStats();
            const callStats = {
                audio: {},
                video: {}
            };

            stats.forEach(report => {
                if (report.type === 'inbound-rtp') {
                    const mediaType = report.kind;
                    callStats[mediaType].bytesReceived = report.bytesReceived;
                    callStats[mediaType].packetsLost = report.packetsLost;
                    callStats[mediaType].jitter = report.jitter;
                }
            });

            console.log('통화 통계:', callStats);
            return callStats;
        } catch (error) {
            console.error('통화 통계 조회 실패:', error);
            return null;
        }
    }
};

/**
 * 원격 비디오 표시
 */
function displayRemoteVideo(stream) {
    // TODO: 비디오 요소에 원격 스트림 연결
    const videoElement = document.getElementById('remoteVideo');
    if (videoElement) {
        videoElement.srcObject = stream;
    }
}

/**
 * 통화 UI 표시
 */
function showCallUI() {
    // TODO: 통화 화면 표시
    // TODO: 통화 컨트롤 (종료, 음소거 등) 표시
}

/**
 * 통화 UI 숨기기
 */
function hideCallUI() {
    // TODO: 통화 화면 숨기기
    // TODO: 통화 컨트롤 숨기기
}

// WebSocket을 통한 SDP 메시지 수신
document.addEventListener('deviceready', function () {
    // TODO: WebSocket 구독에서 SDP 메시지 처리
}, false);

console.log('webrtc.js 로드 완료');
