package cn.ouctechnology.oucfly.result;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 12:52
 * @description: 返回信息实体，所有的操作均返回一个Result实例
 **/
public class Result<T> {
    //标识是否成功
    private boolean success;
    //成功后的返回数据
    private T content;
    //失败信息
    private String errorMsg;


    private Result(boolean success, T content, String errorMsg) {
        this.success = success;
        this.content = content;
        this.errorMsg = errorMsg;
    }

    /**
     * 无内容成功
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    /**
     * 有内容成功
     *
     * @param content
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T content) {
        return new Result<>(true, content, null);
    }

    /**
     * 无错误信息失败
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail() {
        return new Result<>(false, null, null);
    }

    /**
     * 有错误信息失败
     *
     * @param errorMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(String errorMsg) {
        return new Result<>(false, null, errorMsg);
    }

    /**
     * 判断是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取内容
     *
     * @return
     */
    public T getContent() {
        return content;
    }

    /**
     * 获取错误信息
     *
     * @return
     */
    public String getErrorMsg() {
        return errorMsg;
    }
}
