package com.nuonuo.help;

import com.nuonuo.entity.*;
import com.nuonuo.tool.ESTool;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.nuonuo.tool.StringUtils.addZero;

/**
 * Created by zhangbl on 2017-03-13.
 */
public class AnalyzerHelper implements Serializable {

    private static final Logger logger = LogManager.getLogger(AnalyzerHelper.class.getName());
    private static final int EXPIRE_TIME = 60 * 60 * 12;//redis缓存过期时间12h

    //标准信息库
    private static Map<String, StandardInfo> standardInfoMap = new ConcurrentHashMap<>();
    //征收品目信息库
    private static Map<String, LevyInfo> levyInfoMap = new ConcurrentHashMap<>();

    private volatile static AnalyzerHelper instance = new AnalyzerHelper();

    private AnalyzerHelper() {
    }

    public static synchronized AnalyzerHelper getInstance() {
        if (null == instance) {
            instance = new AnalyzerHelper();
        }
        return instance;
    }

    /**
     * 查询对应的商品税收分类编码
     *
     * @param name       商品名称
     * @param type       商品型号规格
     * @param category   商品税收大类
     * @param preference 优先权（保证一个用户查询结果一致）
     * @return {@link Product}实体类
     */
    public Product getProductCode(String name, String type, String category, String preference) {
        Product result = null;

        String key = "code_" + name + "_" + category + "_" + type;//redis key
//        result = (Product) RedisClusterUtil.getObjectByKey(key);
        if (null != result) {//从redis缓存中读取到值，直接返回
            logger.debug("从redis缓存中查到了key为" + key + "的值");
            return result;
        }
        //去es中进行查询
        result = getCodeFromAll(name, type, category, preference);
        //存入redis缓存
//        if (null != result && !"".equals(result.getName())) {
//            RedisClusterUtil.setKeyObjectValue(key, result, EXPIRE_TIME);
//        }

        return result;
    }

    /**
     * 查询对应的商品税收分类编码
     *
     * @param name       商品名称
     * @param type       商品型号规格
     * @param category   商品税收大类
     * @param preference 优先权（保证一个用户查询结果一致）
     * @return {@link Product}实体类
     */
    public ProductOP getProductCodeDK(String name, String type, String category, String preference) {
        ProductOP result = null;

        String key = "code_dk_" + name + "_" + category + "_" + type;//redis key
//        result = (ProductOP) RedisClusterUtil.getObjectByKey(key);
        if (null != result) {//从redis缓存中读取到值，直接返回
            logger.debug("从redis缓存中查到了key为" + key + "的值");
            return result;
        }
        //去es中进行查询
        Product product = getCodeFromAll(name, type, category, preference);
        result = addLevyInfo(product);

        //存入redis缓存
//        if (null != result && !"".equals(result.getName())) {
//            RedisClusterUtil.setKeyObjectValue(key, result, EXPIRE_TIME);
//        }

        return result;
    }

    /**
     * 添加征收代码和名称
     *
     * @param p
     * @return
     */
    private ProductOP addLevyInfo(Product p) {
        if (null == p || p.getTaxCode() == null) {
            ProductOP r = new ProductOP();
            r.checkNull();
            return r;
        }
        final String taxCode = p.getTaxCode();
        ProductOP r = new ProductOP(p);

        LevyInfo info = levyInfoMap.get(taxCode);
        if (info != null) {
            r.addLevyInfo(info);
            return r;
        }

        Client client = ESTool.getClient();
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("l_tax_code", com.nuonuo.tool.StringUtils.addZero(taxCode)));

