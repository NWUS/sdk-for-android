import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Liu233w on 2016/7/6.
 */
public class VoiceRecognizer {
    //这几个参数都是从教程里或sdk里面抄的，我也不知道是啥意思
    private Activity mActivity;
    SpeechUtility utility;
    private SpeechRecognizer mAsr;
    private RecognizerListener mRecognizerListener;

    //回调锁，用来将回调函数转换成基本的函数调用流程
    private boolean mCallBackLock;
    //回调结果
    private String mCallBackResult;

    public VoiceRecognizer(Activity currentActivity) {
        Log.i("JavaClass", "Constructor called with currentActivity = " + currentActivity);
        mActivity = currentActivity;
        //Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        //创建语音配置对象
        utility = SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=57171a19");

        // 在线命令词识别，不启用终端级语法
        // 1.创建SpeechRecognizer对象
        SpeechRecognizer mAsr = SpeechRecognizer.createRecognizer(mActivity, null);

        // 2.设置参数
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        mAsr.setParameter(SpeechConstant.SUBJECT, "asr");

        //这是一个接口，通过一个实现此接口的匿名类来进行回调
        //public int startListening(RecognizerListener listener)
        //此函数负责开始录音，将匿名类传入，就会在执行的过程中自动调用类中的相应函数
        //public void stopListening()
        //使用此函数结束录音
        mRecognizerListener = new  RecognizerListener() {
            // 音量变化
            public void onVolumeChanged(int volume, byte[] data) {}
            // 返回结果
            public void onResult(final RecognizerResult result, boolean isLast)
            {
                //只有是最后一次识别的结果时才算作识别结束
                if(isLast)
                {
                    mCallBackLock=false;
                    mCallBackResult=result.getResultString();
                }
            }
            // 开始说话
            public void onBeginOfSpeech()
            {
                //重置回调锁
                mCallBackLock = true;
            }
            // 结束说话
            public void onEndOfSpeech() {}
            // 错误回调
            public void onError(SpeechError error) {}
            // 事件回调
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
        };
    }

    public String StartRecognize()
    {
        mAsr.startListening(mRecognizerListener);

        while(mCallBackLock);

        return mCallBackResult;
    }
}
