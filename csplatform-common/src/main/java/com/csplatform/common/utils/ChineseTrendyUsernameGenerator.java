package com.csplatform.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author WangXing
 * @Date 2025/12/31 10:45
 * @PackageName:com.csplatform.common.utils
 * @ClassName: ChineseTrendyUsernameGenerator
 * @Version 1.0
 */
public class ChineseTrendyUsernameGenerator {

    // 网络流行前缀
    private static final List<String> TRENDY_PREFIXES = Arrays.asList(
            "快乐", "阳光", "勇敢", "聪明", "温柔", "可爱", "帅气", "美丽",
            "无敌", "超级", "终极", "天才", "神秘", "隐藏", "匿名", "临时",
            "熬夜", "秃头", "干饭", "摸鱼", "躺平", "内卷", "摆烂", "真香"
    );

    // 网络流行后缀
    private static final List<String> TRENDY_SUFFIXES = Arrays.asList(
            "小能手", "达人", "爱好者", "控", "迷", "粉", "狂魔", "终结者",
            "锦鲤", "欧皇", "非酋", "大佬", "萌新", "菜鸟", "高手", "大神",
            "本鲤", "本皇", "本尊", "本人", "在线", "营业中", "已就位"
    );

    // 动物/物品名称
    private static final List<String> ANIMAL_ITEMS = Arrays.asList(
            "小猫", "小狗", "小熊", "小兔", "小鹿", "小猴", "熊猫", "企鹅",
            "橘子", "苹果", "西瓜", "草莓", "奶茶", "咖啡", "蛋糕", "面包"
    );

    /**
     * 生成网络流行风格用户名
     */
    public static String generateTrendyUsername() {
        Random random = ThreadLocalRandom.current();

        int pattern = random.nextInt(5);
        switch (pattern) {
            case 0: {
                // 前缀 + 动物/物品
                String prefix = TRENDY_PREFIXES.get(random.nextInt(TRENDY_PREFIXES.size()));
                String item = ANIMAL_ITEMS.get(random.nextInt(ANIMAL_ITEMS.size()));
                return prefix + item;  // 如：快乐小熊
            }
            case 1: {
                // 动物/物品 + 后缀
                String item = ANIMAL_ITEMS.get(random.nextInt(ANIMAL_ITEMS.size()));
                String suffix = TRENDY_SUFFIXES.get(random.nextInt(TRENDY_SUFFIXES.size()));
                return item + suffix;  // 如：小猫爱好者
            }
            case 2: {
                // 前缀 + 后缀
                String prefix = TRENDY_PREFIXES.get(random.nextInt(TRENDY_PREFIXES.size()));
                String suffix = TRENDY_SUFFIXES.get(random.nextInt(TRENDY_SUFFIXES.size()));
                return prefix + suffix;  // 如：熬夜达人
            }
            case 3: {
                // 纯动物/物品 + 数字
                String item = ANIMAL_ITEMS.get(random.nextInt(ANIMAL_ITEMS.size()));
                int num = 100 + random.nextInt(900);
                return item + num;  // 如：熊猫123
            }
            case 4: {
                // 直接流行语
                String[] popularPhrases = {"锦鲤本鲤", "快乐源泉", "人间清醒", "佛系青年", "躺平选手"};
                return popularPhrases[random.nextInt(popularPhrases.length)];
            }
            default:
                return "用户" + (10000 + random.nextInt(90000));
        }
    }
}
