package com.example.cryptify;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.cryptify.Functions.signIn;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignInActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private Button btnSignIn;
    private TextView toggleLink;
    private ImageButton togglePassword;
    private View blurView;
    private Dialog loadingDialog;
    private boolean passwordVisible = false;

    // Constantes pour la validation du mot de passe
    private static final int MIN_PASSWORD_LENGTH = 16;
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{" + MIN_PASSWORD_LENGTH + ",}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnSignIn = findViewById(R.id.btnSignIn);
        toggleLink = findViewById(R.id.toggleLink);
        togglePassword = findViewById(R.id.togglePassword);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        btnSignIn.setOnClickListener(view -> handleSignIn());

        toggleLink.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });

        togglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            updatePasswordVisibility();
        });
    }

    private void showBlurView() {
        blurView.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(300);
        blurView.startAnimation(animation);
    }

    private void hideBlurView() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(300);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                blurView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }
        });
        blurView.startAnimation(animation);
    }

    private void handleSignIn() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("incomplete", "please fill all fields");
            return;
        }
        showLoadingDialog();
        username=signIn(username,password,this);
        hideLoadingDialog();
        if(username==null){
            showErrorDialog("Authentification failed","incorrect username or password");
            return;
        }
        String finalUsername=username;
        Intent intent=new  Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("username", finalUsername);
        startActivity(intent);
        finish();
    }

    private void showErrorDialog(String title, String message) {
        showBlurView();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        titleText.setText(title);
        messageText.setText(message);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
        });

        dialog.setOnDismissListener(dialogInterface -> hideBlurView());
        dialog.show();
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void updatePasswordVisibility() {
        if (passwordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            passwordInput.setTextAppearance(R.font.robocode);
            togglePassword.setImageResource(R.drawable.vissibility_on);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setImageResource(R.drawable.visibility_off);
        }
        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void showLoadingDialog() {
        showBlurView();

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custom_error_dialog);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.setCancelable(false);

        ImageView icon = loadingDialog.findViewById(R.id.dialogIcon);
        TextView titleText = loadingDialog.findViewById(R.id.dialogTitle);
        TextView messageText = loadingDialog.findViewById(R.id.dialogMessage);
        Button btnOk = loadingDialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.user_grey);
        titleText.setVisibility(TextView.GONE);
        messageText.setText("authentification....");
        btnOk.setVisibility(Button.GONE);

        loadingDialog.show();
    }
    private void hideLoadingDialog(){
        loadingDialog.dismiss();
        hideBlurView();
    }
}
