package xyz.chlamydomonos.brainfuc;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

/**
 * 基础BFCh脚本
 * <br>
 * 基础BFCh脚本只能由合法的控制字符'+'，'-'，'['，']'，'<'，'>'，','，'.'，'/'，'\\'组成，
 * 不能含有其他字符。
 */
public class BFChScript implements IBFChScript
{
    private final ArrayList<Character> data;
    private final ArrayList<Integer> bracketInfo;
    private static final char[] legalChars = new char[]{'+', '-', '[', ']', '<', '>', ',', '.', '/', '\\'};

    /**
     * 由字符串构造BFCh脚本
     * @param data 脚本的字符串，只能由'+'，'-'，'['，']'，'<'，'>'，','，'.'，'/'，'\\'组成
     */
    public BFChScript(@NotNull String data)
    {
        this.data = new ArrayList<>();
        this.bracketInfo = new ArrayList<>();
        Stack<Integer> temp = new Stack<>();
        for(int i = 0; i < data.length(); i++)
        {
            char aChar = data.charAt(i);
            boolean legal = false;
            for(char c : legalChars)
            {
                if (aChar == c)
                {
                    legal = true;
                    break;
                }
            }
            if(!legal)
                throw new IllegalArgumentException("Script contains the illegal character '" + data.charAt(i) + "'");
            this.data.add(aChar);
            this.bracketInfo.add(-1);
        }

        for(int i = 0; i < data.length(); i++)
        {
            char aChar = data.charAt(i);
            if(aChar == '[')
                temp.push(i);
            if(aChar == ']')
            {
                if(temp.empty())
                    throw new IllegalArgumentException("Script contains brackets that does not match");
                int index = temp.pop();
                bracketInfo.set(i, index);
                bracketInfo.set(index, i);
            }

            if(!temp.empty())
                throw new IllegalArgumentException("Script contains brackets that does not match");
        }
    }

    @Override
    public @NotNull ArrayList<Character> getData()
    {
        return data;
    }

    @Override
    public @NotNull ArrayList<Integer> getBracketInfo()
    {
        return bracketInfo;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFChScript)) return false;
        BFChScript that = (BFChScript) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(data);
    }
}
