package com.example.apprelogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class FullscreenActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean runnableStopped = false;
    private boolean cbBateriaChecked = true;

    private BroadcastReceiver bateriaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.tv_nivelBateria.setText(String.valueOf(nivel) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mViewHolder.tv_horasMinutos = findViewById(R.id.tv_horasMinutos);
        mViewHolder.tv_Segundos = findViewById(R.id.tv_segundos);
        mViewHolder.tv_nivelBateria = findViewById(R.id.tv_nivelbateria);
        mViewHolder.cb_nivelBateria = findViewById(R.id.cb_nivelBateria);
        mViewHolder.iv_preferencias = findViewById(R.id.iv_preferencias);
        mViewHolder.iv_sair = findViewById(R.id.iv_sair);
        mViewHolder.ll_menu = findViewById(R.id.ll_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        registerReceiver(bateriaReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mViewHolder.ll_menu.animate().translationY(500);

        mViewHolder.cb_nivelBateria.setChecked(true);

        mViewHolder.cb_nivelBateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbBateriaChecked) {
                    cbBateriaChecked = false;
                    mViewHolder.tv_nivelBateria.setVisibility(View.GONE);
                } else {
                    cbBateriaChecked = true;
                    mViewHolder.tv_nivelBateria.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewHolder.iv_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.ll_menu.animate()
                        .translationY(mViewHolder.ll_menu.getMeasuredHeight())
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });

        mViewHolder.iv_preferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.ll_menu.setVisibility(View.VISIBLE);
                mViewHolder.ll_menu.animate()
                        .translationY(0)
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        runnableStopped = false;
        AtualizarHora();
    }

    @Override
    protected void onStop() {
        super.onStop();
        runnableStopped = true;
    }

    private void AtualizarHora() {

        runnable = new Runnable() {
            @Override
            public void run() {
                if (runnableStopped)
                    return;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                String horasMinutosFormatado = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
                String segundosFormatados = String.format("%02d", calendar.get(Calendar.SECOND));

                mViewHolder.tv_horasMinutos.setText(horasMinutosFormatado);
                mViewHolder.tv_Segundos.setText(segundosFormatados);

                long agora = SystemClock.uptimeMillis();
                long proximo = agora + (1000 - (agora % 1000));

                handler.postAtTime(runnable, proximo);
            }
        };
        runnable.run();
    }

    private static class ViewHolder {
        TextView tv_horasMinutos;
        TextView tv_Segundos;
        CheckBox cb_nivelBateria;
        TextView tv_nivelBateria;
        ImageView iv_preferencias;
        ImageView iv_sair;
        LinearLayout ll_menu;
    }
}