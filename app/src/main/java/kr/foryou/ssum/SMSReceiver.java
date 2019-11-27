package kr.foryou.ssum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceive", "무언가 동작했습니다.");
        // 수신되었을 때 호출되는 콜백 메서드
        // 매개변수 intent의 액션에 방송의 '종류'가 들어있고 필드에는 '추가정보' 가 들어있음

        // SMS 메시지를 파싱합니다.
        Bundle bundle = intent.getExtras();
        if (bundle != null) { // 수신된 내용이 있으면
            // 실제 메세지는 Object타입의 배열에 PDU 형식으로 저장됨

            // 문자 메시지는 pdus란 종류 값으로 들어있음
            Object [] pdus = (Object[])bundle.get("pdus");

            SmsMessage[] msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                msgs[i] = SmsMessage
                        .createFromPdu((byte[]) pdus[i]);

                String sender = msgs[i].getOriginatingAddress();
                String content = msgs[i].getMessageBody().toString();

                Log.i("sender", sender);
                Log.i("content", content);

                if(sender.equals("0518910088")){
                    int startIdx = content.lastIndexOf("[");
                    int endIdx = content.lastIndexOf("]");
                    String authNumber = content.substring(startIdx+1, endIdx);

                    Log.i("authNumber", authNumber);
                    MainActivity.webView.loadUrl("javascript:auths('"+authNumber+"')");
                }
            }
        }

    }
}
