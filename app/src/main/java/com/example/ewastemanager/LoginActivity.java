package com.example.ewastemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public Button loginBtn;
    public EditText email;
    public EditText password;
    private String emailStr,passwordStr;
    private FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences sharedpreferences;
    private String userId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedpreferences = getSharedPreferences("userId", MODE_PRIVATE);
        userId=sharedpreferences.getString("userId","");
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.loginBtn);

        if (!userId.isEmpty()){
            Intent i=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser= mAuth.getCurrentUser();
                if (firebaseUser!=null){
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(LoginActivity.this,"Please log in",Toast.LENGTH_SHORT).show();
                }
            }
        };
        loginBtn.setOnClickListener(v->
        { emailStr=email.getText().toString();
        passwordStr=password.getText().toString();
        if (emailStr.isEmpty()){
            email.setError("Please enter your email address");
            email.requestFocus();
        }
        else if (passwordStr.isEmpty()){
            password.setError("Please Enter your password");
            password.requestFocus();
        }
        else if (emailStr.isEmpty()&&passwordStr.isEmpty())
        {
            Toast.makeText(LoginActivity.this,"All fields are needed",Toast.LENGTH_SHORT).show();
        }
        else if(!emailStr.isEmpty()&&!passwordStr.isEmpty()){
            mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //if login is successful, update the UI with the user's information
                        Log.d("tag", "signInWithEmail:success");
                        FirebaseUser firebaseUser=mAuth.getCurrentUser();

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("userId",firebaseUser.getUid());
                        editor.commit();
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        //if log in fails, display a message to the user
//                        Log.w("tag", "signInWithEmail:failure", task)
                        Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(LoginActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();
        }
        });
    }



}
