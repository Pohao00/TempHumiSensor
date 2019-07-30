package datatomysql;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;
import java.sql.*;

public class light implements SerialPortEventListener {
	SerialPort serialPort;//摰儔serialport�隞�

	private BufferedReader input;//摰���nput buffer
	private static final int TIME_OUT = 2000;//閮剖���ort�������雿瘥怎��
	private static final int DATA_RATE = 115200;//閮剖�aud rate�9600

	//閮剖�erver IP,撣唾��,撖Ⅳ
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";//閮剖�DBC driver  
	static final String DB_URL = "jdbc:mysql://localhost/iot";//server IP敺���澈��迂
	static final String USER = "test123";
	static final String PASS = "test123";
	
	public void initialize() {
		CommPortIdentifier portId = null;//摰儔CommPortIdentifier�隞塚������port
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();//�摮������ort

		while (portEnum.hasMoreElements()) {//�������ort
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();//摰儔currPortId
			if (currPortId.getName().equals("COM3")) {//閮剖�rduino serial port
				portId = currPortId;
				break;
			}	
		}
		
		if (portId == null) {//憒�om port閮剖�隤歹�����銵�
			System.out.println("Could not find COM port.");
			return;
		}
		
		try {//���port
			//open serial port
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

			//閮剖�ort parameters
			serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

			//open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

			// add event listeners
			serialPort.addEventListener(this);// Registers a SerialPortEventListener object to listen for SerialEvents.
			serialPort.notifyOnDataAvailable(true);//Expresses interest in receiving notification when input data is available.
		} 
		catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	//���erial port鈭辣,霈�����蒂print�靘�
	public void serialEvent(SerialPortEvent oEvent) {
        Connection connection = null;//撱箇�onnection�隞�
        Statement statement = null;//撱箇�tatement�隞�
        
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {//if data available on serial port
			try {
	        	//���mysql database
	            connection = DriverManager.getConnection(DB_URL, USER, PASS);
	            System.out.println("SQL Connection to database established!");
	            
				String inputLine=input.readLine();
				System.out.println(inputLine);
				String inputLine2=input.readLine();
				System.out.println(inputLine2);
				
				//�銵uery
				statement = connection.createStatement();
	            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO sensor (temp,humi) value ('"+inputLine+"','"+inputLine2+"')");
	            
	            pstmt.executeUpdate();
	            
	            pstmt.close();
	            
	            statement.close();
	            connection.close();
			} 
			catch (SQLException e) {
	        	//Handle errors for JDBC
	            System.out.println("Connection Failed! Check output console");
	            return;
	        } 
	        catch (Exception e) {
				//System.err.println(e.toString());
			}
			finally {
	            try {
	                if(connection != null)
	                    connection.close();
	                System.out.println("Connection closed !!");
	            } 
	            catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
			/*try {
		        Thread.sleep(5000);
		         } 
		                catch (InterruptedException e) {}*/
		}
		
	}

	public static void main(String[] args) throws Exception {
		light main = new light();//creates an object of the class
		main.initialize();
		
    	//call to ensure the driver is registered
        try {
            Class.forName(JDBC_DRIVER);
        } 
        catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }
        
		System.out.println("Started");
	}
}

