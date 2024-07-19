package camellia.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2024/7/15下午3:42
 * @author: Camellia.xioahua
 */

@Slf4j
public class ImportExcel {

    public static void main(String[] args) {
        //监听器
        //readByListener();
        //同步读
        synchronousRead();
    }

    /**
     * 创建监听器，
     */
    public static void readByListener(){
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "C:\\Users\\24211\\Desktop\\testExcel.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, LikeMindedUserInfo.class, new PageReadListener<LikeMindedUserInfo>(dataList -> {
            for (LikeMindedUserInfo demoData : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
            }
        })).sheet().doRead();
    }

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */

    public static void synchronousRead() {
        String fileName = "C:\\Users\\24211\\Desktop\\testExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<LikeMindedUserInfo> list = EasyExcel.read(fileName).head(LikeMindedUserInfo.class).sheet().doReadSync();
        for (LikeMindedUserInfo data : list) {
            log.info("读取到数据:{}", JSON.toJSONString(data));
        }
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
        for (Map<Integer, String> data : listMap) {
            // 返回每条数据的键值对 表示所在的列 和所在列的值
            log.info("读取到数据:{}", JSON.toJSONString(data));
        }
    }

}
