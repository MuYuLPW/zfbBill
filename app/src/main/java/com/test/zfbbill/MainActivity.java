package com.test.zfbbill;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.king.zxing.util.CodeUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.test.zfbbill.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        setOrderIdAndTime(new Date());
        initListener();
    }

    private void initListener() {
        binding.title.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商家名字", "请输入内容。", text -> {
                binding.title.setText(text);
            }).show();
        });

        binding.price.setOnClickListener(v->{
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入价钱","输入数字，不需要-号，记得输小数",text->{
                binding.price.setText("-"+text);
            }).show();
        });

        binding.payType.setOnClickListener(v->{
            String[] type=new String[]{"花呗","余额宝"};
            new XPopup.Builder(MainActivity.this).asCenterList("选一个", type, new OnSelectListener() {
                @Override
                public void onSelect(int position, String text) {
                    binding.payType.setText(text);
                }
            }).show();
        });

        binding.integral.setOnClickListener(v->{
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入积分","数字就好",text -> binding.integral.setText(text+"积分")).show();
        });

        binding.shopName.setOnClickListener(v->{
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商品说明","",text -> binding.shopName.setText(text)).show();
        });

        binding.time.setOnClickListener(v->{
            DatePickDialog dialog = new DatePickDialog(this);
            //设置上下年分限制
            dialog.setYearLimt(5);
            //设置标题
            dialog.setTitle("选择时间");
            //设置类型
            dialog.setType(DateType.TYPE_YMDHM);
            //设置消息体的显示格式，日期格式
            dialog.setMessageFormat("yyyy-MM-dd HH:mm");
            //设置点击确定按钮回调
            dialog.setOnSureLisener(new OnSureLisener() {
                @Override
                public void onSure(Date date) {
                    setOrderIdAndTime(date);
                }
            });
            dialog.show();
        });

        binding.shopOrderId.setOnClickListener(v->{
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商家订单号","由于不同商家订单号格式不同，自己输入",binding.shopOrderId.getText(),"",text -> binding.shopOrderId.setText(text)).show();
        });

        binding.transType.setOnClickListener(v->{

        });

    }

    //设置订单号和创建时间
    private void setOrderIdAndTime(Date date){
        String formatDate=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        binding.time.setText(formatDate);
        String formatID=new SimpleDateFormat("yyyyMMdd").format(date);
        binding.orderID.setText(formatID+"2200149"+(System.currentTimeMillis()*4));
        //设置商家订单号
        binding.shopOrderId.setText(formatID+StringRandom.getStringRandom(8));
        binding.codeID.setText(StringRandom.getNumberRandom(4));
        Bitmap barCode = CodeUtils.createBarCode(binding.orderID.getText().toString(), 143, 67);
        binding.code.setImageBitmap(barCode);
    }
}
