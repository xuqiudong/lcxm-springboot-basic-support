package cn.xuqiudong.generator;

import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.generator.demo.entity.Generate;
import cn.xuqiudong.generator.demo.mapper.GenerateMapper;
import cn.xuqiudong.generator.demo.query.GenerateQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-10-27 17:27
 */
@SpringBootTest
public class GeneratorDemoTest {

    @Resource
    private GenerateMapper generateMapper;

    @Test
    public void list() {
        List<Generate> generates = generateMapper.selectList(null);
        generates.forEach(System.out::println);
    }

    @Test
    public void insert() {
        Generate generate = new Generate();
        generate.setName("测试3");
        generate.setBirthday(LocalDate.now());
        generate.setNote("测试3");
        generate.setType("测试3");
        generate.setArticle("测试33");
        int insert = generateMapper.insert(generate);
        System.out.println(insert);
    }


    private static final String ID = "1982747353729523713";

    @Test
    public void detail() {
        Generate generate = generateMapper.selectById(ID);
        System.out.println(generate);
        generateMapper.updateEnable(ID, false);
        Generate generate1 = generateMapper.selectByIdWithLob(ID);
        System.out.println(generate1);
    }

    @Test
    public void save() {
        Generate generate = new Generate();
        generate.setName("测试save");
        generate.setBirthday(LocalDate.now());
        generate.setNote("测试save");
        generate.setType("测试save");
        generate.setArticle("测试save");
        int save = generateMapper.save(generate);
        System.out.println(save + " id = " + generate.getId());
        generate.setName("测试save->update");
        int save1 = generateMapper.save(generate);
        System.out.println(save1 + " id = " + generate.getId());
    }

    @Test
    public void insertBatch() {
        Generate generate = new Generate();
        generate.setName("测试insertBatch");
        generate.setBirthday(LocalDate.now());
        generate.setNote("测试insertBatch");
        generate.setType("测试insertBatch");
        generate.setArticle("测试insertBatch");

        Generate generate1 = new Generate();
        generate1.setName("测试insertBatch1");
        generate1.setBirthday(LocalDate.now());
        generate1.setNote("测试insertBatch1");
        generate1.setType("测试insertBatch1");
        generate1.setArticle("测试insertBatch1");
        int insertBatch = generateMapper.insertBatch(List.of(generate, generate1));
        System.out.println(insertBatch);

    }

    @Test
    public void saveBatch() {
        Generate generate = new Generate();
        generate.setId(ID);
        generate.setName("测试saveBatch2");
        generate.setBirthday(LocalDate.now());
        generate.setNote("测试saveBatch2");
        generate.setType("测试saveBatch2");
        generate.setArticle("测试saveBatch2");

        Generate generate1 = new Generate();
        generate1.setName("测试saveBatch33");
        generate1.setBirthday(LocalDate.now());
        generate1.setNote("测试saveBatch33");
        generate1.setType("测试saveBatch33");
        generate1.setArticle("测试saveBatch33");
        int saveBatch = generateMapper.saveBatch(List.of(generate, generate1));
        System.out.println(saveBatch);

    }

    @Test
    public void page() {
        Page<Generate> page = new Page<>(2, 5);
        QueryWrapper<Generate> wrapper = Wrappers.query();
        page.setOrders(List.of(OrderItem.asc("name"), OrderItem.asc("id")));
        wrapper.orderByDesc("type");
        wrapper.orderByDesc("version");
        wrapper.like("name", "测试");
        IPage<Generate> generateIPage = generateMapper.selectPage(page, wrapper);

        JsonUtil.printJson(generateIPage);
    }

    @Test
    public void selectListByQuery() {
        GenerateQuery query = new GenerateQuery();
        query.setType("a,b,c");
        query.setTagName("demo");
        LocalDateTime startTime = LocalDateTime.of(2025, 10, 27, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 10, 31, 23, 59);
        query.setStartTime(startTime);
//        query.setEndTime(endTime);
        List<Generate> list = generateMapper.selectListByQuery(query);
        for (Generate generate : list) {
            System.out.println("name:" + generate.getName() + " type:" + generate.getType() + " tagName:" + generate.getTagName()
                    + " startTime:" + generate.getStartTime());
        }
    }

    @Test
    public void check(){
        boolean check = generateMapper.isValueAvailable(ID, "测试3", "name");
        System.out.println(check);
        boolean check1 = generateMapper.isValueAvailable(null, "测试3", "name");
        System.out.println(check1);
    }

}
