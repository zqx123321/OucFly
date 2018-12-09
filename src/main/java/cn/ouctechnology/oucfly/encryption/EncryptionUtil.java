package cn.ouctechnology.oucfly.encryption;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-05 20:58
 * @description: 参数加密工具类
 **/
public class EncryptionUtil {

    /**
     * 将字符串转化为des编码
     *
     * @param str 需要加密的String
     * @param key 密钥
     * @return des编码的String
     */
    private static String encodeDes(String str, String key) {
        int len = str.length();
        StringBuilder encData = new StringBuilder();
        List<int[]> keyBt;
        int length;
        if (key != null && !key.equals("")) {
            keyBt = getKeyBytes(key);
            length = keyBt.size();
            int iterator = (len / 4);
            int remainder = len % 4;
            for (int i = 0; i < iterator; i++) {
                String tempData = str.substring(i * 4, i * 4 + 4);
                int[] tempByte = strToBt(tempData);
                int[] encByte;
                int[] tempBt = tempByte;
                for (int x = 0; x < length; x++) {
                    tempBt = enc(tempBt, keyBt.get(x));
                }
                encByte = tempBt;
                encData.append(bt64ToHex(encByte));
            }
            if (remainder > 0) {
                String remainderData = str.substring(iterator * 4, len);
                int[] tempByte = strToBt(remainderData);
                int[] encByte;
                int[] tempBt = tempByte;
                for (int x = 0; x < length; x++) {
                    tempBt = enc(tempBt, keyBt.get(x));
                }
                encByte = tempBt;
                encData.append(bt64ToHex(encByte));
            }
        }
        return encData.toString();
    }

    /**
     * 将des编码的字符串进行解码
     *
     * @param str des编码的字符串
     * @param key 密钥
     * @return 解码后的字符串
     */
    private static String decodeDes(String str, String key) {
        int len = str.length();
        StringBuilder decStr = new StringBuilder();
        List<int[]> keyBt;
        int length;
        if (key != null && !key.equals("")) {
            keyBt = getKeyBytes(key);
            length = keyBt.size();
            int iterator = len / 16;
            for (int i = 0; i < iterator; i++) {
                String tempData = str.substring(i * 16, i * 16 + 16);
                String strByte = hexToBt64(tempData);
                int[] intByte = new int[64];
                for (int j = 0; j < 64; j++) {
                    intByte[j] = Integer.parseInt(strByte.substring(j, j + 1));
                }
                int[] decByte;
                int[] tempBt = intByte;
                for (int x = length - 1; x >= 0; x--) {
                    tempBt = dec(tempBt, keyBt.get(x));
                }
                decByte = tempBt;
                decStr.append(byteToString(decByte));
            }
        }
        return decStr.toString();
    }

    //chang the string into the bit array
    //return bit array(it's length % 64 = 0)
    private static List<int[]> getKeyBytes(String key) {
        List<int[]> keyBytes = new ArrayList<>();
        int len = key.length();
        int iterator = len / 4;
        int remainder = len % 4;
        int i;
        for (i = 0; i < iterator; i++) {
            keyBytes.add(i, strToBt(key.substring(i * 4, i * 4 + 4)));
        }
        if (remainder > 0) {
            keyBytes.add(i, strToBt(key.substring(i * 4, len)));
        }
        return keyBytes;
    }

    //chang the string(it's length <= 4) into the bit array
    //return bit array(it's length = 64)
    private static int[] strToBt(String str) {
        int len = str.length();
        int[] bt = new int[64];
        //计算str的前4个字符
        for (int i = 0; i < len && i < 4; i++) {
            int k = str.charAt(i);
            fullBit(bt, i, k);
        }
        // len小于4时补0计算
        for (int p = len; p < 4; p++) {
            int k = 0;
            fullBit(bt, p, k);
        }
        return bt;
    }

    private static void fullBit(int[] bt, int p, int k) {
        for (int q = 0; q < 16; q++) {
            int pow = 1;
            for (int m = 15; m > q; m--) {
                pow *= 2;
            }
            bt[16 * p + q] = (k / pow) % 2;
        }
    }

