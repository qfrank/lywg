package wg.util;

import com.game.legend.net.ProtocolMap;
import org.frkd.io.InputByteArray;
import org.frkd.net.socket.MessageReaderSimulator;
import org.frkd.net.socket.protocol.BasicProtocolMap;
import org.frkd.net.socket.protocol.ParserManager;
import org.frkd.util.StringUtil;
import org.frkd.util.log.LoggerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacketUtil {
	
	static{
		System.setProperty("appdir", "/dev/project/lywg");
		LoggerUtil.initLoggerConfig();
		
		Logger gl = Logger.getLogger("");
		gl.setLevel(Level.FINEST);
		for(Handler h:gl.getHandlers()){
			h.setLevel(Level.FINEST);
		}
	}

	static Pattern PACKET_HEADER_PATTERN = Pattern.compile("^\\d+\\s+(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+)\\s+(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+).+(Send|Recv)\\s+$");
	
	static Pattern PACKET_LINE_PATTERN = Pattern.compile("^\\w{4}\\s+(.+?)\\s{4,}+.*$");
	
	static Logger logger = LoggerUtil.getLogger(PacketUtil.class);
	
	static final String basePath = "/dev/wg/ÁÒÑæ·ÖÎö/·â°ü/";
	
	public static void main(String[] args)throws Exception {
		LoggerUtil.getLogger(ParserManager.class).setLevel(Level.FINEST);

		//trans2Header("77 71 77 2E 31 0D ");
		readFile(new File(basePath+"tmp1.txt"));

		//BattleManager.as
		//readFile(new File(basePath+"±»É±ËÀ.txt"));

		//M1531¡¢SpriteManager¡¢
	}
	
	static void readFile(File file) throws Exception{
		MessageReaderSimulator mrs = new MessageReaderSimulator(new ParserManager(new ProtocolMap()));
		mrs.setNeedDecode(true);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		boolean shouldRead = false;
		while((line = br.readLine())!=null){
			if(!StringUtil.isBlank(line)){
				Matcher m1 = PACKET_HEADER_PATTERN.matcher(line);
				if(m1.matches()){
					String source = m1.group(1);
					String dest = m1.group(2);
					String type = m1.group(3);
					if(type.equals("Send")||source.contains("127.0.0.1")||dest.contains("127.0.0.1")||source.contains(":80")||dest.contains(":80")){
						shouldRead = false;
						continue;
					}else{
						shouldRead = true;
					}
				}
				
				if(shouldRead){
					Matcher m2 = PACKET_LINE_PATTERN.matcher(line);
					if(m2.matches()){
						String hexBytes = m2.group(1).trim();
						byte[] data = StringUtil.hexStr2bytes(hexBytes, " ");
						mrs.read(data.length, data);
					}
				}

			}
		}
	}

	static void trans2Header(String byteHexString){
		byteHexString = byteHexString.trim();
		byte[] b = StringUtil.hexStr2bytes(byteHexString, " ");
		InputByteArray iba = new InputByteArray(BasicProtocolMap.encrypt(b));
		logger.info("cmdLength:"+iba.readShort());
		logger.info("cmdType:"+iba.readInt());
		logger.info("-------------------------------------");
	}
	
}
