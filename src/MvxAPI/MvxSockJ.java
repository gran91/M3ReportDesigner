package MvxAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MvxSockJ {

    private static double Version = 5.0D;
    public boolean DEBUG = false;
    public boolean TRIM = true;
    public boolean PROXY = false;
    private static final String blanks = "                                ";
    private static final int F_FIELDACCESS = 1;
    private static final int F_UCS2 = 2;
    private static final String pwdkey = "0123456789";
    private Socket theSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private String wrapperName;
    private int wrapperPort;
    private int wrapperPortID;
    private String applicationName;
    private int cryptOn;
    private String cryptKey;
    private int flags;
    private String lastError;
    private String lastErrorID;
    private String lastErrorField;
    private Crypto cryptoObj;
    private char[] recvCharBuf = new char[2048];
    private byte[] recvByteBuf = new byte[4096];
    private String conversionTable;
    private int serverType;
    private static final int serverType_LOGON = 0;
    private static final int serverType_JavaNG = 1;
    private static final int serverType_AS400 = 2;
    private static final int serverType_WIN = 3;
    private Vector fields = new Vector();
    private Vector fldmapIn = new Vector();
    private Vector fldmapOut = new Vector();
    private String transaction;
    private String curTrans;
    private Deflater deflater = new Deflater();
    private Inflater inflater = new Inflater();
    private byte[] zipWorkBuffer = new byte[512];
    private byte[] zipDestBuffer = new byte[4096];
    private ITrustedConnection trustConn = null;
    private boolean zippedTransactions = false;
    public static final String version = "$Revision: /main/DEV_12/SP_12.4.6/SP_1.2.5/1 $";

    public MvxSockJ() {
        this.theSocket = null;
        this.inStream = null;
        this.outStream = null;
        this.wrapperName = null;
        this.applicationName = null;
        this.cryptOn = 0;
        this.cryptKey = null;
        this.lastError = null;
    }

    public MvxSockJ(String host, int port, String appName, int encr, String key) {
        this.lastError = null;
        mvxSetup(host, port, appName, encr, key);
    }

    public void mvxSetup(String host, int port, String appName, int encr, String key) {
        this.wrapperName = host;
        this.wrapperPort = port;
        if (appName.length() > 32) {
            this.applicationName = appName.substring(0, 32);
        } else {
            this.applicationName = ensureSize(appName, 32);
        }
        this.cryptOn = encr;
        this.cryptKey = key;
        if (this.cryptOn == 1) {
            this.cryptoObj = new Crypto();
        }
    }

    public int mvxConnect(String host, int port, String userId, String pwd, String MI, String key)throws Exception {
        this.wrapperName = host;
        this.wrapperPort = port;
        this.applicationName = ensureSize("", 32);
        this.cryptOn = (key != null ? 1 : 0);
        this.cryptKey = key;
        if (this.cryptOn == 1) {
            this.cryptoObj = new Crypto();
        }
        return mvxInit("LOCALA", userId, pwd, MI);
    }

    public int mvxConnect(String host, int port, String userId, String pwd, String MI, String key, ITrustedConnection conn)throws Exception {
        this.trustConn = conn;
        return mvxConnect(host, port, userId, pwd, MI, key);
    }

    public int mvxInit(String ConoDivi, String userId, String userPwd, String program) throws Exception{
        this.serverType = 0;
        this.flags = 0;

        if (this.wrapperName == null) {
            this.lastError = "mvxInit() called before proper setup of communication parameters.";
            return 1;
        }

        if ((ConoDivi = ensureSize(ConoDivi, 32)) == null) {
            this.lastError = "Passed parameter ConoDivi too long (max 32)";
            return 1;
        }
        if ((userId = ensureSize(userId, 16)) == null) {
            this.lastError = "Passed parameter userId too long (max 16)";
            return 1;
        }
        if ((userPwd = ensureSize(userPwd, 16)) == null) {
            this.lastError = "Passed parameter userPwd too long (max 16)";
            return 1;
        }
        if ((program = ensureSize(program, 32)) == null) {
            this.lastError = "Passed parameter MI program too long (max 32)";
            return 1;
        }
        try {
            createSocket();
        } catch (IOException e) {
            this.lastError = e.getMessage();
            System.err.println("MvxSockJ: " + this.lastError);
            e.printStackTrace();
            return 2;
        }
        String IPAddress;
        String sendString;
        String recvString;
        if (!this.PROXY) {
            IPAddress = ensureSize(this.theSocket.getLocalAddress().getHostAddress(), 16);
        } else {
            IPAddress = "ForceProxy";
        }
        if (this.cryptOn == 1) {
            sendString = ConoDivi + userId + userPwd + program + this.applicationName + IPAddress;
            StringBuffer inStrBuff = new StringBuffer(sendString);
            StringBuffer outStrBuff = new StringBuffer();
            int lenBuff = this.cryptoObj.MvxEncrypt(sendString.length(), inStrBuff, outStrBuff, this.cryptKey);
            sendString = "CRLOG" + outStrBuff.toString();
            try {
                this.outStream.writeInt(sendString.length());
                this.outStream.write(sendString.getBytes(), 0, sendString.length());
                this.outStream.flush();
            } catch (IOException e) {
                this.lastError = ("Initial write error " + e.getMessage());
                System.err.println("MvxSockJ: " + this.lastError);
                return 2;
            }
        } else {
            byte[] aPwd = userPwd.getBytes();
            byte[] aKey = "0123456789".getBytes();
            for (int ul = 0; ul < 10; ul++) {
                if (aPwd[ul] != aKey[ul]) {
                    aPwd[ul] = (byte) (aPwd[ul] ^ aKey[ul]);
                }
            }
            sendString = "PWLOG" + ConoDivi + userId;
            String sendString3 = program + this.applicationName + IPAddress;
            int sendLen = sendString.length() + 16 + sendString3.length();
            try {
                this.outStream.writeInt(sendLen);
                this.outStream.write(sendString.getBytes(), 0, sendString.length());
                this.outStream.write(aPwd, 0, 16);
                this.outStream.write(sendString3.getBytes(), 0, sendString3.length());
                this.outStream.flush();
            } catch (IOException e) {
                this.lastError = ("Initial write error " + e.getMessage());
                System.err.println("MvxSockJ: " + this.lastError);
                return 2;
            }

        }

        try {
            this.inStream.readShort();
            recvString = this.inStream.readUTF();
        } catch (IOException e) {
            sendString = "LOGON" + ConoDivi + userId + userPwd + program + this.applicationName + IPAddress;
            try {
                createSocket();

                this.outStream.writeShort(0);
                this.outStream.writeUTF(sendString);
                this.outStream.flush();
                this.inStream.readShort();
                recvString = this.inStream.readUTF();
            } catch (IOException ex) {
                this.lastError = ("Initiation error " + ex.getMessage());
                System.err.println("MvxSockJ: " + this.lastError);
                return 2;
            }
        }
        if ((program.startsWith("LOOPBACK")) || (this.applicationName.startsWith("LOOPBACK"))) {
            return 0;
        }
        if (recvString == null) {
            this.lastError = "Got zero bytes from M3";
            return 3;
        }
        if (recvString.startsWith("NOK")) {
            this.lastError = ("The remote server returned: " + recvString);
            return 7;
        }

        this.serverType = 2;

        if (recvString.startsWith("WIN")) {
            this.serverType = 3;
        }

        if (recvString.startsWith("UCS2 ")) {
            this.flags |= 2;
            if (this.DEBUG) {
                System.out.println("Communicates using UCS2");
            }

            if (recvString.startsWith("WIN", 5)) {
                this.serverType = 3;
            }
        }
        if (recvString.startsWith("NGCONOK")) {
            if (this.cryptOn == 1) {
                try {
                    this.theSocket.close();
                } catch (Exception e) {
                }
                this.lastError = "Encrypted communication is not available for this M3 server";
                return 3;
            }
            this.serverType = 1;
            this.flags |= 2;
            if (this.DEBUG) {
                System.out.println("MvxSockJ.mvxInit() Ok! (Connected to JavaNG via proxy)");
            }
            try {
                this.theSocket.setSoTimeout(60000);
            } catch (SocketException e) {
            }
        }
        if (recvString.startsWith("CHG")) {
            try {
                StringTokenizer st = new StringTokenizer(recvString, " ,");
                st.nextToken();
                this.wrapperName = st.nextToken();
                this.wrapperPort = Integer.parseInt(st.nextToken());
                this.wrapperPortID = Integer.parseInt(st.nextToken());
            } catch (Exception e) {
                this.lastError = ("The remote server returned unreadable reroute information: " + recvString);
                System.err.println("MvxSockJ: " + this.lastError);
                return 4;
            }

            if (this.cryptOn == 1) {
                try {
                    this.theSocket.close();
                } catch (Exception e) {
                }
                this.lastError = "Encrypted communication is not available for this M3 server";
                return 3;
            }

            try {
                this.theSocket.close();
                if (this.DEBUG) {
                    System.out.println("ServerType = Java. Rerouting to " + this.wrapperName + ":" + this.wrapperPort + " portId:" + this.wrapperPortID);
                }

                this.theSocket = new Socket(this.wrapperName, this.wrapperPort);
                this.theSocket.setSoTimeout(60000);
                this.inStream = new DataInputStream(new BufferedInputStream(this.theSocket.getInputStream()));
                this.outStream = new DataOutputStream(new BufferedOutputStream(this.theSocket.getOutputStream()));
                this.outStream.writeInt(this.wrapperPortID);
                this.outStream.flush();

                int rc = this.inStream.readInt();
                if (rc != 0) {
                    if (rc == 1) {
                        this.lastError = ("Program " + program + " not found");
                    } else {
                        this.lastError = ("Remote server did not accept the connection, rc = " + rc);
                    }
                    this.theSocket.close();
                    return 6;
                }
            } catch (IOException e) {
                this.lastError = "MvxSockJ.mvxInit() got exception while creating socket to rerouted adress";
                System.err.println("MvxSockJ:" + e.getMessage());
                e.printStackTrace();
                return 5;
            }
            this.serverType = 1;
            this.flags |= 2;
            if (this.DEBUG) {
                System.out.println("MvxSockJ.mvxInit() Ok! (Connected to JavaNG)");
            }
        } else if (this.DEBUG) {
            System.out.println("MvxSockJ.mvxInit() Ok!");
        }

        if (this.serverType == 2) {
            try {
                this.theSocket.setSoTimeout(0);
            } catch (SocketException e) {
                sendString = mvxTrans("FpwVersion     ");
            }
        }
        if (sendString.startsWith("OK   ")) {
            this.flags |= 1;
        }
        return 0;
    }

    private void createSocket() throws UnknownHostException, IOException, SocketException {
        this.theSocket = new Socket(this.wrapperName, this.wrapperPort);
        this.theSocket.setSoTimeout(60000);
        this.inStream = new DataInputStream(new BufferedInputStream(this.theSocket.getInputStream()));
        this.outStream = new DataOutputStream(new BufferedOutputStream(this.theSocket.getOutputStream()));
        if ((this.trustConn != null) && (!this.trustConn.doTrustHandshake(this.inStream, this.outStream))) {
            throw new IOException("Trusted handshake failed");
        }
    }

    public int mvxClose()throws Exception {
        if (this.theSocket == null) {
            this.inStream = null;
            this.outStream = null;
            this.lastError = "mvxClose(): No connection open!";
            return 2;
        }

        String sendString = "CLOSE           ";
        if (this.cryptOn == 1) {
            StringBuffer inStrBuff = new StringBuffer(sendString);
            StringBuffer outStrBuff = new StringBuffer();
            int lenBuff = this.cryptoObj.MvxEncrypt(sendString.length(), inStrBuff, outStrBuff, this.cryptKey);
            sendString = outStrBuff.toString();
        }
        int result = sendData(sendString);
        if (result != 0) {
            return result;
        }

        try {
            this.theSocket.shutdownInput();
            this.theSocket.close();
        } catch (IOException e) {
            this.lastError = ("Failed closing socket. Got exception: " + e.getMessage());
            System.err.println(this.lastError);
            e.printStackTrace();
            return 1;
        }
        this.theSocket = null;
        this.inStream = null;
        this.outStream = null;

        return 0;
    }

    public String mvxRecv() throws Exception{
        String recvString;
        int recvLen;
        if ((this.flags & 0x2) != 0) {
            try {
                recvLen = this.inStream.readInt();
                if (this.zippedTransactions) {
                    this.inStream.readFully(this.recvByteBuf, 0, recvLen);
                    this.inflater.setInput(this.recvByteBuf, 0, recvLen);
                    int newSize = 0;
                    while (!this.inflater.finished()) {
                        int count = this.inflater.inflate(this.zipWorkBuffer);
                        if (count > 0) {
                            System.arraycopy(this.zipWorkBuffer, 0, this.zipDestBuffer, newSize, count);
                            newSize += count;
                        }
                    }
                    this.inflater.reset();
                    int cOffs = 0;
                    for (int i = 0; i < newSize; i += 2) {
                        this.recvCharBuf[(cOffs++)] = (char) ((this.zipDestBuffer[i] & 0xFF) << 8 | this.zipDestBuffer[(i + 1)] & 0xFF);
                    }
                    int orgSize = cOffs * 2;
                    int packedSize = recvLen;

                    recvString = new String(this.recvCharBuf, 0, cOffs);
                } else {
                    for (int i = 0; i < recvLen / 2; i++) {
                        this.recvCharBuf[i] = this.inStream.readChar();
                    }
                    recvString = new String(this.recvCharBuf, 0, recvLen / 2);
                }
            } catch (Exception e) {
                this.lastError = ("mvxRecv() got exception: " + e.getMessage());
                System.err.println(this.lastError);
                e.printStackTrace();
                return null;
            }
            if (this.DEBUG) {
                System.out.print("Received " + recvLen / 2 + " chars (" + recvLen + " bytes): ");
            }
        } else {
            try {
                recvLen = this.inStream.readInt();
                this.inStream.readFully(this.recvByteBuf, 0, recvLen);
                if (this.conversionTable != null) {
                    recvString = new String(this.recvByteBuf, 0, recvLen, this.conversionTable);
                } else {
                    recvString = new String(this.recvByteBuf, 0, recvLen);
                }
            } catch (Exception e) {
                this.lastError = ("mvxRecv() got exception: " + e.getMessage());
                System.err.println(this.lastError);
                e.printStackTrace();
                return null;
            }

            if (this.DEBUG) {
                System.out.print("Received " + recvLen + " chars: ");
            }
        }

        if (this.cryptOn == 1) {
            StringBuffer inStrBuff = new StringBuffer(recvString);
            StringBuffer outStrBuff = new StringBuffer();

            recvLen = recvString.length();
            if (recvLen % 4 != 0) {
                recvLen -= 1;
            }
            int lenBuff = this.cryptoObj.MvxDecrypt(recvLen, inStrBuff, outStrBuff, this.cryptKey);
            recvString = outStrBuff.toString();
        }
        if (this.DEBUG) {
            System.out.println("'" + recvString + "'");
        }
        return recvString;
    }

    public String mvxTrans(String sendString) throws Exception{
        this.lastError = "";

        if (sendString.startsWith("Snd", 0)) {
            this.lastError = "mvxTrans was called with a transaction only intended to use with mvxSend";
            return null;
        }

        if (this.cryptOn == 1) {
            StringBuffer inStrBuff = new StringBuffer(sendString);
            StringBuffer outStrBuff = new StringBuffer();
            int lenBuff = this.cryptoObj.MvxEncrypt(sendString.length(), inStrBuff, outStrBuff, this.cryptKey);
            sendString = outStrBuff.toString();
        }
        int result = sendData(sendString);
        if (result != 0) {
            return null;
        }
        return mvxRecv();
    }

    public int mvxSend(String sendString) {
        if (sendString.startsWith("Snd")) {
            if (this.cryptOn == 1) {
                StringBuffer inStrBuff = new StringBuffer(sendString);
                StringBuffer outStrBuff = new StringBuffer();
                int lenBuff = this.cryptoObj.MvxEncrypt(sendString.length(), inStrBuff, outStrBuff, this.cryptKey);
                sendString = outStrBuff.toString();
            }
            return sendData(sendString);
        }

        this.lastError = "Illegal string input to MvxSend. String must start with \"Snd\"";
        if (this.DEBUG) {
            System.out.println(this.lastError);
        }
        return 7;
    }

    public String mvxGetLastError() {
        return this.lastError;
    }

    public String mvxGetLastMessageID() {
        return this.lastErrorID;
    }

    public String mvxGetLastBadField() {
        return this.lastErrorField;
    }

    public boolean setConversionTable(String newcp) {
        try {
            newcp.getBytes(newcp);
        } catch (Exception e) {
            return false;
        }
        this.conversionTable = newcp;
        return true;
    }

    public double mvxVersion() {
        return Version;
    }

    public void mvxClearFields() {
        this.fields.removeAllElements();
    }

    public void mvxSetField(String name, String data) {
        this.fields.addElement(name);
        this.fields.addElement(data);
    }

    public boolean mvxSetZippedTransactions(boolean zipped) throws Exception{
        if ((this.zippedTransactions ^ zipped)) {
            sendData(zipped ? "ZipTransOn     " : "ZipTransOff    ");
            String response = mvxRecv();
            if (response.startsWith("OK ")) {
                this.zippedTransactions = zipped;
                return true;
            }
            return false;
        }
        return true;
    }

    public int mvxAccess(String transName) throws Exception{
        int out = 0;
        int in = 0;
        int none = 0;
        boolean foundOut = false;
        boolean foundIn = false;

        this.lastError = "";
        this.lastErrorID = "";
        this.lastErrorField = "";
        if ((this.flags & 0x1) == 0) {
            this.lastError = "Field access not supported with the current FPW version.";
            this.fields.removeAllElements();
            return 7;
        }

        if ((transName == null) || (transName.length() == 0)) {
            if (this.transaction.startsWith("OK ")) {
                return 0;
            }
            this.transaction = mvxRecv();
            return 0;
        }
        this.curTrans = transName;

        if (this.fldmapOut.size() > 0) {
            for (out = 0; out < this.fldmapOut.size(); out += 2) {
                if (((String) this.fldmapOut.elementAt(out)).compareTo(transName) == 0) {
                    foundOut = true;
                    break;
                }

            }

        }

        if (!foundOut) {
            String send = "GetOutLayout   " + transName;
            String trans = mvxTrans(send);
            if (trans == null) {
                return 8;
            }
            if (!trans.startsWith("NOK  ")) {
                this.fldmapOut.addElement(transName);
                this.fldmapOut.addElement(trans.substring(15));
                none = 0;
            } else {
                this.fldmapOut.addElement(transName);
                this.fldmapOut.addElement("0");
                none = 1;
            }

        }

        if (this.fldmapIn.size() > 0) {
            for (in = 0; in < this.fldmapIn.size(); in += 2) {
                if (((String) this.fldmapIn.elementAt(in)).compareTo(transName) == 0) {
                    foundIn = true;
                    in++;
                    if (((String) this.fldmapIn.elementAt(in)).compareTo("0") == 0) {
                        none = 1;
                        break;
                    }
                    none = 0;
                    break;
                }
            }
        }
        if (!foundIn) {
            String send = "GetInLayout    " + transName;
            String trans = mvxTrans(send);
            if (trans == null) {
                return 8;
            }

            if ((trans.startsWith("NOK  ")) && (none == 1)) {
                this.lastError = trans;
                this.fields.removeAllElements();
                return 8;
            }
            if (trans.startsWith("OK   ")) {
                this.fldmapIn.addElement(transName);
                this.fldmapIn.addElement(trans.substring(15));
                in = this.fldmapIn.size() - 1;
                none = 0;
            } else {
                this.fldmapIn.addElement(transName);
                this.fldmapIn.addElement("0");
                none = 1;
            }
        }
        String transStr;
        if (none == 1) {
            transStr = ensureSize(transName, 15);
        } else {
            String map = (String) this.fldmapIn.elementAt(in);

            String s = map.substring(map.lastIndexOf(";"));
            in = s.indexOf(",");
            s = s.substring(in + 1, s.lastIndexOf(","));
            int pos = Integer.parseInt(s);
            pos += Integer.parseInt(map.substring(map.lastIndexOf(",") + 1));

            char[] transChars = new char[pos + 1];
            for (int i = 0; i < pos; i++) {
                transChars[i] = ' ';
            }
            transName.getChars(0, transName.length(), transChars, 0);
            for (int i = 0; i < this.fields.size(); i += 2) {
                int ix = map.indexOf((String) this.fields.elementAt(i));
                if (ix == -1) {
                    this.lastError = ("Field " + (String) this.fields.elementAt(i) + " not found in " + this.curTrans + " transaction");
                    this.fields.removeAllElements();
                    return 7;
                }
                String f = map.substring(ix);
                f = f.substring(f.indexOf(",") + 1);
                f = f.substring(0, f.indexOf(","));
                pos = Integer.parseInt(f) - 1;
                out = ((String) this.fields.elementAt(i + 1)).length();
                ((String) this.fields.elementAt(i + 1)).getChars(0, out, transChars, pos);
            }
            transStr = new String(transChars);
        }

        this.fields.removeAllElements();

        if (transName.startsWith("Snd")) {
            return mvxSend(transStr);
        }

        this.transaction = mvxTrans(transStr);
        if (this.transaction == null) {
            return 7;
        }
        if (this.transaction.startsWith("NOK  ")) {
            if (this.transaction.length() > 256) {
                this.lastErrorID = this.transaction.substring(256, 263);
            }
            if (this.transaction.length() > 264) {
                this.lastErrorField = this.transaction.substring(264).trim();
            }
            if (this.transaction.length() > 256) {
                this.lastError = this.transaction.substring(0, 255).trim();
            } else {
                this.lastError = this.transaction.trim();
            }
            return 8;
        }
        return 0;
    }

    public String mvxGetField(String name) {
        int out = 0;
        int pos = 0;
        int len = 0;
        int ix = 0;
        boolean found = false;

        if (this.fldmapOut.size() > 0) {
            for (out = 0; out < this.fldmapOut.size(); out += 2) {
                if (((String) this.fldmapOut.elementAt(out)).compareTo(this.curTrans) == 0) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            this.lastError = ("Field map for " + this.curTrans + " not available.");
            return null;
        }

        String map = (String) this.fldmapOut.elementAt(out + 1);
        ix = map.indexOf(name);
        if (ix == -1) {
            this.lastError = ("Field " + name + " not found in " + this.curTrans);
            return null;
        }
        String f = map.substring(ix);
        try {
            StringTokenizer st = new StringTokenizer(f, ",;");
            st.nextToken();
            pos = Integer.parseInt(st.nextToken()) - 1;
            len = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
            this.lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }

        if (this.TRIM) {
            try {
                return this.transaction.substring(pos, pos + len).trim();
            } catch (StringIndexOutOfBoundsException x) {
                return "";
            }
        }
        try {
            return this.transaction.substring(pos, pos + len);
        } catch (StringIndexOutOfBoundsException x) {
        }
        return ensureSize("", len);
    }

    public boolean mvxMore() {
        return this.transaction.startsWith("REP  ");
    }

    public int getSoTimeout()
            throws SocketException {
        return this.theSocket.getSoTimeout();
    }

/** @deprecated */
    public void setSoTimeout(int timeout) throws SocketException {
        this.theSocket.setSoTimeout(timeout);
    }

    public int mvxSetMaxWait(int timeout) {
        try {
            this.theSocket.setSoTimeout(timeout);
        } catch (SocketException e) {
            this.lastError = "Failed to set receive timeout.";
            System.err.println("MvxSockJ: Failed to set receive timeout. " + e.getMessage());
            return 7;
        }
        return 0;
    }

    public int mvxSetBlob(byte[] buffer) {
        int rc;
        if (this.serverType == 2) {
            this.lastError = "Binary objects function not supported in the server";
            return 6;
        }
        sendData("setblob        ");
        try {
            this.outStream.writeInt(buffer.length);
            this.outStream.write(buffer, 0, buffer.length);
            this.outStream.flush();
        } catch (IOException e) {
            this.lastError = ("Exception writing binary object: " + e);
            System.err.println(this.lastError);
            return 7;
        }
        try {
            rc = this.inStream.readInt();
        } catch (Exception e) {
            this.lastError = ("mvxSetBlob got exception: " + e.getMessage());
            System.err.println(this.lastError);
            e.printStackTrace();
            return 7;
        }
        if (rc != 0) {
            this.lastError = "Something failed in server, check server error log";
            return 8;
        }

        return 0;
    }

    public byte[] mvxGetBlob() {
        int size;
        if (this.serverType == 2) {
            this.lastError = "Binary objects function not supported in the server";
            return null;
        }

        sendData("getblob        ");
        try {
            size = this.inStream.readInt();
        } catch (Exception e) {
            this.lastError = ("Read blob size exception: " + e.getMessage());
            System.err.println(this.lastError);
            e.printStackTrace();
            return null;
        }
        if (size == 0) {
            this.lastError = "No binary object available";
            return null;
        }

        byte[] buffer = new byte[size];
        try {
            this.inStream.readFully(buffer, 0, size);
        } catch (Exception e) {
            this.lastError = ("Read blob exception: " + e.getMessage());
            System.err.println(this.lastError);
            e.printStackTrace();
            return null;
        }

        return buffer;
    }

    private int sendData(String stringToSend) {
        if (this.theSocket == null) {
            this.lastError = "sendData() (primitive) called before socket was set up.";
            return 1;
        }

        try {
            if ((this.flags & 0x2) != 0) {
                if (this.DEBUG) {
                    System.out.print("Sending " + stringToSend.length() + " chars (" + stringToSend.length() * 2 + " bytes): ");

                    System.out.println("'" + stringToSend + "'");
                }

                if (this.zippedTransactions) {
                    int offs = 0;
                    for (int j = 0; j < stringToSend.length(); j++) {
                        char c = stringToSend.charAt(j);
                        this.zipDestBuffer[(offs++)] = (byte) (c >> '\b' & 0xFF);
                        this.zipDestBuffer[(offs++)] = (byte) (c & 0xFF);
                    }
                    this.deflater.setInput(this.zipDestBuffer, 0, offs);
                    this.deflater.finish();
                    int newSize = 0;
                    while (!this.deflater.finished()) {
                        int cSize = this.deflater.deflate(this.zipWorkBuffer);
                        if (cSize > 0) {
                            System.arraycopy(this.zipWorkBuffer, 0, this.recvByteBuf, newSize, cSize);
                            newSize += cSize;
                        }
                    }
                    this.deflater.reset();
                    this.outStream.writeInt(newSize);
                    this.outStream.write(this.recvByteBuf, 0, newSize);
                } else {
                    this.outStream.writeInt(stringToSend.length() * 2);
                    this.outStream.writeChars(stringToSend);
                }
            } else {
                if (this.DEBUG) {
                    System.out.print("Sending " + stringToSend.length());
                    System.out.println(" chars: '" + stringToSend + "'");
                }
                byte[] sendBytes;
                if (this.conversionTable != null) {
                    sendBytes = stringToSend.getBytes(this.conversionTable);
                } else {
                    sendBytes = stringToSend.getBytes();
                }
                this.outStream.writeInt(sendBytes.length);
                this.outStream.write(sendBytes, 0, sendBytes.length);
            }

            this.outStream.flush();
        } catch (IOException e) {
            this.lastError = ("sendData() (primitive) got exception " + e);
            System.err.println(this.lastError);
            return 2;
        }
        return 0;
    }

    private String ensureSize(String s, int len) {
        int l = s.length();
        if (l > len) {
            return null;
        }
        if (l < len) {
            return s + "                                ".substring(0, len - l);
        }
        return s;
    }

    protected void finalize() throws Throwable {
        if (this.theSocket != null) {
            mvxClose();
        }
        super.finalize();
    }

    public static abstract interface ITrustedConnection {

        public abstract boolean doTrustHandshake(DataInputStream paramDataInputStream, DataOutputStream paramDataOutputStream);
    }
}