package MvxAPI;

public class Base64
{

    public Base64()
    {
        alphabet = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
    }

    private int base64c2i(char c)
    {
        switch(c)
        {
        case 65: // 'A'
            return 0;

        case 66: // 'B'
            return 1;

        case 67: // 'C'
            return 2;

        case 68: // 'D'
            return 3;

        case 69: // 'E'
            return 4;

        case 70: // 'F'
            return 5;

        case 71: // 'G'
            return 6;

        case 72: // 'H'
            return 7;

        case 73: // 'I'
            return 8;

        case 74: // 'J'
            return 9;

        case 75: // 'K'
            return 10;

        case 76: // 'L'
            return 11;

        case 77: // 'M'
            return 12;

        case 78: // 'N'
            return 13;

        case 79: // 'O'
            return 14;

        case 80: // 'P'
            return 15;

        case 81: // 'Q'
            return 16;

        case 82: // 'R'
            return 17;

        case 83: // 'S'
            return 18;

        case 84: // 'T'
            return 19;

        case 85: // 'U'
            return 20;

        case 86: // 'V'
            return 21;

        case 87: // 'W'
            return 22;

        case 88: // 'X'
            return 23;

        case 89: // 'Y'
            return 24;

        case 90: // 'Z'
            return 25;

        case 97: // 'a'
            return 26;

        case 98: // 'b'
            return 27;

        case 99: // 'c'
            return 28;

        case 100: // 'd'
            return 29;

        case 101: // 'e'
            return 30;

        case 102: // 'f'
            return 31;

        case 103: // 'g'
            return 32;

        case 104: // 'h'
            return 33;

        case 105: // 'i'
            return 34;

        case 106: // 'j'
            return 35;

        case 107: // 'k'
            return 36;

        case 108: // 'l'
            return 37;

        case 109: // 'm'
            return 38;

        case 110: // 'n'
            return 39;

        case 111: // 'o'
            return 40;

        case 112: // 'p'
            return 41;

        case 113: // 'q'
            return 42;

        case 114: // 'r'
            return 43;

        case 115: // 's'
            return 44;

        case 116: // 't'
            return 45;

        case 117: // 'u'
            return 46;

        case 118: // 'v'
            return 47;

        case 119: // 'w'
            return 48;

        case 120: // 'x'
            return 49;

        case 121: // 'y'
            return 50;

        case 122: // 'z'
            return 51;

        case 48: // '0'
            return 52;

        case 49: // '1'
            return 53;

        case 50: // '2'
            return 54;

        case 51: // '3'
            return 55;

        case 52: // '4'
            return 56;

        case 53: // '5'
            return 57;

        case 54: // '6'
            return 58;

        case 55: // '7'
            return 59;

        case 56: // '8'
            return 60;

        case 57: // '9'
            return 61;

        case 43: // '+'
            return 62;

        case 47: // '/'
            return 63;

        case 44: // ','
        case 45: // '-'
        case 46: // '.'
        case 58: // ':'
        case 59: // ';'
        case 60: // '<'
        case 61: // '='
        case 62: // '>'
        case 63: // '?'
        case 64: // '@'
        case 91: // '['
        case 92: // '\\'
        case 93: // ']'
        case 94: // '^'
        case 95: // '_'
        case 96: // '`'
        default:
            return -1;
        }
    }

    public int oldMvxBase64encode(int inLen, StringBuffer inStr, StringBuffer outStr)
    {
        int j = 0;
        StringBuffer inBuf = inStr;
        for(int i = 0; i < inLen;)
        {
            char c1 = inBuf.charAt(i);
            char c2;
            if(++i < inLen)
                c2 = inBuf.charAt(i);
            else
                c2 = '=';
            char c3;
            if(++i < inLen)
                c3 = inBuf.charAt(i);
            else
                c3 = '=';
            i++;
            int val1 = 0;
            int val2 = 0;
            int val3 = 0;
            int val4 = 0;
            val1 = (c1 & 0xfc) >> 2;
            val2 = ((c1 & 3) << 4) + ((c2 & 0xf0) >> 4);
            val3 = ((c2 & 0xf) << 2) + ((c3 & 0xc0) >> 6);
            val4 = c3 & 0x3f;
            outStr.insert(j++, alphabet.charAt(val1));
            outStr.insert(j++, alphabet.charAt(val2));
            outStr.insert(j++, alphabet.charAt(val3));
            outStr.insert(j++, alphabet.charAt(val4));
        }

        outStr.setLength(j);
        return j;
    }

