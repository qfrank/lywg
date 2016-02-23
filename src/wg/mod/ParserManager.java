package wg.mod;

import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.net.socket.protocol.IProtocolMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Frank Tang <br/>
 * Date: 15/2/1<br/>
 * Time: 上午12:07<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class ParserManager extends org.frkd.net.socket.protocol.ParserManager {

    private Map<IMessageHandler, IMessageHandler> proxyHandlerMapping = new ConcurrentHashMap<IMessageHandler, IMessageHandler>();

    public ParserManager(IProtocolMap protocolMap) {
        super(protocolMap);
    }

    public void removeMessageHandler(Class<? extends BasicMessage> clazz, IMessageHandler handler) {
        IMessageHandler proxyHandler = proxyHandlerMapping.get(handler);
        super.removeMessageHandler(clazz, proxyHandler);
    }

    public void registerMessageHander(Class<? extends BasicMessage> clazz, BaseModule module, IMessageHandler handler) {
        IMessageHandler handlerProxy;
        final MessageHandlerWrapper handlerWrapper = new MessageHandlerWrapper(module, handler);
        if (handler instanceof IFlashMessageHandler) {
            handlerProxy = new IFlashMessageHandler() {
                @Override
                public void handle(BasicMessage message) {
                    handlerWrapper.handle(message);
                }
            };
        } else {
            handlerProxy = new IMessageHandler() {
                @Override
                public void handle(BasicMessage message) {
                    handlerWrapper.handle(message);
                }
            };
        }

        proxyHandlerMapping.put(handler, handlerProxy);

        super.registerMessageHander(clazz, handlerProxy);
    }

    private class MessageHandlerWrapper {

        private BaseModule fromModule;

        private IMessageHandler messageHandler;

        public MessageHandlerWrapper(BaseModule fromModule, IMessageHandler messageHandler) {
            this.fromModule = fromModule;
            this.messageHandler = messageHandler;
        }

        public void handle(BasicMessage message) {
            if (fromModule != null && fromModule.isModuleDisabled()) {
                return;
            }
            messageHandler.handle(message);
        }
    }

}
