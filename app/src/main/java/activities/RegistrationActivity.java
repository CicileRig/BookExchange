package activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.bcs.tpdev_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import classes.User;
import controllers.DataBaseManager;

public class RegistrationActivity extends AppCompatActivity {


    private static final String TAG = "EmailPassword";
    private ImageButton datepickerdialogbutton = null;
    private TextView selecteddate= null;
    private Button registerBtn =null;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText adressEditText;
    private EditText passwrdEditText;
    private TextView burthDateEditText;

    private FirebaseAuth mAuth;
    private DataBaseManager dataBaseManager = new DataBaseManager();
    MainActivity activity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //views :
        nameEditText = findViewById(R.id.nameEditText);
        nameEditText.setText("Nom");
        surnameEditText = findViewById(R.id.surnameEditText);
        adressEditText = findViewById(R.id.adressEditText);
        passwrdEditText = findViewById(R.id.passwordEditText);
        burthDateEditText = findViewById(R.id.burthDateEditText);

        // focus change des views :
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus & nameEditText.getText().toString().equals("Nom")) {
                    nameEditText.setText("");
                }
                if (!hasFocus & nameEditText.getText().toString().equals("")){
                    nameEditText.setText("Nom");
                }
            }
        });

        surnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus & surnameEditText.getText().toString().equals("Prénom")) {
                    surnameEditText.setText("");
                }
                if (!hasFocus & surnameEditText.getText().toString().equals("")){
                    surnameEditText.setText("Prénom");
                }
            }
        });

        adressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus & adressEditText.getText().toString().equals("Adresse mail")) {
                    adressEditText.setText("");
                }
                if (!hasFocus & adressEditText.getText().toString().equals("")){
                    adressEditText.setText("Adresse mail");
                }
            }
        });

        passwrdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus & passwrdEditText.getText().toString().equals("Mot de passe")) {
                    passwrdEditText.setText("");
                }
                if (!hasFocus & passwrdEditText.getText().toString().equals("")){
                    passwrdEditText.setText("Mot de passe");
                }
            }
        });


        /********************************        Boite de dialog datepicker           **********************************/

        datepickerdialogbutton = (ImageButton) findViewById(R.id.button1);
        selecteddate = (TextView) findViewById(R.id.burthDateEditText);


        Calendar CurDate = Calendar.getInstance();
        DatePickerDialogClass dpD = new DatePickerDialogClass();

        datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DialogFragment dialogfragment = new DatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");

            }
        });


        /*******************************Validation de l'inscription ********************************/
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(adressEditText.getText().toString(), passwrdEditText.getText().toString());
            }
        });
    }


    public void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            activity.updateUI(user);
                            User myUser = new User(nameEditText.getText().toString(), surnameEditText.getText().toString()
                            , adressEditText.getText().toString(),passwrdEditText.getText().toString(),burthDateEditText.getText().toString());
                            dataBaseManager.writeNewUser(myUser);
                            Intent intent = new Intent(RegistrationActivity.this, ProfilActivity.class);
                            intent.putExtra("user", myUser);
                            startActivity(intent);
                        } else {

                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(TAG, "onComplete: weak_password");
                                Toast.makeText(RegistrationActivity.this, "Le mot de passe doit contenir plus de 6 caracteres",
                                        Toast.LENGTH_SHORT).show();
                                // TODO: take your actions!
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                Toast.makeText(RegistrationActivity.this, "Veuillez introduire une adresse mail valide",
                                        Toast.LENGTH_SHORT).show();

                                // TODO: Take your action
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(TAG, "onComplete: exist_email");
                                Toast.makeText(RegistrationActivity.this, "Cette adresse mail est deja utilisée",
                                        Toast.LENGTH_SHORT).show();

                                // TODO: Take your action
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                            activity.updateUI(null);
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]

    }

    public boolean validateForm() {
        boolean valid = true;

        String email = adressEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            adressEditText.setError("Required.");
            valid = false;
        } else {
            adressEditText.setError(null);
        }

        String password = passwrdEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwrdEditText.setError("Required.");
            valid = false;
        } else {
            passwrdEditText.setError(null);
        }

        return valid;
    }

    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);
            return datepickerdialog;
        }


        public static String getMonthName(int month){
            switch(month+1){
                case 1:
                    return "Janvier";

                case 2:
                    return "Fevrier";

                case 3:
                    return "Mars";

                case 4:
                    return "Avril";

                case 5:
                    return "Mai";

                case 6:
                    return "Join";

                case 7:
                    return "Juillet";

                case 8:
                    return "Aout";

                case 9:
                    return "Septembre";

                case 10:
                    return "Octobre";

                case 11:
                    return "Novembre";

                case 12:
                    return "Decembre";
            }

            return "";
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView textview = getActivity().findViewById(R.id.burthDateEditText);
            textview.setText(day + "  " + getMonthName(month) + "  " + year);
        }
    }

}