        long start = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch("levy").setTypes("icode")
                .setQuery(qb)
                .setFrom(0).setSize(1)
                .execute().actionGet();
        long end = System.currentTimeMillis();
        logger.debug("从增值税征收品目最终版中进行查询耗时：" + (end - start) + "ms");
        if (response.getHits().getTotalHits() <= 0) {
            r.checkNull();
            return r;
        } else {
            //加载到standardInfoMap
            info = JSON.parseObject(response.getHits().getAt(0).getSourceAsString(), LevyInfo.class);
            r.addLevyInfo(info);
            levyInfoMap.put(taxCode, info);
        }
        return r;
    }

    /**
     * 没有查到认证的，对全量表进行查询
     *
     * @param name       商品名称
     * @param type       商品型号规格
     * @param category   商品税收大类
     * @param preference 优先权（保证一个用户查询结果一致）
     * @return
     */
    private Product getCodeFromAll(String name, String type, String category, String preference) {

        Client client = ESTool.getClient();
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("u_name", name))
                .should(QueryBuilders.matchQuery("u_flag", "1"));
        long start = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch("user").setTypes("user_code")
                .addSort(SortBuilders.scoreSort())
                .setQuery(qb)
                .setFrom(0).setSize(20)
                .setPreference(preference)
                .execute().actionGet();
        long end = System.currentTimeMillis();
        logger.debug("对" + name + "  " + type + "   " + category + "进行全量查询耗时" + (end - start) + "ms");
        if (response.getHits().getTotalHits() <= 0) {
            return null;
        }
        //进行最优匹配
        List<Product> rs = new ArrayList<Product>();
        for (SearchHit hit : response.getHits()) {
            Product product = JSON.parseObject(hit.getSourceAsString(), Product.class);
            product.setScore(hit.getScore());
            //需求 #28224 智能编码的推荐阈值设置为3 modified by zhangbl 20170517
            if ((null == product.getRecommendFlag() || product.getRecommendFlag().equals(""))
                    && product.getTaxCode().startsWith("1010101") && product.getChoiceCount() <= 3) {
                continue;
            }
            if (product.getTaxCode().equals("")) {
                continue;
            }
            rs.add(product);
        }
        return reScoreAndSort(rs, name, category, type);
    }


    /**
     * 重新计算得分（根据自定义规则）
     *
     * @param rs       es返回的结果集
     * @param name     商品名称（用户输入）
     * @param category 商品大类（用户输入）
     * @param type     商品类型（用户输入）
     * @return
     */
    private Product reScoreAndSort(List<Product> rs, String name, String category, String type) {
        for (Product p : rs) {
            float reScore = getCScore(p.getName(), name, p.getChoicePercent(), p.getRecommendFlag());
            p.setReScore(reScore);
        }
        //对自定义分数进行排序，如果分数一样的话，字符数少的优先级高
        Collections.sort(rs, new ProductComparator());

        Product p = null;
//        if (rs.size() > 0) {
//            p = rs.get(0);
//            //#39335 智能编码-编码检测，认证库中不存在的商品，检测后返回仍是已认证
//            //期望结果：若认证库相同商品名称不存在的，则返回的认证标识u_flag=0
//            //modified by zhangbl 2017-11-07
//            if (null != name && !name.equals(p.getName())) {
//                p.setRecommendFlag("0");
//            }
//        } else {
//            p = new Product();
//        }
//        p.checkNull();
//        addStandardInfo(p);
//        return p;


        Iterator<Product> iterator = rs.iterator();
        while (iterator.hasNext()) {
            p = iterator.next();
            if (null != name && !name.equals(p.getName())) {
                p.setRecommendFlag("0");
            }
            p.checkNull();
            addStandardInfo(p);
            if (null != p && null != p.getTaxCode() && !p.getTaxCode().equals("") && null != p.getName() && !"".equals(p.getName())) {
                break;
            }
        }
        return p;
    }

    /**
     * 计算自定义分数（优化版）
     *
     * @param s1   商品名称（用户输入）
     * @param s2   商品名称（es匹配返回）
     * @param cp   用户选择比
     * @param flag 认证标识
     * @return
     */
    public float getCScore(String s1, String s2, String cp, String flag) {
        float score = 0;
        float nameBoost = 65;
        float min = com.nuonuo.tool.StringUtils.minEditDistance(s1, s2);
        //最短编辑距离来计算商品名称比重 65%
        score += (1 - (s1.length() > s2.length() ? min / s1.length() : min / s2.length())) * nameBoost;
        //用户选择占比比重 15%
        score += cp == null ? 0 : Float.valueOf(cp) * 15;
        //对认证进行分数加成，比重 20%
        if (StringUtils.isNotEmpty(flag) && flag.equals("1")) {
            score += 20;
        }
        return score;
    }

    /**
     * 自己计算百分比分数
     * 商品名称比重65%，商品大类10%，商品型号10%，推荐标识15%
     *
     * @param s1            es返回的最佳结果的商品名称
     * @param s2            用户输入的商品名称
     * @param c1            es返回的最佳结果的商品大类
     * @param c2            用户输入的商品型号
     * @param t1            es返回的最佳结果的商品型号
     * @param t2            用户输入的商品型号
     * @param recommendFlag 推荐标识
     * @return
     */
    @Deprecated
    public float getCustomScore(String s1, String s2, String c1, String c2,
                                String t1, String t2, String recommendFlag) {
        float score = 0;
        float nameBoost = 65;
        if (StringUtils.isEmpty(c2)) {
            nameBoost += 10;
        }
        if (StringUtils.isEmpty(t2)) {
            nameBoost += 10;
        }
        //先计算商品名称得分
        if (s1.equals(s2)) {
            score = nameBoost;
        } else {
            float i = 0;//代表s1和s2有几个字符相同
            for (char c : s2.toCharArray()) {
                if (s1.toLowerCase().contains(new Character(c).toString().toLowerCase())) {
                    i++;
                    if (i >= s1.length()) {
                        break;
                    }
                }
            }
            if (s1.length() > s2.length()) {
                score = i / s1.length() * nameBoost;
            } else {
                score = i / s2.length() * nameBoost;
            }
        }
        //进行商品大类得分计算，比重10%
        if (StringUtils.isNotEmpty(c1) && StringUtils.isNotEmpty(c2) && c1.equals(c2)) {
            score += 10;
        }
        //对商品型号得分进行计算，比重10%
        if (StringUtils.isNotEmpty(t1) && StringUtils.isNotEmpty(t2) && t1.equals(t2)) {
            score += 10;
        }
        //对认证进行分数加成，比重15%
        if (StringUtils.isNotEmpty(recommendFlag) && recommendFlag.equals("1")) {
            score += 15;
        }
        //超过95分，全部都按照95分算
        if (score > 95) {
            score = 95;
        }
        return score;
    }


    /**
     * 增加国家标准库的信息(如免税政策,小规模纳税人税率等)
     *
     * @param p {@link Product} 商品实体类
     */
    private void addStandardInfo(Product p) {
        String code = p.getTaxCode();
        if (null == code || "".equals(code)) {
            return;
        }

        StandardInfo info = standardInfoMap.get(code);
        if (null == info) {
            Client client = ESTool.getClient();

            //从国家标准编码库中进行查询
            //过滤掉汇总项而且必须是可用的
            QueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("i_code", addZero(code)))
                    .must(QueryBuilders.matchQuery("i_flag", "N"))
                    .must(QueryBuilders.matchQuery("i_use", "Y"));

            long start = System.currentTimeMillis();
            SearchResponse response = client.prepareSearch("licode").setTypes("icode")
                    .setQuery(qb)
                    .setFrom(0).setSize(1)
                    .execute().actionGet();
            long end = System.currentTimeMillis();
            logger.debug("从国家标准编码库中进行查询耗时：" + (end - start) + "ms");
            if (response.getHits().getTotalHits() <= 0) {
                //从国家编码库查询不到该code，说明该code非法，返回空，客户端手动获取
                p.reset();
                return;
            } else {
                //加载到standardInfoMap
                info = JSON.parseObject(response.getHits().getAt(0).getSourceAsString(), StandardInfo.class);
                standardInfoMap.put(code, info);
            }
        }

        if (null != info.getSpecialManagement() && info.getSpecialManagement().contains("稀土产品")) {//稀土产品够国家管控，该编码不能使用
            p.reset();
        } else {
            String rate = info.getRate();
            if (!rate.equals("") && rate.contains("、")) {//如果有多个税率可选择，返回左手边第一个
                p.setRate(rate.split("、")[0]);
            } else if (!rate.equals("")) {
                p.setRate(rate);
            }
            p.setName(info.getName());
            p.setSmallRate(info.getSmallRate());
            p.setPolicy(info.getPolicy());
            p.setSpecialManagement(info.getSpecialManagement());
            p.setPolicyNum(info.getPolicyNum());
            p.setRecommendScore(new DecimalFormat("##").format(p.getReScore()) + "%");
        }
    }

    /**
     * 从ES中获取商品名称的相关项
     *
     * @param name       商品名称
     * @param preference 税号
     * @return
     */
    public List<Product> getProductByNameFromES(String name, String preference) {
        Client client = ESTool.getClient();

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("u_name", name));

        long start = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch("product").setTypes("name")
                .addSort(SortBuilders.scoreSort())
                .setQuery(qb)
                .setFrom(0).setSize(20)
                .setPreference(preference)
                .execute().actionGet();
        long end = System.currentTimeMillis();
        logger.debug("根据" + name + "获取相关关键字耗时：" + (end - start) + "ms");

        List<Product> list = new ArrayList<>();
        for (SearchHit hit :
                response.getHits()) {
            Product p = JSON.parseObject(hit.getSourceAsString(), Product.class);
            p.setScore(hit.getScore());
            list.add(p);
        }

        for (Product p : list) {
            float reScore = getCScore(p.getName(), name, p.getChoicePercent(), p.getRecommendFlag());
            p.setReScore(reScore);
        }

        //对自定义分数进行排序，如果分数一样的话，字符数少的优先级高
        Collections.sort(list, new ProductComparator());

        //取出前5
        List<Product> result = new ArrayList<>();
        int count = 0;
        for (Product p : list) {
            if (count >= 5) {
                break;
            }
            result.add(p);
            count++;
        }
        return result;
    }

    /**
     * 从ES中获取商品信息的相关项
     *
     * @param name       商品名称
     * @param count      每个名称返回数量，0代表所有
     * @param preference 税号
     * @return
     */
    public List<ProductDo> getProductCodeByNameFromES(String name, int count, String preference) {
        Client client = ESTool.getClient();

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("u_name", name));

        long start = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch("product").setTypes("name")
                .addSort(SortBuilders.scoreSort())
                .setQuery(qb)
                .setFrom(0).setSize(20)
                .setPreference(preference)
                .execute().actionGet();
        long end = System.currentTimeMillis();
        logger.debug("根据" + name + "获取相关关键字耗时：" + (end - start) + "ms");

        List<ProductDo> list = new ArrayList<>();
        for (SearchHit hit :
                response.getHits()) {
            ProductDo p = JSON.parseObject(hit.getSourceAsString(), ProductDo.class);
            p.setScore(hit.getScore());
            list.add(p);
        }

        for (Product p : list) {
            float reScore = getCScore(p.getName(), name, p.getChoicePercent(), p.getRecommendFlag());
            p.setReScore(reScore);
        }

        //对自定义分数进行排序，如果分数一样的话，字符数少的优先级高
        Collections.sort(list, new ProductComparator());

        List<ProductDo> result = new ArrayList<>();
        int begin = 0;
        for (ProductDo p : list) {
            if (count > 0 && begin >= count) {
                break;
            }
            Product p1 = getCodeFromAll(p.getName(), "", "", preference);
            if (null != p1 && p1.getReScore() >= 70) {
                p.setTaxName(p1.getName());
                p.setTaxCode(p1.getTaxCode());
                p.setReScore(p1.getReScore());
                p.setRecommendScore(p1.getRecommendScore());
                result.add(p);
                begin++;
            }
        }
        return result;
    }

    private static class ProductComparator implements Comparator<Product> {
        @Override
        public int compare(Product p1, Product p2) {
            int value = Double.compare(p2.getReScore(), p1.getReScore());
            if (value == 0) {
                if (p1.getName().length() == p2.getName().length()) {
                    if (p2.getChoiceCount() < p1.getChoiceCount()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                if (p1.getName().length() > p2.getName().length()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return value;
            }
        }
    }
}