    public int MvxBase64encode(int inLen, StringBuffer inStr, StringBuffer outStr)
    {
        int j = 0;
        StringBuffer inBuf = inStr;
        for(int i = 0; i < inLen;)
        {
            int val1;
            int val2;
            int val3;
            char c1;
            char c2;
            if(i + 2 >= inLen)
                if(i + 1 == inLen)
                {
                    c1 = inBuf.charAt(i);
                    i++;
                    val1 = (c1 & 0xfc) >> 2;
                    val2 = (c1 & 3) << 4;
                    outStr.insert(j++, alphabet.charAt(val1));
                    outStr.insert(j++, alphabet.charAt(val2));
                    outStr.insert(j++, '=');
                    outStr.insert(j++, '=');
                    outStr.setLength(j);
                    return j;
                } else
                {
                    c1 = inBuf.charAt(i);
                    i++;
                    c2 = inBuf.charAt(i);
                    i++;
                    val1 = (c1 & 0xfc) >> 2;
                    val2 = ((c1 & 3) << 4) + ((c2 & 0xf0) >> 4);
                    val3 = (c2 & 0xf) << 2;
                    outStr.insert(j++, alphabet.charAt(val1));
                    outStr.insert(j++, alphabet.charAt(val2));
                    outStr.insert(j++, alphabet.charAt(val3));
                    outStr.insert(j++, '=');
                    outStr.setLength(j);
                    return j;
                }
            c1 = inBuf.charAt(i);
            i++;
            c2 = inBuf.charAt(i);
            i++;
            char c3 = inBuf.charAt(i);
            i++;
            val1 = (c1 & 0xfc) >> 2;
            val2 = ((c1 & 3) << 4) + ((c2 & 0xf0) >> 4);
            val3 = ((c2 & 0xf) << 2) + ((c3 & 0xc0) >> 6);
            int val4 = c3 & 0x3f;
            outStr.insert(j++, alphabet.charAt(val1));
            outStr.insert(j++, alphabet.charAt(val2));
            outStr.insert(j++, alphabet.charAt(val3));
            outStr.insert(j++, alphabet.charAt(val4));
        }

        outStr.setLength(j);
        return j;
    }

    public int oldMvxBase64decode(int inLen, StringBuffer inStr, StringBuffer outStr)
    {
        if(inLen % 4 != 0)
            return -1;
        int j = 0;
        StringBuffer inBuf = inStr;
        for(int i = 0; i < inLen;)
        {
            char c1 = inBuf.charAt(i);
            i++;
            char c2 = inBuf.charAt(i);
            i++;
            char c3 = inBuf.charAt(i);
            i++;
            char c4 = inBuf.charAt(i);
            i++;
            int val1 = 0;
            int val2 = 0;
            int val3 = 0;
            int bc1;
            int bc2;
            int bc3;
            int bc4;
            if((bc1 = base64c2i(c1)) == -1 || (bc2 = base64c2i(c2)) == -1 || (bc3 = base64c2i(c3)) == -1 || (bc4 = base64c2i(c4)) == -1)
                return -2;
            val1 = (bc1 << 2) + (bc2 >> 4);
            val2 = ((bc2 & 0xf) << 4) + (bc3 >> 2);
            val3 = ((bc3 & 3) << 6) + bc4;
            outStr.insert(j++, (char)val1);
            outStr.insert(j++, (char)val2);
            outStr.insert(j++, (char)val3);
        }

        if(outStr.charAt(j - 1) == '=' && j % 3 == 0)
            j--;
        if(outStr.charAt(j - 1) == '=' && (j + 1) % 3 == 0)
            j--;
        outStr.setLength(j);
        return j;
    }

    public int MvxBase64decode(int inLen, StringBuffer inStr, StringBuffer outStr)
    {
        if(inLen % 4 != 0)
            return -1;
        int j = 0;
        StringBuffer inBuf = inStr;
        for(int i = 0; i < inLen;)
        {
            char c1 = inBuf.charAt(i);
            i++;
            char c2 = inBuf.charAt(i);
            i++;
            char c3 = inBuf.charAt(i);
            i++;
            char c4 = inBuf.charAt(i);
            i++;
            int bc1;
            int bc2;
            int bc3;
            int val1;
            int val2;
            if(c4 == '=')
            {
                if(c3 == '=')
                    if((bc1 = base64c2i(c1)) == -1 || (bc2 = base64c2i(c2)) == -1)
                    {
                        return -2;
                    } else
                    {
                        val1 = (bc1 << 2) + (bc2 >> 4);
                        outStr.insert(j++, (char)val1);
                        outStr.setLength(j);
                        return j;
                    }
                if((bc1 = base64c2i(c1)) == -1 || (bc2 = base64c2i(c2)) == -1 || (bc3 = base64c2i(c3)) == -1)
                {
                    return -2;
                } else
                {
                    val1 = (bc1 << 2) + (bc2 >> 4);
                    val2 = ((bc2 & 0xf) << 4) + (bc3 >> 2);
                    outStr.insert(j++, (char)val1);
                    outStr.insert(j++, (char)val2);
                    outStr.setLength(j);
                    return j;
                }
            }
            int bc4;
            if((bc1 = base64c2i(c1)) == -1 || (bc2 = base64c2i(c2)) == -1 || (bc3 = base64c2i(c3)) == -1 || (bc4 = base64c2i(c4)) == -1)
                return -2;
            val1 = (bc1 << 2) + (bc2 >> 4);
            val2 = ((bc2 & 0xf) << 4) + (bc3 >> 2);
            int val3 = ((bc3 & 3) << 6) + bc4;
            outStr.insert(j++, (char)val1);
            outStr.insert(j++, (char)val2);
            outStr.insert(j++, (char)val3);
        }

        outStr.setLength(j);
        return j;
    }

    public static void main(String args[])
    {
        StringBuffer inStr = new StringBuffer("Let's test this stuff!!");
        StringBuffer outStr = new StringBuffer();
        StringBuffer inStr2 = new StringBuffer();
        int inLen = inStr.length();
        int outLen = 0;
        Base64 b = new Base64();
        System.out.println("in   [" + inLen + "]  : " + inStr);
        outLen = b.MvxBase64encode(inLen, inStr, outStr);
        System.out.println("out  [" + outLen + "]  : " + outStr);
        outLen = b.MvxBase64decode(outLen, outStr, inStr2);
        System.out.println("out2 [" + outLen + "]  : " + inStr2);
    }

    private StringBuffer alphabet;
    public static final String version = "$Revision: /main/DEV_12/SP_12.4.6/2 $";
}
