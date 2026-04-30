/**
 * WebRTC 통화 관리 모듈
 *
 * WebSocket을 통한 WebRTC 시그널링
 * - Offer/Answer 교환
 * - ICE Candidate 교환
 * - 통화 상태 관리
 */

const WebRTC = {
	// WebSocket 연결
	ws: null,
	wsUrl: 'ws://localhost:8080/webrtc',

	// 현재 사용자 ID
	currentUserId: null,

	// RTCPeerConnection
	peerConnections: new Map(),  // Key: userId, Value: RTCPeerConnection

	// 로컬 미디어 스트림
	localStream: null,

	// STUN/TURN 서버
	iceServers: [
		{ urls: ['stun:stun.l.google.com:19302'] },
		{ urls: ['stun:stun1.l.google.com:19302'] },
		// TURN 서버 설정 (선택사항)
		// {
		//   urls: ['turn:your-turn-server.com:3478'],
		//   username: 'username',
		//   credential: 'password'
		// }
	],

	// 온라인 사용자 목록
	onlineUsers: [],

	/**
	 * 초기화
	 */
	init: function (userId, callbacks = {}) {
		this.currentUserId = userId;
		this.callbacks = callbacks;

		console.log('WebRTC 모듈 초기화: ' + userId);

		// WebSocket 연결
		this.connectWebSocket();

		// 로컬 미디어 스트림 획득
		this.getLocalStream();
	},

	/**
	 * WebSocket 연결
	 */
	connectWebSocket: function () {
		try {
			this.ws = new WebSocket(this.wsUrl);

			this.ws.onopen = (event) => {
				console.log('WebSocket 연결 성공');

				// 사용자 등록
				const registerMsg = {
					type: 'register',
					from: this.currentUserId
				};
				this.ws.send(JSON.stringify(registerMsg));
			};

			this.ws.onmessage = (event) => {
				const message = JSON.parse(event.data);
				this.handleMessage(message);
			};

			this.ws.onerror = (error) => {
				console.error('WebSocket 오류', error);
				if (this.callbacks.onError) {
					this.callbacks.onError('WebSocket connection error');
				}
			};

			this.ws.onclose = () => {
				console.log('WebSocket 연결 해제');
				// 자동 재연결 (3초 후)
				setTimeout(() => this.connectWebSocket(), 3000);
			};
		} catch (error) {
			console.error('WebSocket 연결 실패', error);
		}
	},

	/**
	 * 메시지 처리
	 */
	handleMessage: function (message) {
		const { type, from, to } = message;

		console.log('메시지 수신:', type, 'from:', from);

		switch (type) {
			case 'registered':
				console.log('클라이언트 등록 완료');
				if (this.callbacks.onRegistered) {
					this.callbacks.onRegistered(message);
				}
				break;

			case 'users-online':
				this.onlineUsers = message.users;
				console.log('온라인 사용자:', this.onlineUsers);
				if (this.callbacks.onUsersUpdate) {
					this.callbacks.onUsersUpdate(this.onlineUsers);
				}
				break;

			case 'offer':
				this.handleOffer(message);
				break;

			case 'answer':
				this.handleAnswer(message);
				break;

			case 'ice-candidate':
				this.handleIceCandidate(message);
				break;

			case 'hangup':
				this.handleHangup(message);
				break;

			case 'error':
				console.error('서버 오류:', message.message);
				if (this.callbacks.onError) {
					this.callbacks.onError(message.message);
				}
				break;

			default:
				console.warn('알 수 없는 메시지 타입:', type);
		}
	},

	/**
	 * 로컬 미디어 스트림 획득
	 */
	getLocalStream: function () {
		navigator.mediaDevices.getUserMedia({
			audio: true,
			video: {
				width: { ideal: 1280 },
				height: { ideal: 720 }
			}
		})
		.then((stream) => {
			this.localStream = stream;
			console.log('로컬 미디어 스트림 획득 성공');

			// 로컬 비디오 표시
			const localVideo = document.getElementById('localVideo');
			if (localVideo) {
				localVideo.srcObject = stream;
			}

			if (this.callbacks.onLocalStreamReady) {
				this.callbacks.onLocalStreamReady(stream);
			}
		})
		.catch((error) => {
			console.error('미디어 스트림 획득 실패', error);
			if (this.callbacks.onError) {
				this.callbacks.onError('Failed to get media stream: ' + error.message);
			}
		});
	},

	/**
	 * 통화 시작 (Offer 전송)
	 */
	startCall: function (recipientId) {
		console.log('통화 시작:', recipientId);

		// PeerConnection 생성
		const peerConnection = this.createPeerConnection(recipientId);

		// 로컬 스트림 추가
		if (this.localStream) {
			this.localStream.getTracks().forEach((track) => {
				peerConnection.addTrack(track, this.localStream);
			});
		}

		// Offer 생성
		peerConnection.createOffer()
			.then((offer) => {
				console.log('Offer 생성 완료');
				return peerConnection.setLocalDescription(offer);
			})
			.then(() => {
				// Offer 전송
				const offerMsg = {
					type: 'offer',
					from: this.currentUserId,
					to: recipientId,
					sdp: peerConnection.localDescription.sdp,
					callId: this.generateCallId()
				};

				this.ws.send(JSON.stringify(offerMsg));
				console.log('Offer 전송');

				if (this.callbacks.onCallStarted) {
					this.callbacks.onCallStarted(recipientId);
				}
			})
			.catch((error) => {
				console.error('Offer 생성/전송 실패', error);
				if (this.callbacks.onError) {
					this.callbacks.onError('Failed to create offer: ' + error.message);
				}
			});
	},

	/**
	 * Offer 처리
	 */
	handleOffer: function (message) {
		const { from, to, sdp, callId } = message;

		console.log('Offer 수신:', from);

		// PeerConnection 생성
		const peerConnection = this.createPeerConnection(from);

		// 로컬 스트림 추가
		if (this.localStream) {
			this.localStream.getTracks().forEach((track) => {
				peerConnection.addTrack(track, this.localStream);
			});
		}

		// Remote Description 설정
		peerConnection.setRemoteDescription(new RTCSessionDescription({
			type: 'offer',
			sdp: sdp
		}))
		.then(() => {
			console.log('Remote Offer 설정 완료');
			return peerConnection.createAnswer();
		})
		.then((answer) => {
			console.log('Answer 생성 완료');
			return peerConnection.setLocalDescription(answer);
		})
		.then(() => {
			// Answer 전송
			const answerMsg = {
				type: 'answer',
				from: this.currentUserId,
				to: from,
				sdp: peerConnection.localDescription.sdp,
				callId: callId
			};

			this.ws.send(JSON.stringify(answerMsg));
			console.log('Answer 전송');

			if (this.callbacks.onIncomingCall) {
				this.callbacks.onIncomingCall(from);
			}
		})
		.catch((error) => {
			console.error('Answer 생성/전송 실패', error);
			if (this.callbacks.onError) {
				this.callbacks.onError('Failed to create answer: ' + error.message);
			}
		});
	},

	/**
	 * Answer 처리
	 */
	handleAnswer: function (message) {
		const { from, sdp } = message;

		console.log('Answer 수신:', from);

		const peerConnection = this.peerConnections.get(from);
		if (!peerConnection) {
			console.error('PeerConnection을 찾을 수 없음:', from);
			return;
		}

		peerConnection.setRemoteDescription(new RTCSessionDescription({
			type: 'answer',
			sdp: sdp
		}))
		.then(() => {
			console.log('Remote Answer 설정 완료');

			if (this.callbacks.onCallConnected) {
				this.callbacks.onCallConnected(from);
			}
		})
		.catch((error) => {
			console.error('Answer 설정 실패', error);
		});
	},

	/**
	 * ICE Candidate 처리
	 */
	handleIceCandidate: function (message) {
		const { from, candidate, sdpMLineIndex, sdpMid } = message;

		const peerConnection = this.peerConnections.get(from);
		if (!peerConnection) {
			console.error('PeerConnection을 찾을 수 없음:', from);
			return;
		}

		const iceCandidate = new RTCIceCandidate({
			candidate: candidate,
			sdpMLineIndex: sdpMLineIndex,
			sdpMid: sdpMid
		});

		peerConnection.addIceCandidate(iceCandidate)
			.then(() => {
				console.log('ICE Candidate 추가 완료');
			})
			.catch((error) => {
				console.error('ICE Candidate 추가 실패', error);
			});
	},

	/**
	 * 통화 종료
	 */
	endCall: function (recipientId) {
		console.log('통화 종료:', recipientId);

		// Hangup 메시지 전송
		const hangupMsg = {
			type: 'hangup',
			from: this.currentUserId,
			to: recipientId,
			callId: this.currentCallId
		};

		this.ws.send(JSON.stringify(hangupMsg));

		// PeerConnection 정리
		this.closePeerConnection(recipientId);

		if (this.callbacks.onCallEnded) {
			this.callbacks.onCallEnded(recipientId);
		}
	},

	/**
	 * Hangup 처리
	 */
	handleHangup: function (message) {
		const { from } = message;

		console.log('Hangup 수신:', from);

		this.closePeerConnection(from);

		if (this.callbacks.onRemoteHangup) {
			this.callbacks.onRemoteHangup(from);
		}
	},

	/**
	 * PeerConnection 생성
	 */
	createPeerConnection: function (peerId) {
		if (this.peerConnections.has(peerId)) {
			return this.peerConnections.get(peerId);
		}

		const peerConnection = new RTCPeerConnection({
			iceServers: this.iceServers
		});

		// ICE Candidate 발생 시
		peerConnection.onicecandidate = (event) => {
			if (event.candidate) {
				const candidateMsg = {
					type: 'ice-candidate',
					from: this.currentUserId,
					to: peerId,
					candidate: event.candidate.candidate,
					sdpMLineIndex: event.candidate.sdpMLineIndex,
					sdpMid: event.candidate.sdpMid,
					callId: this.currentCallId
				};

				this.ws.send(JSON.stringify(candidateMsg));
			}
		};

		// Remote Stream 수신 시
		peerConnection.ontrack = (event) => {
			console.log('원격 트랙 수신:', event.track.kind);

			const remoteVideo = document.getElementById('remoteVideo');
			if (remoteVideo && !remoteVideo.srcObject) {
				remoteVideo.srcObject = event.streams[0];
			}

			if (this.callbacks.onRemoteStreamReady) {
				this.callbacks.onRemoteStreamReady(event.streams[0], peerId);
			}
		};

		// Connection State 변경 시
		peerConnection.onconnectionstatechange = () => {
			console.log('Connection State:', peerConnection.connectionState);

			if (peerConnection.connectionState === 'failed' || peerConnection.connectionState === 'disconnected') {
				this.closePeerConnection(peerId);
				if (this.callbacks.onConnectionFailed) {
					this.callbacks.onConnectionFailed(peerId);
				}
			}
		};

		this.peerConnections.set(peerId, peerConnection);
		console.log('PeerConnection 생성:', peerId);

		return peerConnection;
	},

	/**
	 * PeerConnection 종료
	 */
	closePeerConnection: function (peerId) {
		const peerConnection = this.peerConnections.get(peerId);

		if (peerConnection) {
			peerConnection.close();
			this.peerConnections.delete(peerId);
			console.log('PeerConnection 종료:', peerId);
		}
	},

	/**
	 * 음성 토글
	 */
	toggleAudio: function (enabled) {
		if (this.localStream) {
			this.localStream.getAudioTracks().forEach((track) => {
				track.enabled = enabled;
			});
			console.log('음성 ' + (enabled ? '활성화' : '비활성화'));
		}
	},

	/**
	 * 비디오 토글
	 */
	toggleVideo: function (enabled) {
		if (this.localStream) {
			this.localStream.getVideoTracks().forEach((track) => {
				track.enabled = enabled;
			});
			console.log('비디오 ' + (enabled ? '활성화' : '비활성화'));
		}
	},

	/**
	 * 화면 공유 시작
	 */
	startScreenShare: function (recipientId) {
		navigator.mediaDevices.getDisplayMedia({ video: true, audio: false })
			.then((screenStream) => {
				const screenTrack = screenStream.getVideoTracks()[0];
				const peerConnection = this.peerConnections.get(recipientId);

				if (peerConnection) {
					const sender = peerConnection.getSenders().find((s) => s.track.kind === 'video');

					if (sender) {
						sender.replaceTrack(screenTrack);
						console.log('화면 공유 시작');

						screenTrack.onended = () => {
							console.log('화면 공유 종료');
							// 원래 카메라로 복구
							if (this.localStream) {
								const videoTrack = this.localStream.getVideoTracks()[0];
								sender.replaceTrack(videoTrack);
							}
						};

						if (this.callbacks.onScreenShareStarted) {
							this.callbacks.onScreenShareStarted();
						}
					}
				}
			})
			.catch((error) => {
				console.error('화면 공유 실패', error);
				if (this.callbacks.onError) {
					this.callbacks.onError('Screen share failed: ' + error.message);
				}
			});
	},

	/**
	 * Call ID 생성
	 */
	generateCallId: function () {
		return this.currentUserId + '-' + Date.now();
	},

	/**
	 * 정리
	 */
	cleanup: function () {
		if (this.localStream) {
			this.localStream.getTracks().forEach((track) => {
				track.stop();
			});
		}

		this.peerConnections.forEach((pc) => {
			pc.close();
		});

		this.peerConnections.clear();

		if (this.ws) {
			this.ws.close();
		}

		console.log('WebRTC 정리 완료');
	}
};

console.log('webrtc.js 로드 완료');