    private static String[] binaryArray = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};

    // 二进制转十六进制
    private static String bt4ToHex(String binary) {
        String hex = "";
        try {
            int i = Integer.parseInt(binary, 2);
            if (i > -1 && i < 16) {
                hex = Integer.toHexString(i);
            }
        } catch (Exception ignored) {
        }
        return hex.toUpperCase();
    }

    // 十进制转二进制
    private static String getBoxBinary(int i) {
        String binary = "";
        if (i > -1 && i < 16) {
            binary = binaryArray[i];
        }
        return binary;
    }

    // 十六进制转二进制
    private static String hexToBt4(String hex) {
        String binary = "";
        try {
            int i = Integer.parseInt(hex, 16);
            if (i > -1 && i < 16) {
                binary = binaryArray[i];
            }
        } catch (Exception ignored) {
        }
        return binary;
    }


    private static String byteToString(int[] byteData) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int count = 0;
            for (int j = 0; j < 16; j++) {
                int pow = 1;
                for (int m = 15; m > j; m--) {
                    pow *= 2;
                }
                count += byteData[16 * i + j] * pow;
            }
            if (count != 0) {
                str.append((char) (count));
            }
        }
        return str.toString();
    }

    private static String bt64ToHex(int[] byteData) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            StringBuilder bt = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                bt.append(byteData[i * 4 + j]);
            }
            hex.append(bt4ToHex(bt.toString()));
        }
        return hex.toString();
    }

    private static String hexToBt64(String hex) {
        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            binary.append(hexToBt4(hex.substring(i, i + 1)));
        }
        return binary.toString();
    }

    private static int[] dec(int[] dataByte, int[] keyByte) {
        int[] index = new int[16];
        for (int i = 0; i <= 15; i++) {
            index[i] = 15 - i;
        }
        return transferData(dataByte, keyByte, index);
    }

    private static int[] enc(int[] dataByte, int[] keyByte) {
        int[] index = new int[16];
        for (int i = 0; i <= 15; i++) {
            index[i] = i;
        }
        return transferData(dataByte, keyByte, index);
    }

    private static int[] transferData(int[] dataByte, int[] keyByte, int[] index) {
        int[][] keys = generateKeys(keyByte);
        int[] ipByte = initPermute(dataByte);
        int[] ipLeft = new int[32];
        int[] ipRight = new int[32];
        int[] tempLeft = new int[32];
        for (int k = 0; k < 32; k++) {
            ipLeft[k] = ipByte[k];
            ipRight[k] = ipByte[32 + k];
        }
        for (int i : index) {
            for (int j = 0; j < 32; j++) {
                tempLeft[j] = ipLeft[j];
                ipLeft[j] = ipRight[j];
            }
            int[] key = new int[48];
            System.arraycopy(keys[i], 0, key, 0, 48);
            int[] tempRight = xor(pPermute(sBoxPermute(xor(expandPermute(ipRight), key))), tempLeft);
            System.arraycopy(tempRight, 0, ipRight, 0, 32);
        }
        int[] finalData = new int[64];
        for (int i = 0; i < 32; i++) {
            finalData[i] = ipRight[i];
            finalData[32 + i] = ipLeft[i];
        }
        return finallyPermute(finalData);
    }

    private static int[] initPermute(int[] originalData) {
        int[] ipByte = new int[64];
        int i, m, n, j, k;
        for (i = 0, m = 1, n = 0; i < 4; i++, m += 2, n += 2) {
            for (j = 7, k = 0; j >= 0; j--, k++) {
                ipByte[i * 8 + k] = originalData[j * 8 + m];
                ipByte[i * 8 + k + 32] = originalData[j * 8 + n];
            }
        }
        return ipByte;
    }

    private static int[] expandPermute(int[] rightData) {
        int[] epByte = new int[48];
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                epByte[i * 6] = rightData[31];
            } else {
                epByte[i * 6] = rightData[i * 4 - 1];
            }
            epByte[i * 6 + 1] = rightData[i * 4];
            epByte[i * 6 + 2] = rightData[i * 4 + 1];
            epByte[i * 6 + 3] = rightData[i * 4 + 2];
            epByte[i * 6 + 4] = rightData[i * 4 + 3];
            if (i == 7) {
                epByte[i * 6 + 5] = rightData[0];
            } else {
                epByte[i * 6 + 5] = rightData[i * 4 + 4];
            }
        }
        return epByte;
    }

    private static int[] xor(int[] byteOne, int[] byteTwo) {
        int[] xorByte = new int[byteOne.length];
        for (int i = 0; i < byteOne.length; i++) {
            xorByte[i] = byteOne[i] ^ byteTwo[i];
        }
        return xorByte;
    }

    private static int[] sBoxPermute(int[] expandByte) {
        int[] sBoxByte = new int[32];
        String binary = "";
        int[][] s1 =
                {
                        {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                        {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                        {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                        {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
                };
        int[][] s2 =
                {
                        {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                        {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                        {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                        {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
                };
        int[][] s3 =
                {
                        {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                        {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                        {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                        {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
                };
        int[][] s4 =
                {
                        {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                        {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                        {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                        {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
                };
        int[][] s5 =
                {
                        {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                        {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                        {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                        {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
                };
        int[][] s6 =
                {
                        {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                        {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                        {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                        {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
                };
        int[][] s7 =
                {
                        {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                        {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                        {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                        {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
                };
        int[][] s8 =
                {
                        {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                        {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                        {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                        {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
                };
        for (int m = 0; m < 8; m++) {
            int i = expandByte[m * 6] * 2 + expandByte[m * 6 + 5];
            int j = expandByte[m * 6 + 1] * 2 * 2 * 2 + expandByte[m * 6 + 2] * 2 * 2 + expandByte[m * 6 + 3] * 2 + expandByte[m * 6 + 4];
            switch (m) {
                case 0:
                    binary = getBoxBinary(s1[i][j]);
                    break;
                case 1:
                    binary = getBoxBinary(s2[i][j]);
                    break;
                case 2:
                    binary = getBoxBinary(s3[i][j]);
                    break;
                case 3:
                    binary = getBoxBinary(s4[i][j]);
                    break;
                case 4:
                    binary = getBoxBinary(s5[i][j]);
                    break;
                case 5:
                    binary = getBoxBinary(s6[i][j]);
                    break;
                case 6:
                    binary = getBoxBinary(s7[i][j]);
                    break;
                case 7:
                    binary = getBoxBinary(s8[i][j]);
                    break;
            }
            sBoxByte[m * 4] = Integer.parseInt(binary.substring(0, 1));
            sBoxByte[m * 4 + 1] = Integer.parseInt(binary.substring(1, 2));
            sBoxByte[m * 4 + 2] = Integer.parseInt(binary.substring(2, 3));
            sBoxByte[m * 4 + 3] = Integer.parseInt(binary.substring(3, 4));
        }
        return sBoxByte;
    }

    private static int[] pPermute(int[] e) {
        return new int[]{//e = sBoxByte
                e[15], e[6], e[19], e[20], e[28], e[11], e[27], e[16], e[0], e[14], e[22], e[25], e[4], e[17], e[30], e[9],
                e[1], e[7], e[23], e[13], e[31], e[26], e[2], e[8], e[18], e[12], e[29], e[5], e[21], e[10], e[3], e[24]
        };
    }

    private static int[] finallyPermute(int[] e) {
        return new int[]{//e = endByte
                e[39], e[7], e[47], e[15], e[55], e[23], e[63], e[31], e[38], e[6], e[46], e[14], e[54], e[22], e[62], e[30],
                e[37], e[5], e[45], e[13], e[53], e[21], e[61], e[29], e[36], e[4], e[44], e[12], e[52], e[20], e[60], e[28],
                e[35], e[3], e[43], e[11], e[51], e[19], e[59], e[27], e[34], e[2], e[42], e[10], e[50], e[18], e[58], e[26],
                e[33], e[1], e[41], e[9], e[49], e[17], e[57], e[25], e[32], e[0], e[40], e[8], e[48], e[16], e[56], e[24]
        };
    }

    //generate 16 keys for xor
    private static int[][] generateKeys(int[] keyByte) {
        int[] e = new int[56];
        int[][] keys = new int[16][48];
        int[] loop = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        for (int i = 0; i < 7; i++) {
            for (int j = 0, k = 7; j < 8; j++, k--) {
                e[i * 8 + j] = keyByte[8 * k + i];
            }
        }
        for (int i = 0; i < 16; i++) {
            int tempLeft;
            int tempRight;
            for (int j = 0; j < loop[i]; j++) {
                tempLeft = e[0];
                tempRight = e[28];
                for (int k = 0; k < 27; k++) {
                    e[k] = e[k + 1];
                    e[28 + k] = e[29 + k];
                }
                e[27] = tempLeft;
                e[55] = tempRight;
            }
            int[] t = {
                    e[13], e[16], e[10], e[23], e[0], e[4], e[2], e[27], e[14], e[5], e[20], e[9], e[22], e[18], e[11], e[3],
                    e[25], e[7], e[15], e[6], e[26], e[19], e[12], e[1], e[40], e[51], e[30], e[36], e[46], e[54], e[29], e[39],
                    e[50], e[44], e[32], e[47], e[43], e[48], e[38], e[55], e[33], e[52], e[45], e[41], e[49], e[35], e[28], e[31]
            };
            System.arraycopy(t, 0, keys[i], 0, 48);
        }
        return keys;
    }


    /**
     * 将字符串转化为base64编码
     *
     * @param str 需要加密的String
     */
    public static String encodeBase64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * 将base64编码的字符串进行解码
     *
     * @param str base64编码的字符串
     */
    public static String decodeBase64(String str) {
        byte[] bytes = Base64.getDecoder().decode(str);
        return new String(bytes);
    }

    /**
     * 得到字符串的MD5值
     *
     * @param OrigString 需要转换的字符串
     * @return MD5值
     */
    private static String encodeMD5(String OrigString) {
        MessageDigest md5Digest = DigestUtils.getMd5Digest();
        md5Digest.update(OrigString.getBytes());
        return byte2hex(md5Digest.digest()).toLowerCase();
    }

    private static String byte2hex(byte byteString[]) {
        StringBuilder result = new StringBuilder();
        String tmpChar;
        for (byte aByteString : byteString) {
            tmpChar = Integer.toHexString(aByteString & 255);
            if (tmpChar.length() == 1)
                result.append("0").append(tmpChar);
            else
                result.append(tmpChar);
        }
        return result.toString().toUpperCase();
    }

    public static String getEncParams(String params, String timeStamp, String desKey) {
        String token = getTripleMD5(params, timeStamp);
        params = encodeBase64(encodeDes(params, desKey));
        params = "params=" + params + "&token=" + token + "&timestamp=" + timeStamp;
        return params;
    }

    public static String getTripleMD5(String str1, String str2) {
        return encodeMD5(encodeMD5(str1) + encodeMD5(str2));
    }
}
