package MvxAPI;

public class Crypto1 {

    public Crypto1() {
    }

    public java.lang.String ac(java.lang.String p_Key) {
        char a[] = {
            '\267', '\213', '\213', '\217', '\254', '\232', '\215', '\211', '\223', '\232',
            '\213', '\247'
        };
        for (int index = 0; index < a.length; index++) {
            a[index] = (char) (a[index] ^ 0xff);
        }

        return java.lang.String.valueOf(a);
    }

    public java.lang.String encryptFixed(java.lang.String p_String) {
        return encrypt(p_String, ac(p_String));
    }

    public java.lang.String decryptFixed(java.lang.String p_String) {
        return decrypt(p_String, ac(p_String));
    }

    public java.lang.String decrypt(java.lang.String p_String, java.lang.String p_Key) {
        if (p_String == null || p_Key == null) /*  67*/ {
            throw new NullPointerException("Crypto.decrypt(String, String) recieved a nullpointer String");
        }
        if (p_Key.length() == 0 && p_String.length() == 0) /*  74*/ {
            return p_String;
        }
        if (p_Key.length() == 0) /*  79*/ {
            throw new IllegalArgumentException("Crypto.decrypt(String, String) recieved an empty crypto key");
        }
        java.lang.StringBuffer decrypted = new StringBuffer();
        if (p_String.startsWith("##")) {
            java.lang.Integer versionTag = java.lang.Integer.valueOf(p_String.substring(2, 4));
            java.lang.StringBuffer str = new StringBuffer(p_String.substring(4));
            switch (versionTag.intValue()) {
                case 0: // '\0'
                    mvxCrypto.MvxDecrypt(str.length(), str, decrypted, ac(p_String).substring(0, 7));
                    break;

                case 1: // '\001'
                    mvxCrypto.MvxDecrypt(str.length(), str, decrypted, p_Key);
                    break;

                default:
                    throw new NullPointerException("Undefined encryption version tag, see application log.");
            }
        } else {
            java.lang.StringBuffer str = new StringBuffer(p_String);
            mvxCrypto.MvxDecrypt(str.length(), str, decrypted, p_Key.substring(0, 7));
        }
        return decrypted.toString();
    }

    public java.lang.String encrypt(java.lang.String p_String, java.lang.String p_Key) {

        if (p_String == null || p_Key == null) /* 130*/ {
            throw new NullPointerException("Crypto.encrypt(String, String) recieved a nullpointer String");
        }
        if (p_Key.length() == 0 && p_String.length() == 0) /* 137*/ {
            return p_String;
        }
        if (p_Key.length() == 0) {
            throw new IllegalArgumentException("Crypto.encrypt(String, String) recieved an empty crypto key");
        } else {
            java.lang.StringBuffer string = new StringBuffer();
            java.lang.StringBuffer encrypted = new StringBuffer();
            string.append(p_String);
            mvxCrypto.MvxEncrypt(string.length(), string, encrypted, p_Key);
            return (new StringBuilder()).append("##01").append(encrypted.toString()).toString();
        }
    }

    public java.lang.String compressKey(java.lang.String p_Key) {
        java.lang.StringBuffer comprKey = new StringBuffer();
        int keyLen = 0;
        int retainIndex = 0;
        int retainCtr = 0;
        int i = 0;
        double retainDble = 0.0D;
        double keyLenDble = 0.0D;
        double maxLenDble = 0.0D;
        keyLen = p_Key.length();
        if (keyLen <= mvxCrypto.MAXKEYLEN) /* 186*/ {
            return p_Key;
        }
        maxLenDble = mvxCrypto.MAXKEYLEN;
        keyLenDble = keyLen;
        retainDble = java.lang.Math.ceil(keyLenDble / maxLenDble);
        retainIndex = (int) retainDble;
        retainCtr = retainIndex;
        for (i = 0; i < keyLen; i++) /* 208*/ {
            if (retainCtr == retainIndex) {
                comprKey.append(p_Key.charAt(i));
                retainCtr = 1;
            } else {
                retainCtr++;
            }
        }
        return comprKey.toString();
    }
    private static MvxAPI.Crypto mvxCrypto = new MvxAPI.Crypto();
}
