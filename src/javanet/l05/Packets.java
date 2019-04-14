package javanet.l05;

import com.sun.istack.internal.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class Packets {
    private final static Charset charset = StandardCharsets.UTF_8;

    public static byte[] getBytes(String str) {
        return str.getBytes(charset);
    }

    public static String getString(byte[] bytes) {
        return new String(bytes, charset);
    }

    /**
     * @param data 收到的字节数组
     * @return 对应类型的数据, 可通过getClass获取<br>
     * 出现错误，返回null
     */
    @Nullable
    public static Object read(byte[] data) {
        String d = getString(data);
        if (d.startsWith("1")) return Reader.clientQuery(d);
        if (d.startsWith("2")) return Reader.busPosition(d);
        if (d.endsWith("\n\n")) Reader.busStopList(d);
        return null;
    }

    static class Builder {
        /**
         * @param bus_id 线路号
         * @return 查询请求
         */
        public static byte[] clientQuery(String bus_id) {
            return getBytes("1\n" + bus_id + "\n");
        }

        /**
         * @param bus 公交类实例
         * @return 查询结果（完整公交站点列表）
         */
        public static byte[] busStopList(Bus bus) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < bus.stops.size(); i++) {
                builder.append(bus.stops.get(i)).append("\n");
            }
            builder.append("\n");
            return getBytes(builder.toString());
        }

        /**
         * @param bus 公交类实例
         * @return 公交位置列表
         */
        public static byte[] busPositionList(Bus[] bus) {
            StringBuilder builder = new StringBuilder().append("2\n");
            for (Bus bus1 : bus) {
                builder.append(bus1.pos).append("\n");
            }
            builder.append("\n");
            return getBytes(builder.toString());
        }
    }

    static class Reader {
        /**
         * @param data 收到的数据
         * @return 被查询的线路号
         */
        public static String clientQuery(String data) {
            return data.substring(1).trim();
        }

        /**
         * @param data 收到的数据
         * @return 站点列表
         */
        public static LinkedList<String> busStopList(String data) {
            LinkedList<String> linkedList = new LinkedList<>();
            while (data.length() != 1) {
                int pos = data.indexOf("\n");
                linkedList.add(data.substring(0, pos));
                data = data.substring(pos + 1);
            }
            return linkedList;
        }

        /**
         * @param data 收到的数据
         * @return 公交所在位置
         */
        public static Integer busPosition(String data) {
            return Integer.parseInt(data.substring(1).trim());
        }
    }
}
