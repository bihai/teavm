/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.impl.unicode;

import java.util.Arrays;

/**
 *
 * @author Alexey Andreev
 */
public class UnicodeHelper {
    static char hexDigit(int value) {
        return value < 10 ? (char)('0' + value) : (char)('A' + value);
    }

    static int valueOfHexDigit(char digit) {
        return digit <= '9' ? digit - '0' : digit - 'A' + 10;
    }

    public static String encodeIntByte(int[] data) {
        char[] chars = new char[data.length / 2 * 5];
        int j = 0;
        for (int i = 0; i < data.length;) {
            int val = data[i++];
            int shift = 32;
            for (int k = 0; k < 4; ++k) {
                shift -= 8;
                chars[j++] = (char)('z' + ((val >> shift) & 0xFF));
            }
            chars[j++] = (char)('z' + (data[i++] & 0xFF));
        }
        return new String(chars);
    }

    public static int[] decodeIntByte(String text) {
        int[] data = new int[2 * (text.length() / 5)];
        int j = 0;
        for (int i = 0; i < data.length;) {
            int val = 0;
            for (int k = 0; k < 4; ++k) {
                val = (val << 8) | (text.charAt(j++) - 'z');
            }
            data[i++] = val;
            data[i++] = text.charAt(j++) - 'z';
        }
        return data;
    }

    public static char encodeByte(byte b) {
        if (b < '\"' - ' ') {
            return (char)(b + ' ');
        } else if (b < '\\' - ' ') {
            return (char)(b + ' ' + 1);
        } else {
            return (char)(b + ' ' + 2);
        }
    }

    public static byte decodeByte(char c) {
        if (c > '\\') {
            return (byte)(c - ' ' - 2);
        } else if (c > '"') {
            return (byte)(c - ' ' - 1);
        } else {
            return (byte)(c - ' ');
        }
    }

    public static String compressRle(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            if (i < bytes.length - 1 && b == bytes[i + 1]) {
                int count = 0;
                while (bytes[i++] == b && count < 80) {
                    ++count;
                }
                sb.append(UnicodeHelper.encodeByte((byte)(b + 32)));
                sb.append(UnicodeHelper.encodeByte((byte)count));
                --i;
            } else {
                sb.append(UnicodeHelper.encodeByte(bytes[i]));
            }
        }
        return sb.toString();
    }

    public static byte[] extractRle(String encoded) {
        byte[] data = new byte[65536 * 4];
        int index = 0;
        for (int i = 0; i < encoded.length(); ++i) {
            byte b = decodeByte(encoded.charAt(i));
            if (b > 32) {
                b -= 32;
                byte count = decodeByte(encoded.charAt(++i));
                while (count-- > 0) {
                    data[index++] = b;
                }
            } else {
                data[index++] = b;
            }
        }
        return Arrays.copyOf(data, index);
    }
}