package com.test.zfbbill;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.king.zxing.util.CodeUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.test.zfbbill.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String[] type = new String[]{"花呗", "余额宝"};
    private String[] transType = new String[]{"餐饮美食", "服饰美容", "生活日用", "充值缴费", "交通出行", "通讯物流", "休闲生活", "医疗保健"
            , "住房物业", "图书教育", "酒店旅行", "爱车养车", "运动健康", "生活服务", "投资理财", "金融保险", "信用借还", "人情往来", "公益慈善",
            "经营所得", "职业酬劳", "奖金红包", "转账充值", "其他"};
    private int transTypePos = 0;
    private boolean shopType=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.getRoot());

        binding.payType.setText(type[0]);
        binding.transType.setText(transType[0]);
        setOrderIdAndTime(new Date());
        setShopType();
        initListener();
    }

    //设置商家订二维码显示
    private void setShopType() {
        if (shopType){
            binding.transInfo.setVisibility(View.VISIBLE);
            binding.coedView.setVisibility(View.VISIBLE);
            binding.shopOrderId.setVisibility(View.VISIBLE);
            binding.shopOrderDes.setVisibility(View.GONE);
        }else {
            binding.transInfo.setVisibility(View.GONE);
            binding.coedView.setVisibility(View.GONE);
            binding.shopOrderId.setVisibility(View.GONE);
            binding.shopOrderDes.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        binding.title.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商家名字", "请输入内容。", text -> {
                binding.title.setText(text);
            }).show();
        });

        binding.price.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入价钱", "输入数字，不需要-号，记得输小数", text -> {
                binding.price.setText("-" + text);
            }).show();
        });

        binding.payType.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asCenterList("选一个", type, new OnSelectListener() {
                @Override
                public void onSelect(int position, String text) {
                    binding.payType.setText(text);
                }
            }).show();
        });

        binding.integral.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入积分", "数字就好", text -> binding.integral.setText(text + "积分")).show();
        });

        binding.shopName.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商品说明", "", text -> binding.shopName.setText(text)).show();
        });

        binding.time.setOnClickListener(v -> {
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

        binding.shopOrderId.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this).asInputConfirm("输入商家订单号", "由于不同商家订单号格式不同，自己输入", binding.shopOrderId.getText(), "", text -> binding.shopOrderId.setText(text)).show();
        });

        binding.transType.setOnClickListener(v -> {
            new XPopup.Builder(MainActivity.this)
                    .maxHeight(dp2px(400))
                    .asBottomList("选一个", transType, null, transTypePos, new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            transTypePos = position;
                            binding.transType.setText(text);
                        }
                    }).show();
        });

        binding.shopType.setOnClickListener(v->{
            shopType=!shopType;
            setShopType();
        });

        binding.img.setOnClickListener(v->{
            String iconName[] =new String[]{"默认","理财","wps","从相册选择"};
            int icons[]=new int[]{R.mipmap.shop,R.mipmap.price,R.mipmap.wps,R.mipmap.ic_launcher};
            new XPopup.Builder(MainActivity.this).asCenterList("选择图片", iconName, icons, new OnSelectListener() {
                @Override
                public void onSelect(int position, String text) {
                    switch (position){
                        case 3:
                            openGallery();
                            break;
                        default:
                            binding.img.setImageResource(icons[position]);
                    }
                }
            }).show();
        });

    }

    //打开相册
    private void openGallery() {
        PictureSelector.create(this)
                .openGallery(ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)
                .isSingleDirectReturn(false)
                .isCamera(false)
                .isEnableCrop(true)
//                .cropImageWideHigh(1,1)
                .withAspectRatio(1,1)//裁剪比例
                .isDragFrame(true)
                .rotateEnabled(false)
                .compress(true)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LocalMedia localMedia = result.get(0);
//                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//                            Glide.with(MainActivity.this).load(localMedia.getAndroidQToPath()).into(binding.img);
//                            return;
//                        }
                        Glide.with(MainActivity.this).load(localMedia.getCompressPath()).into(binding.img);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    //设置订单号和创建时间
    private void setOrderIdAndTime(Date date) {
        String formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        binding.time.setText(formatDate);
        String formatID = new SimpleDateFormat("yyyyMMdd").format(date);
        binding.orderID.setText(formatID + "2200149" + (System.currentTimeMillis() * 4));
        //设置商家订单号
        binding.shopOrderId.setText(formatID + StringRandom.getStringRandom(8));
        binding.codeID.setText(StringRandom.getNumberRandom(4));
        Bitmap barCode = CodeUtils.createBarCode(binding.orderID.getText().toString(), 143, 67);
        binding.code.setImageBitmap(barCode);
    }

    public int dp2px(int dp){
        return (int)(getResources().getDisplayMetrics().density*dp+0.5);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PictureFileUtils.deleteAllCacheDirFile(this);
    }
}
