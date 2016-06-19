package cn.aezo.ls.remote.server;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;

import cn.aezo.ls.remote.RemoteChatDataAdapter;
import cn.aezo.ls.remote.RemoteChatMetaDataAdapter;

import com.lightstreamer.adapters.remote.DataProviderServer;
import com.lightstreamer.adapters.remote.MetadataProviderServer;
import com.lightstreamer.adapters.remote.log.Logger;

public class ServerMain implements Container{
    private static Logger _log = OutPrintLog.getInstance().getLogger("LS.ServerMain");

    public static final String ARG_HOST = "127.0.0.1";
    public static final String ARG_METADATA_RR_PORT = "6663";
    public static final String ARG_DATA_RR_PORT = "6661";
    public static final String ARG_DATA_NOTIF_PORT = "6662";
    public static final String ARG_NAME = "remoteAdapter";
    
    @Override
	public void init(String[] args, String name, String configFile)
			throws ContainerException {
		
	}

	@Override
	public boolean start() throws ContainerException {
		 _log.info("Lightstreamer RocketMQ-LightStreamer Adapter Standalone Server starting...");

        // Server.setLoggerProvider(OutPrintLog.getInstance()); // 一致打印remote adapter是否保持连接

        Map<String,String> parameters = new HashMap<String,String>();
        String host = ARG_HOST;
        int rrPortMD = Integer.valueOf(ARG_METADATA_RR_PORT);
        int rrPortD = Integer.valueOf(ARG_DATA_RR_PORT);
        int notifPortD = Integer.valueOf(ARG_DATA_NOTIF_PORT);
        String name = ARG_NAME;
        
        {
            MetadataProviderServer server = new MetadataProviderServer();
            server.setAdapter(new RemoteChatMetaDataAdapter());
            server.setAdapterParams(parameters);
            // server.setAdapterConfig not needed by LiteralBasedProvider
            if (name != null) {
                server.setName(name);
            }
            _log.debug("Remote Metadata Adapter initialized");
   
            ServerStarter starter = new ServerStarter(host, rrPortMD, -1);
            starter.launch(server);
        }
        {
            DataProviderServer server = new DataProviderServer();
            //server.setAdapter(new StockQuotesDataAdapter());
            server.setAdapter(new RemoteChatDataAdapter());
            // server.AdapterParams not needed by StockListDemoAdapter
            // server.AdapterConfig not needed by StockListDemoAdapter
            if (name != null) {
                server.setName(name);
            }
            _log.debug("Remote Data Adapter initialized");
   
            ServerStarter starter = new ServerStarter(host, rrPortD, notifPortD);
            starter.launch(server);
        }

        _log.info("Lightstreamer RocketMQ-LightStreamer Adapter Standalone Server running");
	
		return false;
	}

	@Override
	public void stop() throws ContainerException {
		// 定义关闭线程
		Thread shutdownThread = new Thread() {
			public void run() {
				try {
					ServerMain.this.stop();
				} catch (ContainerException e) {
					e.printStackTrace();
				}
			}
		};
		// jvm关闭的时候先执行该线程钩子
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

	@Override
	public String getName() {
		return null;
	}

}