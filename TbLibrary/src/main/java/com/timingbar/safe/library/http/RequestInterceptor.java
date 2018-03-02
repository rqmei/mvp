package com.timingbar.safe.library.http;

import android.support.annotation.Nullable;
import com.timingbar.safe.library.util.CharactorHandler;
import com.timingbar.safe.library.util.ZipHelper;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * RequestInterceptor
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 网络请求okhttp3中HTTP相关的拦截器相关处理
 *
 * @author rqmei on 2018/1/29
 */
@Singleton
public class RequestInterceptor implements Interceptor {
    private IGlobalHttpHandler mHandler;

    @Inject
    public RequestInterceptor(@Nullable IGlobalHttpHandler handler) {
        this.mHandler = handler;
    }

    /**
     * @param chain 当前的拦截器链条
     * @return 服务器响应的结果
     * @throws IOException io异常
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取到当前的 Request 对象
        Request request = chain.request ();
        boolean hasRequestBody = request.body () != null;
        Buffer requestbuffer = new Buffer ();
        if (hasRequestBody) {
            request.body ().writeTo (requestbuffer);
        }

        //打印请求信息
        Timber.tag (getTag (request, "Request_Info")).w ("Params : 「 %s 」%nConnection : 「 %s 」%nHeaders : %n「 %s 」"
                , hasRequestBody ? parseParams (request.body (), requestbuffer) : "Null"
                , chain.connection ()
                , request.headers ());

        long t1 = System.nanoTime ();
        Response originalResponse;
        try {
            //继续拦截器链条的执行
            originalResponse = chain.proceed (request);
        } catch (Exception e) {
            Timber.w ("Http Error: " + e);
            throw e;
        }
        long t2 = System.nanoTime ();
        String bodySize = originalResponse.body ().contentLength () != -1 ? originalResponse.body ().contentLength () + "-byte" : "unknown-length";
        //打印响应时间以及响应头
        Timber.tag (getTag (request, "Response_Info")).w ("Received response in [ %d-ms ] , [ %s ]%n%s"
                , TimeUnit.NANOSECONDS.toMillis (t2 - t1), bodySize, originalResponse.headers ());

        //打印响应结果
        String bodyString = printResult (request, originalResponse);

        if (mHandler != null)//这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
            return mHandler.onHttpResultResponse (bodyString, chain, originalResponse);

        return originalResponse;
    }

    /**
     * 打印响应结果
     *
     * @param request
     * @param originalResponse
     * @return
     * @throws IOException
     */
    @Nullable
    private String printResult(Request request, Response originalResponse) throws IOException {
        //读取服务器返回的结果
        ResponseBody responseBody = originalResponse.body ();
        String bodyString = null;
        if (isParseable (responseBody)) {
            BufferedSource source = responseBody.source ();
            source.request (Long.MAX_VALUE); // Buffer the entire body.
            //得到输入流对象。
            Buffer buffer = source.buffer ();
            //获取content的压缩类型
            String encoding = originalResponse
                    .headers ()
                    .get ("Content-Encoding");
            Buffer clone = buffer.clone ();

            //解析response content
            bodyString = parseContent (responseBody, encoding, clone);
            Timber.tag (getTag (request, "Response_Result")).w (isJson (responseBody) ? CharactorHandler.jsonFormat (bodyString) : bodyString);

        } else {
            Timber.tag (getTag (request, "Response_Result")).w ("This result isn't parsed");
        }
        return bodyString;
    }


    private String getTag(Request request, String tag) {
        return String.format (" [%s] 「 %s 」>>> %s", request.method (), request.url ().toString (), tag);
    }


    /**
     * 解析服务器响应的内容
     *
     * @param responseBody 请求体类，这是上传数据的核心类
     * @param encoding
     * @param clone
     * @return
     */
    private String parseContent(ResponseBody responseBody, String encoding, Buffer clone) {
        Charset charset = Charset.forName ("UTF-8");
        MediaType contentType = responseBody.contentType ();
        if (contentType != null) {
            charset = contentType.charset (charset);
        }
        if (encoding != null && encoding.equalsIgnoreCase ("gzip")) {//content使用gzip压缩
            return ZipHelper.decompressForGzip (clone.readByteArray (), convertCharset (charset));//解压
        } else if (encoding != null && encoding.equalsIgnoreCase ("zlib")) {//content使用zlib压缩
            return ZipHelper.decompressToStringForZlib (clone.readByteArray (), convertCharset (charset));//解压
        } else {//content没有被压缩
            return clone.readString (charset);
        }
    }

    /**
     * 获取网络请求中的参数
     *
     * @param body          请求体类，这是上传数据的核心类
     * @param requestbuffer
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String parseParams(RequestBody body, Buffer requestbuffer) throws UnsupportedEncodingException {
        if (body.contentType () == null)
            return "Unknown";
        if (!body.contentType ().toString ().contains ("multipart")) {
            Charset charset = Charset.forName ("UTF-8");
            MediaType contentType = body.contentType ();
            if (contentType != null) {
                charset = contentType.charset (charset);
            }
            return URLDecoder.decode (requestbuffer.readString (charset), convertCharset (charset));
        }
        return "This Params isn't Text";
    }

    /**
     * 服务器返回的内容是不是text类型
     *
     * @param responseBody 服务器响应的流对象
     * @return true:是；false 不是
     */
    public static boolean isParseable(ResponseBody responseBody) {
        if (responseBody.contentLength () == 0)
            return false;
        return responseBody.contentType ().toString ().contains ("text") || isJson (responseBody);
    }

    /**
     * 服务器返回的内容是不是json类型
     *
     * @param responseBody 服务器响应的流对象
     * @return true:是；false 不是
     */
    public static boolean isJson(ResponseBody responseBody) {
        return responseBody.contentType ().toString ().contains ("json");
    }

    /**
     * 去掉字符串中的符号"["
     *
     * @param charset
     * @return
     */
    public static String convertCharset(Charset charset) {
        String s = charset.toString ();
        int i = s.indexOf ("[");
        if (i == -1)
            return s;
        return s.substring (i + 1, s.length () - 1);
    }

}
