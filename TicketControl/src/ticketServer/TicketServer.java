package ticketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ticketServer.dao.Menu;
import ticketServer.dao.MenuDAO;
import ticketServer.packet.BarcodePacket;
import ticketServer.packet.MenuPacket;
import ticketServer.packet.PointPacket;
import ticketServer.packet.TicketPacket;

public class TicketServer extends Application{
	private AsynchronousChannelGroup channelGroup;
	private AsynchronousServerSocketChannel serverSocketChannel;
	private List<Client> connections = new Vector<>();
	
//	private static final String SERVER_IP = "localhost";
	private static final String SERVER_IP = "70.12.109.100";
	private static final int SERVER_PORT = 6001;
	private static final int BUFFER_SIZE = 1024;
	
	
	
	class Client{
		AsynchronousSocketChannel socketChannel;
		
		public Client(AsynchronousSocketChannel socketChannel) {
			this.socketChannel = socketChannel;
			
			receive();
		}
		
		void response(TicketPacket packet){
			SimpleDateFormat sdf = null;
			Date currentTime = null;
			
			if(packet instanceof PointPacket){
				PointPacket p = (PointPacket)packet;
				p.setPoint(p.getPoint());
				
				try {
					System.out.println("구매자 정보 : "+socketChannel.getRemoteAddress());
					sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
					currentTime = new Date();
					System.out.println("구매시간 : "+ sdf.format(currentTime));
					System.out.println("구매금액 : "+ p.getPoint());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (packet instanceof BarcodePacket){
				BarcodePacket p = (BarcodePacket)packet;
				p.setBarcode(p.getBarcode());
				try {
					System.out.println("구매자 정보 : "+socketChannel.getRemoteAddress());
					sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
					currentTime = new Date();
					System.out.println("정산시간 : "+ sdf.format(currentTime));
					int amount = Integer.parseInt(p.getBarcode().substring(0, 2)) * 100;
					System.out.println("정산금액 : " + amount);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (packet instanceof MenuPacket){
//				MenuPacket p = (MenuPacket)packet;
//				MenuDAO dao = new MenuDAO();
//				
////				List<Menu []> list = dao.getWeekMenu(dao.searchMonday(dao.today()));
//				ArrayList<Menu []> list = new ArrayList<>();
//				Menu [] menues = new Menu [2];
//				
//				for (int i = 0; i< menues.length;i++) {
//					menues[i] = new Menu();
//					menues[i].setNum(1);
//					menues[i].setInformation_date("2016.10.19");
//					menues[i].setMenu_type(1);
//					menues[i].setRice("rice");
//					menues[i].setSoup("soup");
//					menues[i].setSidedish1("sidedish1");
//					menues[i].setSidedish2("sidedish2");
//					menues[i].setSidedish3("sidedish3");
//					menues[i].setImage("C:\\menu\\9920161019.img");
//				}
//				list.add(menues);
//				
//				p.setMenuList(list);
			}
			else {
				System.out.println("잘못된 패킷입니다.");
			}
		}
		
		void receive(){ 
			ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
			socketChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer attachment) {

					// 수신 데이터 처리 ///////////////////////////////////////////
					byte [] bytes = attachment.array();
					TicketPacket packet = (TicketPacket)TicketSerialize.deserialize(bytes);
					response(packet);
								
					// 요청 클라이언트로 응답처리 /////////////////////////////////
					Client.this.send(packet);
					
					ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
					socketChannel.read(byteBuffer, byteBuffer, this);
				}

				@Override
				public void failed(Throwable arg0, ByteBuffer arg1) {
					try {
						connections.remove(Client.this);
						System.out.println("[연결개수 : " + connections.size() + "]");
						socketChannel.close();
					} catch (IOException e) {}
				}
			});
		}
		
		
		
		void send(Object obj){ 
			byte[] bytes = TicketSerialize.serialize(obj);
			ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
			ByteBuffer byteBuffer = buff.wrap(bytes, 0, bytes.length);
			
			socketChannel.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {

				@Override
				public void completed(Integer result, Void attachment) {
					
				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					try {
						connections.remove(Client.this);
						System.out.println("[연결개수 : " + connections.size() + "]");
						socketChannel.close();
					} catch (IOException e) {}
				}
			});
			
		}
	}

	public void startServer(){ 
		try {
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
			serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
			serverSocketChannel.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
		} catch (IOException e) {
			if (serverSocketChannel.isOpen()){
				stopServer();
				return;
			}
		}
		
		System.out.println("[서버시작]");
		
		serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
				
				try {
					System.out.println("[연결수락 : " + socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + "]" );
					Client client = new Client(socketChannel);
					connections.add(client);
					System.out.println("[연결개수 : " + connections.size() + "]");
					
					serverSocketChannel.accept(null, this);
				} catch (IOException e) {
				}
			}

			@Override
			public void failed(Throwable arg0, Void arg1) {
				if (serverSocketChannel.isOpen()){
					stopServer();
				}
			}
		});
		
	}
	
	public void stopServer(){
		if (!connections.isEmpty()){
			connections.clear();
			System.out.println("[연결개수 : " + connections.size() + "]");
		}
		
		if((channelGroup != null) && (!channelGroup.isShutdown())){
			try {
				channelGroup.shutdownNow();
			} catch (IOException e) {}
		}
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// TicketServer 시작
		startServer();
		
		//login stage 로딩
		Parent logInRoot = FXMLLoader.load(getClass().getResource("logIn\\Login.fxml"));
		
		
		stage.setTitle("Ticket Control Administrator");
		stage.setOnCloseRequest(event->stopServer());
		stage.setScene(new Scene(logInRoot));
		stage.show();
		
		//////////////////////////////////////////////////////////////////////
		
		Thread thread = new BarcodeScan();
		thread.setDaemon(true);
		thread.start();
		
		///////////////////////////////////////////////////////////////////////
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
