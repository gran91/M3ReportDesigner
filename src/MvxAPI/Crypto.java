package MvxAPI;

public class Crypto {

    public Crypto() {
        MAXKEYLEN = 7;
    }

    public int MvxEncrypt(int inLen, StringBuffer inStr, StringBuffer outStr, String key) {
        StringBuffer bufStr = new StringBuffer();
        StringBuffer keySB = new StringBuffer(key);
        Blowfish bf = new Blowfish();
        bf.iv = bf.byteArrayToLong(orgIvec, 0);
        if (keySB.length() > MAXKEYLEN) {
            keySB.setLength(MAXKEYLEN);
        }
        int bufLen = bf.BF_cfb64_encrypt(inStr, bufStr, inStr.length(), keySB, keySB.length(), bf.iv);
        Base64 base = new Base64();
        int outLen = base.MvxBase64encode(bufLen, bufStr, outStr);
        return outLen;
    }

    public int MvxDecrypt(int inLen, StringBuffer inStr, StringBuffer outStr, String key) {
        StringBuffer bufStr = new StringBuffer();
        StringBuffer keySB = new StringBuffer(key);
        Base64 base = new Base64();
        int bufLen = base.MvxBase64decode(inLen, inStr, bufStr);
        Blowfish bf = new Blowfish();
        bf.iv = bf.byteArrayToLong(orgIvec, 0);
        if (keySB.length() > MAXKEYLEN) {
            keySB.setLength(MAXKEYLEN);
        }
        int outLen = bf.BF_cfb64_decrypt(bufStr, outStr, bufLen, keySB, keySB.length(), bf.iv);
        return outLen;
    }

    public static void main(String args[]) {
        /*String key = new String("ABDBFC");
         StringBuffer in = new StringBuffer("Rock {[@\243$#\244%&/()=?+]} the boat really hard. -Let's test this stuff!");
         StringBuffer out = new StringBuffer();
         StringBuffer out2 = new StringBuffer();
         int len = in.length();
         System.out.println("1st: [" + len + "] [" + in + "]");
         int newLen = c.MvxEncrypt(len, in, out, key);
         System.out.println("2nd: [" + newLen + "] [" + out + "]");
         int newLen2 = c.MvxDecrypt(newLen, out, out2, key);
         System.out.println("3rd: [" + newLen2 + "] [" + out2 + "]");  */
        /*System.out.println("DECRYPT");
         Crypto c = new Crypto();
         String key = new String("ABDBFC");
         StringBuffer in = new StringBuffer("##01eQ0lUgoDinjkmGD5B6VWYQ==");
         StringBuffer out = new StringBuffer();
         c.MvxDecrypt(in.length(), in, out, key);
         System.out.println(out);
         */
        Crypto1 crypto = new Crypto1();
        String key = "yo0m93O52EZw3GppTFnYm6gl";
        String s = crypto.encryptFixed("Lawson123");
//String s = crypto.decrypt("##01TyEMei8t1iCjuVPKMIpkRQ==", key);
        System.out.println(s);
    }
    private byte orgIvec[] = {
        -2, -36, -70, -104, 118, 84, 50, 16
    };
    public int MAXKEYLEN;
    public static final String version = "$Revision: /main/DEV_12/SP_12.4.6/2 $";
}
