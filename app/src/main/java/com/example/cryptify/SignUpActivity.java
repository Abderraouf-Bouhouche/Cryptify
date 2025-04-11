package com.example.cryptify;

import static com.example.cryptify.Functions.signUp;

import static java.lang.Thread.sleep;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button btnSignUp;
    private TextView toggleLink;
    private ImageButton togglePassword, toggleConfirmPassword;
    private View blurView;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    private Dialog loadingDialog;

    // Constantes pour la validation du mot de passe
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{" + MIN_PASSWORD_LENGTH + ",}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.usernameInput);
        //emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        btnSignUp = findViewById(R.id.btnSignUp);
        toggleLink = findViewById(R.id.toggleLink);
        togglePassword = findViewById(R.id.togglePassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);
        blurView = findViewById(R.id.blurView);
    }

    private void setupClickListeners() {
        btnSignUp.setOnClickListener(view -> handleSignUp());

        toggleLink.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });

        togglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            updatePasswordVisibility(passwordInput, togglePassword, passwordVisible);
        });

        toggleConfirmPassword.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            updatePasswordVisibility(confirmPasswordInput, toggleConfirmPassword, confirmPasswordVisible);
        });
    }

    private void handleSignUp() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("incomplete", "please fill all fields");
            return;
        }

        if (!isValidPassword(password)) {
            showErrorDialog("invalid password", "minimum 8 characters\nwith letters and numbers");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("password and confirmation", "doesn't match");
            return;
        }
        showLoadingDialog();
        // TODO: Implémenter la logique d'inscription
        username=signUp(username, password, this);
        hideLoadingDialog();
            if (username == null) {
                showUserExistsDialog();
                return;
            }
            // Simuler un délai pour le chargement
        String finalUsername = username;
                Intent intent=new  Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("username", finalUsername);
                startActivity(intent);
                finish();
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
        messageText.setText("User creation....");
        btnOk.setVisibility(Button.GONE);

        loadingDialog.show();
    }
    private void hideLoadingDialog(){
        loadingDialog.dismiss();
        hideBlurView();
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void updatePasswordVisibility(EditText input, ImageButton toggle, boolean visible) {
        if (visible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            input.setTextAppearance(R.font.robocode);
            toggle.setImageResource(R.drawable.vissibility_on);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggle.setImageResource(R.drawable.visibility_off);
        }
        input.setSelection(input.getText().length());
    }

    private void showUserExistsDialog() {
        showBlurView();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView icon = dialog.findViewById(R.id.dialogIcon);
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        icon.setImageResource(R.drawable.user_grey);
        titleText.setText("user exists");
        messageText.setText("this username\nis already registered");
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            hideBlurView();
        });

        dialog.setOnDismissListener(dialogInterface -> hideBlurView());
        dialog.show();
    }
}
