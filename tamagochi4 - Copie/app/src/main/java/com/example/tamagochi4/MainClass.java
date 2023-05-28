package com.example.tamagochi4;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Handler;
import android.provider.Settings;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainClass extends AppCompatActivity{

    private String tmdevice;
    public Integer hunger;
    public Integer happiness;
    public Integer money;
    int delay = 3000; //


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout1);

        TextView textView1 = findViewById(R.id.textviewHunger);
        TextView textviewmoney = findViewById(R.id.textviewmoney);
        ProgressBar hungerprogressbar = findViewById(R.id.hungerprogressbar);
        ProgressBar hapinessprogressBar = findViewById(R.id.hapinessprogressBar);
        Button feedbutton = findViewById(R.id.feedbutton);
        ImageButton tamagoimgbutton = findViewById(R.id.tamagoimgbutton);
        Button buttontoflip = findViewById(R.id.buttontoflip);
        //final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        tmdevice = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tamagochi4-f4ebf-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("users");

        //------------ajoute l'appareil dans la base de donnée si il n'est pas deja présent
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(tmdevice)) {
                    myRef.child(tmdevice).child("hunger").setValue(100);
                    myRef.child(tmdevice).child("happiness").setValue(100);
                    myRef.child(tmdevice).child("money").setValue(0);
                }
                if (!snapshot.child(tmdevice).hasChild("hunger")) {
                    myRef.child(tmdevice).child("hunger").setValue(100);
                }
                if (!snapshot.child(tmdevice).hasChild("happiness")) {
                    myRef.child(tmdevice).child("happiness").setValue(100);
                }
                if (!snapshot.child(tmdevice).hasChild("money")) {
                    myRef.child(tmdevice).child("money").setValue(100);
                }
                hunger = snapshot.child(tmdevice).child("hunger").getValue(Integer.class);
                happiness = snapshot.child(tmdevice).child("happiness").getValue(Integer.class);
                money = snapshot.child(tmdevice).child("money").getValue(Integer.class);
                hungerprogressbar.setProgress(hunger);
                hapinessprogressBar.setProgress(happiness);
                textviewmoney.setText(String.valueOf(money));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        //-------onDataChangeEnd-------------



        //------------------clicker------------------
        tamagoimgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money += 1;
                myRef.child(tmdevice).child("money").setValue(money);
                textviewmoney.setText(String.valueOf(money));
            }
        });

        //------------------bouton feed------------------
        feedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (money >= 100) {
                            if(hunger == 100) {
                                myRef.child(tmdevice).child("hunger").setValue(100);
                            }else {
                                myRef.child(tmdevice).child("hunger").setValue(hunger + 10);
                            }
                            myRef.child(tmdevice).child("money").setValue(money - 10);
                            textviewmoney.setText(String.valueOf(money));
                            hungerprogressbar.setProgress(hunger);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if (hunger <= 90) {
                    hunger = hunger + 10;
                }else{
                    hunger = 100;
                }
                money = money - 100;
            }
        });
        //-------setOnClickListener End-------------

        final Handler handler = new Handler();


        //------------------update de la faim et du bonheur toute les 50 secondes---------
        handler.postDelayed(new Runnable() {
            public void run() {
                delay = 50000;
                hunger -= 1;
                happiness -= 1;
                myRef.child(tmdevice).child("happiness").setValue(happiness);
                myRef.child(tmdevice).child("hunger").setValue(hunger);
                hungerprogressbar.setProgress(hunger);
                if(hunger <= 0 || happiness <= 0){
                    if (hunger <= 0) {
                        Toast.makeText(getApplicationContext(), "tamagochi is dead from hunger", Toast.LENGTH_SHORT).show();
                    }
                    if (happiness <= 0) {
                        Toast.makeText(getApplicationContext(), "tamagochi is dead from suicide", Toast.LENGTH_SHORT).show();
                    }

                    //------------------game over------------------
                    setContentView(R.layout.gameover);
                    Button restartbutton = findViewById(R.id.restartbutton);
                    restartbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myRef.child(tmdevice).child("hunger").setValue(100);
                            myRef.child(tmdevice).child("happiness").setValue(100);
                            myRef.child(tmdevice).child("money").setValue(0);
                            hunger = 100;
                            money = 0;
                            happiness = 100;
                            hungerprogressbar.setProgress(hunger);
                            hapinessprogressBar.setProgress(happiness);
                            textviewmoney.setText(String.valueOf(money));
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        //-----------------------------------------------------------


        //------------------changement de view pile ou face------------------
        buttontoflip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.pileouface);
                Button flipbutton = findViewById(R.id.flipbutton);
                TextView mntxt = findViewById(R.id.moneytxt);
                mntxt.setText(String.valueOf(money));
                TextView vordtxt = findViewById(R.id.vordxt);
                TextView happtxt = findViewById(R.id.happtxt);
                vordtxt.setText("");
                happtxt.setText(String.valueOf(happiness));
                Button backbutton = findViewById(R.id.backtohomepage);

                backbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                //------------------jeux pile ou face------------------
                flipbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( ((int)Math.floor(Math.random() * (1 +1) + 0)) == 1) {
                            money += 10;
                            happiness += 2;
                            if (happiness > 100) {
                                happiness = 100;
                            }
                            vordtxt.setText("victory");
                            myRef.child(tmdevice).child("money").setValue(money);
                            myRef.child(tmdevice).child("happiness").setValue(happiness);
                            mntxt.setText(String.valueOf(money));
                            happtxt.setText(String.valueOf(happiness));
                        }else {
                            if (money < 0) {
                                money += money;
                            } else {
                                money -= 10;
                            }
                            vordtxt.setText("defeat");
                            myRef.child(tmdevice).child("money").setValue(money);
                            mntxt.setText(String.valueOf(money));
                            happtxt.setText(String.valueOf(happiness));
                        }
                    }
                });
            }
        });
        //----------------------------------------





    }
}
