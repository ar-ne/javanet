package javanet.l05;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;

public class Packets {
    private final static Charset charset = StandardCharsets.UTF_8;
    private final static String[] requestType = {
            "1",//线路查询
            "2",//位置查询
            "3"//位置汇报
    };

    public static byte[] getBytes(String str) {
        return str.getBytes(charset);
    }

    public static String getString(byte[] bytes) {
        return new String(bytes, charset);
    }

    static class Builder {
        /**
         * @param bus_id 线路号
         * @return 站点列表查询请求
         */
        public static byte[] listQuery(String bus_id) {
            return getBytes(requestType[0] + "\n" + bus_id + "\n");
        }

        /**
         * @param bus_id 线路号
         * @return 位置列表查询请求
         */
        public static byte[] positionQuery(String bus_id) {
            return getBytes(requestType[1] + "\n" + bus_id + "\n");
        }

        /**
         * @param bus 公交类实例
         * @return 包含线路号和位置的包
         */
        public static byte[] positionReport(Bus bus) {
            return getBytes(requestType[2] + "\n" + bus.id + "\n" + bus.pos + "\n");
        }

        /**
         * @param bus 公交类实例
         * @return 查询结果（完整公交站点列表）
         */
        public static byte[] busStopList(Bus bus) {
            StringBuilder builder = new StringBuilder();
            if (bus.stops == null) {
                return getBytes("\n");
            }
            for (int i = 0; i < bus.stops.size(); i++) {
                builder.append(bus.stops.get(i)).append("\n");
            }
            builder.append("\n");
            return getBytes(builder.toString());
        }

        /**
         * @param data 有公交停靠的站点列表
         * @return 公交位置列表
         */
        public static byte[] busPositionList(HashSet<Integer> data) {
            StringBuilder builder = new StringBuilder().append("2\n");
            for (Integer integer : data) {
                builder.append(integer).append("\n");
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

        public static Bus parsePositionReport(String data) {
            Bus bus = new Bus();
            data = data.substring(1).trim();
            //"id \n pos
            bus.id = data.substring(0, data.indexOf("\n")).trim();
            bus.pos = Integer.parseInt(data.substring(data.indexOf("\n") + 1).trim());
            return bus;
        }

        /**
         * @param data 收到的数据
         * @return 站点列表, 第一个元素是线路号
         */
        public static LinkedList<String> busStopList(String data) {
            LinkedList<String> linkedList = new LinkedList<>();
            while (data.charAt(0) != '\n') {
                int pos = data.indexOf("\n");
                linkedList.add(data.substring(0, pos));
                data = data.substring(pos + 1);
            }
            return linkedList;
        }

        /**
         * @param data 收到的数据byte[]
         * @return 公交所在位置
         */
        public static HashSet<Integer> busPosition(String data) {
            HashSet<Integer> set = new HashSet<>();
            data = data.substring(2);
            while (data.charAt(0) != '\n') {
                int pos = data.indexOf("\n");
                set.add(Integer.valueOf(data.substring(0, pos).trim()));
                data = data.substring(pos + 1);
            }
            return set;
        }
    }
}
