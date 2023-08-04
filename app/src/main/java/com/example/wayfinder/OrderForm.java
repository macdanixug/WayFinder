package com.example.brokers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderForm extends AppCompatActivity {
    Spinner PropertyType;
    EditText firstname, lastname, phone, email,propertyname;
    RadioGroup radioGroup;
    Button submit;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        PropertyType = findViewById(R.id.property_type);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        propertyname = findViewById(R.id.propertyname);
        radioGroup = findViewById(R.id.radioGroup);
        submit = findViewById(R.id.submit);

        String[] propertyType = {"Select Property Type", "Plot", "House"};
        ArrayAdapter<String> busNameAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, propertyType);
        busNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PropertyType.setAdapter(busNameAdapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Lastname = lastname.getText().toString().trim();
                String Firstname = firstname.getText().toString().trim();
                String Phone = phone.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String ProptyName = propertyname.getText().toString().trim();
                String selectedPropertyType = PropertyType.getSelectedItem().toString().trim();

                RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                String selectedOption = selectedRadioButton.getText().toString();
                if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(OrderForm.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedPropertyType.isEmpty() || selectedPropertyType.equals("Select Property Type")) {
                    Toast.makeText(OrderForm.this, "Please select a property type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Firstname.isEmpty()) {
                    firstname.setError("Firstname is required");
                    firstname.requestFocus();
                    return;
                }
                if (Lastname.isEmpty()) {
                    lastname.setError("Lastname is required");
                    lastname.requestFocus();
                    return;
                }
                if (Email.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Valid Email Required");
                    email.requestFocus();
                    return;
                }
                if (Phone.isEmpty()) {
                    phone.setError("Phone is required");
                    phone.requestFocus();
                    return;
                }
                if (ProptyName.isEmpty()) {
                    propertyname.setError("Property Name is required");
                    propertyname.requestFocus();
                    return;
                }
                else{
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference("Orders");
                    String userID = root.push().getKey();
                    PropertyModel model = new PropertyModel(Firstname, Lastname, Phone,Email, selectedPropertyType, ProptyName, selectedOption);
                    FirebaseDatabase.getInstance().getReference("Orders")
                            .child(userID)
                            .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firstname.getText().clear();
                                        lastname.getText().clear();
                                        phone.getText().clear();
                                        email.getText().clear();
                                        propertyname.getText().clear();
                                        radioGroup.clearCheck();
                                        PropertyType.setSelection(0);

                                        firstname.requestFocus();
                                        Toast.makeText(OrderForm.this, "Order Made Successfully", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(OrderForm.this, "Error! Order Request Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }

        });

    }
}