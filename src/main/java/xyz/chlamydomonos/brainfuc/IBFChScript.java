package xyz.chlamydomonos.brainfuc;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * BFCh脚本接口
 */
public interface IBFChScript
{
    /**
     * 获取字符数组形式的脚本
     * @return 脚本的有效字符数组，只能由'+'，'-'，'['，']'，'<'，'>'，','，'.'，'/'，'\\'组成
     */
    @NotNull
    ArrayList<Character> getData();

    /**
     * 获取脚本的括号匹配信息
     * <br>
     * 返回的括号匹配信息中，每一位的值若为负数，则代表该位不是括号，
     * 否则，代表与该位匹配的括号所在位置（序号从0开始）。
     */
    @NotNull
    ArrayList<Integer> getBracketInfo();
}
