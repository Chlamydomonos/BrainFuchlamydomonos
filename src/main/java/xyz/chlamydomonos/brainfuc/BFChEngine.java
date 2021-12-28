package xyz.chlamydomonos.brainfuc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Queue;

/**
 * BFCh脚本引擎，内置BFCh虚拟机，用于运行BFCh脚本
 */
public class BFChEngine
{
    private final ArrayList<Object> memory;
    private final ArrayList<Object> outputs;
    private final Object[] pasteBin;
    private int currentMemoryIndex;

    private int handlePlus(int currentIndex)
    {
        if(memory.get(currentMemoryIndex) == null)
            memory.set(currentMemoryIndex, 1);
        else if(memory.get(currentMemoryIndex) instanceof Integer)
            memory.set(currentMemoryIndex, (Integer)memory.get(currentMemoryIndex) + 1);
        else if(memory.get(currentMemoryIndex) instanceof Method)
        {
            Method temp = (Method) memory.get(currentMemoryIndex);
            Object next = memory.get(currentMemoryIndex + 1);
            if(next instanceof Integer)
            {
                while (memory.size() < currentMemoryIndex + 3)
                    memory.add(null);
                Object obj = memory.get(currentMemoryIndex + 2);
                ArrayList<Object> params = new ArrayList<>();
                for(int i = 0; i < (Integer) next - 1; i++)
                    params.add(memory.get(currentMemoryIndex + i + 3));

                try
                {
                    memory.set(currentMemoryIndex, temp.invoke(obj, params.toArray()));
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return currentIndex + 1;
    }

    private int handleMinus(int currentIndex)
    {
        Object temp = memory.get(currentMemoryIndex);
        if(!(temp instanceof Integer) || (Integer)temp <= 1)
            memory.set(currentMemoryIndex, null);
        else
            memory.set(currentMemoryIndex, (Integer)temp - 1);
        return currentIndex + 1;
    }

    private int handleLeft(int currentIndex)
    {
        currentMemoryIndex--;
        if(currentMemoryIndex < 0)
            throw new RuntimeException("Memory index less than 0!");
        while (currentMemoryIndex >= memory.size())
            memory.add(null);
        return currentIndex + 1;
    }

    private int handleRight(int currentIndex)
    {
        currentMemoryIndex++;
        while (currentMemoryIndex >= memory.size())
            memory.add(null);
        return currentIndex + 1;
    }

    private int handleLeftBracket(int currentIndex, ArrayList<Integer> bracketInfo)
    {
        if(memory.get(currentMemoryIndex) == null)
        {
            int temp = bracketInfo.get(currentIndex);
            while (temp >= memory.size())
                memory.add(null);
            return temp;
        }
        return currentIndex + 1;
    }

    private int handleRightBracket(int currentIndex, ArrayList<Integer> bracketInfo)
    {
        if(memory.get(currentMemoryIndex) != null)
        {
            int temp = bracketInfo.get(currentIndex);
            while (temp >= memory.size())
                memory.add(null);
            return temp;
        }
        return currentIndex + 1;
    }

    private int handleInput(int currentIndex, Queue<Object> inputs)
    {
        if(inputs.isEmpty())
            throw new RuntimeException("Not enough inputs!");
        memory.set(currentMemoryIndex, inputs.poll());
        return currentIndex + 1;
    }

    private int handleOutput(int currentIndex)
    {
        outputs.add(memory.get(currentMemoryIndex));
        return currentIndex + 1;
    }

    private int handleCopy(int currentIndex)
    {
        pasteBin[0] = memory.get(currentMemoryIndex);
        return currentIndex + 1;
    }

    private int handlePaste(int currentIndex)
    {
        memory.set(currentMemoryIndex, pasteBin[0]);
        return currentIndex + 1;
    }

    /**
     * 构造方法
     * <br>
     * 初始化时，BFCh虚拟机的内存被全部初始化为0，输出序列为空，剪贴板值为0
     */
    public BFChEngine()
    {

        memory = new ArrayList<>();
        memory.add(null);
        outputs = new ArrayList<>();
        currentMemoryIndex = 0;
        pasteBin = new Object[]{null};
    }

    /**
     * 在BFCh虚拟机上运行脚本
     * @param script 要运行的脚本
     * @param inputs 输入数据的队列。脚本请求输入时，将把一个对象出队作为输入
     * @param refreshMemory 运行前是否重置内存（把内存全部置为0）
     * @param refreshOutputs 运行前是否清空输出序列
     * @param refreshPasteBin 运行前是否重置剪贴板（把其值置为0）
     */
    public void runScript(IBFChScript script, Queue<Object> inputs, boolean refreshMemory, boolean refreshOutputs, boolean refreshPasteBin)
    {
        if(refreshMemory)
        {
            memory.clear();
            memory.add(null);
            currentMemoryIndex = 0;
        }
        if(refreshOutputs)
            outputs.clear();
        if(refreshPasteBin)
            pasteBin[0] = null;

        int currentIndex = 0;
        ArrayList<Character> scriptArray = script.getData();
        ArrayList<Integer> bracketInfo = script.getBracketInfo();
        while (currentIndex != scriptArray.size())
        {
            char currentChar = scriptArray.get(currentIndex);
            switch (currentChar)
            {
                case '+':
                    currentIndex = handlePlus(currentIndex);
                    break;
                case '-':
                    currentIndex = handleMinus(currentIndex);
                    break;
                case '<':
                    currentIndex = handleLeft(currentIndex);
                    break;
                case '>':
                    currentIndex = handleRight(currentIndex);
                    break;
                case '[':
                    currentIndex = handleLeftBracket(currentIndex, bracketInfo);
                    break;
                case ']':
                    currentIndex = handleRightBracket(currentIndex, bracketInfo);
                    break;
                case ',':
                    currentIndex = handleInput(currentIndex, inputs);
                    break;
                case '.':
                    currentIndex = handleOutput(currentIndex);
                    break;
                case '/':
                    currentIndex = handleCopy(currentIndex);
                    break;
                case '\\':
                    currentIndex = handlePaste(currentIndex);
                    break;
                default:
                    currentIndex++;
                    break;
            }
        }
    }

    /**
     * 获取输出序列
     */
    public ArrayList<Object> getOutputs()
    {
        return outputs;
    }
}
