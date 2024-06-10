package com.sih2020.abhyuday;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {
    static final int GOOGLE_SIGN=123;
    FirebaseAuth mAuth;
    Button btn_login;
    TextView text;
    ImageView image;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    String phone=" ";
    private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("ABHYUDAY",MODE_PRIVATE);
        editor = pref.edit();
        btn_login = findViewById(R.id.login);
        progressBar=findViewById( R.id.progress_circular );
        mAuth=FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility( View.VISIBLE );
                Intent signIntent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult( signIntent,GOOGLE_SIGN );
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);


                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Sign in Failed",Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately

                Log.w(TAG,"Google sig in failed",e);
                e.printStackTrace();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d( "TAG","firebaseAuthWithGoogle:"+account.getId() );
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("Sucess auth1");
                        if (task.isSuccessful()) {
                            System.out.println("Sucess auth");
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility( View.INVISIBLE );
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String email = user.getEmail();
                            final String name = user.getDisplayName();
                            final String uid = user.getUid();
                            System.out.println("Check point 1");
                            //final String phone = user.getPhoneNumber();
                            String login_url = getResources().getString(R.string.GET_DATA);
                            JSONObject jsonObject1=new JSONObject();
                            JSONObject jsonObject2=new JSONObject();
                            try {
                                jsonObject1.put("API_KEY",getResources().getString(R.string.API_KEY));
                                jsonObject2.put("email",email);
                                jsonObject2.put("name",name);
                                jsonObject2.put("authId",uid);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject jsonObject= new JSONObject();
                            try {
                                jsonObject.put("MODE","USER_REGISTER");
                                jsonObject.put("headers",jsonObject1);
                                jsonObject.put("payload",jsonObject2);
                                Log.e("DEBUG",jsonObject.toString());
                                System.out.println("Check point 2");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Check point 3");
                            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                            System.out.println("Check point 3.0");

                            JsonObjectRequest objectRequest= new JsonObjectRequest(Request.Method.POST, login_url, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("Check point 3.00");
                                    Log.e("RESPONSE CODE",response.toString());

                                    try {
                                        Log.e("RESPONSE",response.toString());

                                        if(response.getString("MESSAGE").equals("SUCCESS"))
                                        {
                                            System.out.println("Check point 3.1");
                                            //editor.putString("MODE","REGISTER");
                                            editor.putString("EMAIL",email);
                                            editor.putString("NAME",name);
                                            editor.putString("USER_TYPE","NORMAL");
                                            editor.putString("UID",uid);
                                            editor.putString("PHONE","");
                                            editor.putString("PASSWORD","");

                                            editor.apply();
                                            editor.commit();
                                            System.out.println("Check point 4");
                                            Toast.makeText(getApplicationContext(),"User logged in successfully",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Failed!! Contact Administrator !!!",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }
                            );
                            requestQueue.add(objectRequest);
                            Log.e("TAG",user.getUid()+"\t"+user.getEmail()+"\t"+user.getDisplayName()+"\t"+user.getProviderId());
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility( View.INVISIBLE );
                            Toast.makeText(getApplicationContext(),"Could not log in User",Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}