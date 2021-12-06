import Vue from 'vue';
import router from '../router';

let uuid = 0;
const CMD_TYPE = {
    CMD_HEART_BEAT: 'heart_beat'
};
const audio = new Audio('/media/message.mp3');
const defaultOptions = {
    retryLimit: 'infinity',
    retryDelay: 5000,
    heartbeatDelay: 540000 // 9分钟一次心跳
};

export default class WS {
    constructor(options = defaultOptions) {
        this.retryTimer = null;
        this.retryLimit = options.retryLimit;
        this.retryDelay = options.retryDelay;
        this.currentRetryTimes = 0;
        this.heartbeatTimer = null;
        this.heartbeatServerTimer = null;
        this.heartbeatDelay = options.heartbeatDelay;
    }

    get socket() {
        return this.WebSocket;
    }

    get readyState() {
        return this.WebSocket.readyState;
    }

    init(url, protocol = 'ws') {
        this.clear();
        this.WebSocket = new WebSocket(url, protocol);
    }

    detect() {
        return 'WebSocket' in window;
    }

    onOpen(openHandler) {
        this.WebSocket.addEventListener('open', (event) => {
            if (this.retryTimer) {
                clearTimeout(this.retryTimer);
            }
            this.clearHeartbeat();
            this.heartbeat();
            openHandler(event);
        });
    }

    onMessage(messageHandler) {
        this.WebSocket.addEventListener('message', (event) => {
            this.clearHeartbeat();
            this.heartbeat();
            messageHandler(event);
        });
    }

    onClose(closeHandler) {
        this.WebSocket.addEventListener('close', closeHandler);
    }

    onError(errorHandler) {
        this.WebSocket.addEventListener('error', () => {
            errorHandler();
            this.clear();
        });
    }

    retry(retryHandler) {
        this.retryTimer = setTimeout(() => {
            if (this.retryLimit === 'infinity' || this.currentRetryTimes < this.retryLimit) {
                this.currentRetryTimes++;
                retryHandler();
            } else {
                console.log('已达到最大重连次数');
                this.clear();
            }
        }, this.retryDelay);
    }

    heartbeat() {
        this.heartbeatTimer = setInterval(() => {
            const dataChunk = {
                type: CMD_TYPE.CMD_HEART_BEAT,
                msg: 'ping'
            };
            this.WebSocket.send(JSON.stringify(dataChunk));
            this.log('ping!');
            // this.heartbeatServerTimer = setTimeout(() => {
            //     this.WebSocket.close()
            // }, this.heartbeatDelay)
        }, this.heartbeatDelay);
    }

    clear() {
        clearTimeout(this.retryTimer);
        this.clearHeartbeat();
        this.WebSocket && this.WebSocket.close();
        this.WebSocket = null;
        this.retryTimer = null;
    }

    clearHeartbeat() {
        // clearTimeout(this.heartbeatTimer)
        clearInterval(this.heartbeatTimer);
        clearTimeout(this.heartbeatServerTimer);
    }

    log(message) {
        const now = new Date();
        const hours = now.getHours();
        const minutes = now.getMinutes();
        const seconds = now.getSeconds();
        const currentTime = `${hours}:${minutes < 10 ? '0' + minutes : minutes}:${
            seconds < 10 ? '0' + seconds : seconds
        }`;
        console.log('当前时间: ', currentTime, message);
    }
}

/**
 * 开启消息通知的接收任务
 */
export function startReceiveOrderMsgTask() {
    const { protocol } = location;
    const wsProtocol = protocol.includes('https') ? 'wss' : 'ws';
    const ws = new WS();
    const isSupportWebSocket = ws.detect();

    if (!isSupportWebSocket) {
        Vue.prototype.$message.warning('当前浏览器版本不支持websocket特性，请更换最新版本的chrome浏览器');
        return null;
    }

    const request = () => {
        try {
            const token = Vue.prototype.$vueCookie.get('bjyixiu');

            ws.init(`${wsProtocol}://${location.host}/ws?token=${token}`, wsProtocol);
            // ws.init(`${wsProtocol}://192.168.50.124/ws?token=${token}`, wsProtocol)

            ws.onOpen(function () {
                console.log('websocket connection successful!');
            });

            ws.onMessage(function (event) {
                // eslint-disable-next-line no-unneeded-ternary
                const isLogin = Vue.prototype.$Store.state.isLogin ? false : true;
                if (typeof event.data === 'string' && !location.hash.includes('/Login') && isLogin) {
                    const { title, body, uri, objectID, extra = {} } = JSON.parse(event.data);
                    const key = ++uuid;
                    playMessageAudio();
                    Vue.prototype.$notification['info']({
                        message: title,
                        description: body,
                        duration: null,
                        key,
                        onClick: () => {
                            Vue.prototype.$notification.close(key);
                            switch (uri) {
                                case '/hospital/SuppliesClaim':
                                    router.replace({ path: `${uri}`, query: { voucher_sn: objectID } });
                                    break;
                                case '/commonView/salesOrder':
                                    router.replace({ path: `${uri}`, query: { so_sn: objectID } });
                                    break;
                                case '/ptm/agencyManage/RegistAudit':
                                    router.replace({
                                        path: `${uri}`,
                                        query: { name: extra.org_name, system_type: extra.system_type }
                                    });
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            });

            ws.onClose(function () {
                // eslint-disable-next-line no-unneeded-ternary
                const isLogin = Vue.prototype.$Store.state.isLogin ? false : true;
                if (isLogin) {
                    console.log('websocket disconnect');
                    console.log('retry connecting...');
                    ws.retry(request);
                } else {
                    ws.clear();
                }
            });

            ws.onError(function () {
                console.log('An error occurred in websocket');
            });
        } catch (error) {
            console.log(error);
        }
    };

    request();

    return ws;
}

function playMessageAudio() {
    try {
        audio.play();
    } catch (error) {
        console.error(error);
    }
}
