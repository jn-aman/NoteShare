
package com.aman.firebase.app.notesshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.aman.firebase.app.notesshare.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int RC_PHOTO_PICKER =  2;
    public static final int RC_SIGN_IN=1;

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mChildEventListener2;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
private FirebaseStorage mFirebaseStorage;

    public  static String a;
    public static String b;
    public static String c ;
    public static String d;


    ArrayList<String> coll=new ArrayList<String>();
    ArrayList<String> cou=new ArrayList<String>();
    ArrayList<String> semis=new ArrayList<String>();
    ArrayList<String> suba=new ArrayList<String>();





    private StorageReference mChatPhotosStorageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseStorage=FirebaseStorage.getInstance();
        mChatPhotosStorageReference=mFirebaseStorage.getReference().child("chat_photos");
        mUsername = ANONYMOUS;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mMessagesDatabaseReference=mFirebaseDatabase.getReference().child("messages");
        // Initialize references to views











        mMessagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
FriendlyMessage fm=child.getValue(FriendlyMessage.class);

if(!coll.contains(fm.getCollege()))
    coll.add(fm.getCollege());
                    if(!cou.contains(fm.getCourse()))
                    cou.add(fm.getCourse());
                    if(!semis.contains(fm.getSemester()))
                    semis.add(fm.getSemester());
                    if(!suba.contains(fm.getSubject()))
                    suba.add(fm.getSubject());
//Toast.makeText(getApplicationContext(),fm.getCollege(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



//        String [] stringArray = coll.toArray(new String[coll.size()]);



        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.alertdialog_custom_view, null);
        final AutoCompleteTextView college = (AutoCompleteTextView)mView.findViewById(R.id.et_name1);
        final AutoCompleteTextView course = (AutoCompleteTextView)mView.findViewById(R.id.et_name2);
        final AutoCompleteTextView sem = (AutoCompleteTextView)mView.findViewById(R.id.et_name3);
        final AutoCompleteTextView subject = (AutoCompleteTextView)mView.findViewById(R.id.et_name4);



//        String[] countries = getResources().getStringArray(R.array.countries_array);
// Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coll);
        college.setAdapter(adapter);

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cou);
        course.setAdapter(adapter2);


        ArrayAdapter<String> adapter3 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, semis);
        sem.setAdapter(adapter3);

        ArrayAdapter<String> adapter4 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suba);
        subject.setAdapter(adapter4);




        builder.setView(mView)

                // Add action buttons
                .setPositiveButton("Go",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                a = college.getText().toString();
                                b = course.getText().toString();
                                c= sem.getText().toString();
                                d = subject.getText().toString();
    mMessagesDatabaseReference.addChildEventListener(mChildEventListener);




                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();










        mFirebaseAuth=FirebaseAuth.getInstance();



        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click

                // Clear input box
                mMessageEditText.setText("");
            }
        });

// Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null,a,b,c,d);
//                friendlyMessage.setOthers(a,b,c,d);
                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                // Clear input box
                mMessageEditText.setText("");
            }
        });





               mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                List<FriendlyMessage> fm = new ArrayList<FriendlyMessage>();


                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                fm.add(friendlyMessage);

                for (FriendlyMessage element : fm) {
//                    Toast.makeText(getApplicationContext(),element.getCollege(),Toast.LENGTH_SHORT).show();
                    if((element.getCollege().equals(a)))
                        if((element.getCourse().equals(b)))
                            if(element.getSemester().equals(c))
                                if(element.getSubject().equals(d))

                                {                        mMessageAdapter.add(element);

//                                            Toast.makeText(getApplicationContext(),element.getCollege(),Toast.LENGTH_SHORT).show();
                                }

                }


//                for (FriendlyMessage element : fm) {
//                        mMessageAdapter.add(element);
//                }

//                mMessageAdapter.add(friendlyMessage);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

















  mAuthStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          FirebaseUser user=firebaseAuth.getCurrentUser();
          if(user != null )
          {






              onSignedInInitialize(user.getDisplayName());

          }
          else
          {onSignedOutCleanup();
              startActivityForResult(
                  AuthUI.getInstance()
                          .createSignInIntentBuilder()
                          .setLogo(R.drawable.hello)
                          .setAvailableProviders(Arrays.asList(
                                  new AuthUI.IdpConfig.GoogleBuilder().build(),

                                  new AuthUI.IdpConfig.EmailBuilder().build()

                                  ))
                          .build(),
                  RC_SIGN_IN);


          }

      }
  };
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
                default:
                    return super.onOptionsItemSelected(item);

        }

    }

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
    } else if (resultCode == RESULT_CANCELED) {
        Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_SHORT).show();
    } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
        Uri selectedImageUri = data.getData();

        // Get a reference to store file at chat_photos/<FILENAME>
        final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

        photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //When the image has successfully uploaded, get its download URL
                        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dlUri = uri;
                                FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, dlUri.toString(),a,b,c,d);
//                                friendlyMessage.setOthers(a,b,c,d);
                                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                            }
                        });
                    }
                });
    }


    }

@Override
    protected void onPause() {
super.onPause();

    mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    mMessageAdapter.clear();
    if(mChildEventListener!=null) {
        mMessagesDatabaseReference.removeEventListener(mChildEventListener);
        mChildEventListener=null;
    }
    }

@Override
    protected void onResume() {
        super.onResume();
mFirebaseAuth.addAuthStateListener(mAuthStateListener);



    mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            mMessageAdapter.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                FriendlyMessage fm=child.getValue(FriendlyMessage.class);
//if(fm.getPhotoUrl()!=null)
//{


    if((fm.getCollege().equals(a)))
        if((fm.getCourse().equals(b)))
            if(fm.getSemester().equals(c))
                if(fm.getSubject().equals(d))

                {                        mMessageAdapter.add(fm);

//                                            Toast.makeText(getApplicationContext(),element.getCollege(),Toast.LENGTH_SHORT).show();
                }


}

//Toast.makeText(getApplicationContext(),fm.getCollege(), Toast.LENGTH_SHORT).show();
//            }

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    });







    }



    private void onSignedInInitialize(String username ){
mUsername=username;
if(mChildEventListener==null){

//    mMessagesDatabaseReference.addChildEventListener(mChildEventListener);

    }
    }

    private void  onSignedOutCleanup()
    {
mUsername=ANONYMOUS;
mMessageAdapter.clear();
if(mChildEventListener!=null) {
    mMessagesDatabaseReference.removeEventListener(mChildEventListener);
    mChildEventListener=null;
}

    }


}





